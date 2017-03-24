/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 tools4j.org (Marco Terzer)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.tools4j.spockito;

import java.lang.reflect.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class Converters {

    private static final Pattern UNESCAPED_COMMA = Pattern.compile("(?<=[^\\\\]),");
    private static final Pattern UNESCAPED_COLON = Pattern.compile("(?<=[^\\\\]):");
    private static final Pattern UNESCAPED_EQUAL = Pattern.compile("(?<=[^\\\\])=");

    public static final Function<String, Character> CHAR_CONVERTER = s -> {
        if (s.length() == 1) {
            return s.charAt(0);
        }
        if (s.length() == 3 && s.charAt(0) == '\'' && s.charAt(2) == '\'') {
            return s.charAt(1);
        }
        throw new IllegalArgumentException("Cannot convert string to char: " + s);
    };

    public static final Function<String, String> STRING_CONVERTER = s -> removeStartAndEndChars(s, '\'', '\'');

    public static final Function<String, Date> DATE_CONVERTER = s -> {
        try {
            return DateFormat.getTimeInstance(DateFormat.SHORT).parse(s);
        } catch (final ParseException e) {
            throw new IllegalArgumentException("Cannot convert string to java.util.Date: " + s, e);
        }
    };

    public static final ValueConverter CLASS_CONVERTER = new ValueConverter() {
        @Override
        public <T> T convert(final Class<T> type, final Type genericType, final String value) {
            try {
                final Class<?> clazz = Class.forName(value);
                return type.cast(clazz);
            } catch (final Exception e) {
                throw new IllegalArgumentException("Cannot convert string to " + type.getName() + ": " + value, e);
            }
        }
    };

    public static class CollectionConverter implements ValueConverter {
        private final ValueConverter elementConverter;
        public CollectionConverter(final ValueConverter elementConverter) {
            this.elementConverter = Objects.requireNonNull(elementConverter);
        }

        @Override
        public <T> T convert(final Class<T> type, final Type genericType, final String value) {
            if (!Collection.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException("Type must be a collection: " + type.getName());
            }
            final Class<?> elementType = actualTypeForTypeParam(genericType, 0, 1);
            final List<?> list = toList(elementType, value);
            if (type.isInstance(list)) {
                return type.cast(list);
            }
            if (type.isAssignableFrom(ArrayList.class)) {
                return type.cast(new ArrayList<>(list));
            }
            if (type.isAssignableFrom(LinkedList.class)) {
                return type.cast(new LinkedList<>(list));
            }
            if (type.isAssignableFrom(ArrayDeque.class)) {
                return type.cast(new ArrayDeque<>(list));
            }
            if (type.isAssignableFrom(LinkedHashSet.class)) {
                return type.cast(new LinkedHashSet<>(list));
            }
            if (type.isAssignableFrom(TreeSet.class)) {
                return type.cast(new TreeSet<>(list));
            }
            if (type.isAssignableFrom(HashSet.class)) {
                return type.cast(new HashSet<>(list));
            }
            if (type.isAssignableFrom(EnumSet.class)) {
                return type.cast(enumSet(Enum.class.asSubclass(elementType), list));
            }
            if (type.isAssignableFrom(ConcurrentLinkedQueue.class)) {
                return type.cast(new ConcurrentLinkedQueue<>(list));
            }
            if (type.isAssignableFrom(ConcurrentLinkedDeque.class)) {
                return type.cast(new ConcurrentLinkedDeque<>(list));
            }
            if (type.isAssignableFrom(ConcurrentSkipListSet.class)) {
                return type.cast(new ConcurrentSkipListSet<>(list));
            }
            //unsupported collection type
            throw new IllegalArgumentException("Cannot convert value to " + type.getName() + ": " + value);
        }

        private List<Object> toList(final Class<?> elementType, final String value) {
            final String plainValue = removeStartAndEndChars(value, '[', ']');
            final String[] parts = UNESCAPED_COMMA.split(plainValue);
            final List<Object> list = new ArrayList<>(parts.length);
            for (int i = 0; i < parts.length; i++) {
                list.add(elementConverter.convert(elementType, elementType, parts[i].trim()));
            }
            return list;
        }

        private static <E extends Enum<E>> EnumSet<E> enumSet(final Class<E> enumType, final List<?> list) {
            final EnumSet<E> set = EnumSet.noneOf(enumType);
            list.forEach(v -> set.add(enumType.cast(v)));
            return set;
        }
    }

    public static class MapConverter implements ValueConverter {
        private final ValueConverter elementConverter;
        public MapConverter(final ValueConverter elementConverter) {
            this.elementConverter = Objects.requireNonNull(elementConverter);
        }

        @Override
        public <T> T convert(final Class<T> type, final Type genericType, final String value) {
            final Class<?> keyType = actualTypeForTypeParam(genericType, 0, 2);
            final Class<?> valueType = actualTypeForTypeParam(genericType, 1, 2);
            final Map<?,?> map = toMap(keyType, valueType, value);
            if (type.isInstance(map)) {
                return type.cast(map);
            }
            if (type.isAssignableFrom(LinkedHashMap.class)) {
                return type.cast(new LinkedHashMap<>(map));
            }
            if (type.isAssignableFrom(TreeMap.class)) {
                return type.cast(new TreeMap<>(map));
            }
            if (type.isAssignableFrom(HashMap.class)) {
                return type.cast(new HashMap<>(map));
            }
            if (type.isAssignableFrom(EnumMap.class)) {
                return type.cast(enumMap(Enum.class.asSubclass(keyType), map));
            }
            if (type.isAssignableFrom(Properties.class)) {
                final Properties props = new Properties();
                props.putAll(map);
                return type.cast(props);
            }
            if (type.isAssignableFrom(ConcurrentHashMap.class)) {
                return type.cast(new ConcurrentHashMap<>(map));
            }
            if (type.isAssignableFrom(ConcurrentSkipListMap.class)) {
                return type.cast(new ConcurrentSkipListMap<>(map));
            }
            //unsupported map type
            throw new IllegalArgumentException("Cannot convert value to " + type.getName() + ": " + value);
        }

        private Map<Object, Object> toMap(final Class<?> keyType, final Class<?> valueType, final String value) {
            final String plainValue = removeStartAndEndChars(value, '{', '}');
            final String[] parts = UNESCAPED_COMMA.split(plainValue);
            final Map<Object, Object> map = new LinkedHashMap<>();
            for (int i = 0; i < parts.length; i++) {
                final String[] keyAndValue = parseKeyValue(parts[i].trim());
                if (keyAndValue.length != 2) {
                    throw new IllegalArgumentException("Invalid map key/value pair: " + parts[i]);
                }
                try {
                    final Object key = elementConverter.convert(keyType, keyType, keyAndValue[0].trim());
                    final Object val = elementConverter.convert(valueType, valueType, keyAndValue[1].trim());
                    map.put(key, val);
                } catch (final Exception e) {
                    throw new IllegalArgumentException("Conversion to map key/value failed: " + parts[i], e);
                }
            }
            return map;
        }

        private static <K extends Enum<K>, V> EnumMap<K, V> enumMap(final Class<K> enumType, final Map<?,V> map) {
            final EnumMap<K,V> enumMap = new EnumMap(enumType);
            map.forEach((k,v) -> enumMap.put(enumType.cast(k), v));
            return enumMap;
        }
    }

    public static class BeanConverter implements ValueConverter {

        private final ValueConverter elementConverter;
        public BeanConverter(final ValueConverter elementConverter) {
            this.elementConverter = Objects.requireNonNull(elementConverter);
        }

        @Override
        public <T> T convert(final Class<T> type, final Type genericType, final String value) {
            final T instance = newInstance(type, value);
            final Map<String, Accessor> accessorByName = new LinkedHashMap<>();
            if (hasAccessibleSetters(type)) {
                inspectSetters(type, accessorByName);
            } else {
                inspectFields(type, accessorByName);
            }
            if (accessorByName.isEmpty()) {
                throw new IllegalArgumentException(type.getName() + " is not a bean class, no accessible setters or fields found");
            }
            injectValues(instance, accessorByName, value);
            return instance;
        }

        private <T> T newInstance(final Class<T> type, final String value) {
            try {
                final Constructor<T> constructor = type.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (final Exception e) {
                throw new IllegalArgumentException("Could not instantiate bean " + type.getName(), e);
            }
        }

        private void injectValues(final Object instance, final Map<String, Accessor> accessorByName, final String value) {
            final String plainValue = removeStartAndEndChars(value, '{', '}');
            final String[] parts = UNESCAPED_COMMA.split(plainValue);
            final Map<String, String> valueByName = new LinkedHashMap<>();
            for (int i = 0; i < parts.length; i++) {
                final String[] nameAndValue = parseKeyValue(parts[i].trim());
                if (nameAndValue.length != 2) {
                    throw new IllegalArgumentException("Invalid name/value pair: " + parts[i]);
                }
                final String name = normalizeFieldName(nameAndValue[0].trim());
                final String val = nameAndValue[1].trim();
                valueByName.put(name, val);
            }
            for (final Map.Entry<String, Accessor> e : accessorByName.entrySet()) {
                final String val = valueByName.get(e.getKey());
                if (val == null) {
                    throw new IllegalArgumentException("No value found for bean property " + instance.getClass().getName() + "." + e.getKey());
                }
                try {
                    final Class<?> type = e.getValue().type();
                    final Object convertedVal = elementConverter.convert(type, type, val);
                    e.getValue().set(instance, convertedVal);
                } catch (final Exception ex) {
                    throw new IllegalArgumentException("Could not set bean property " + instance.getClass().getName() + "." + e.getKey() +
                            " to value: " + val, ex);
                }
            }
        }

        public static boolean isBeanClass(final Class<?> clazz) {
            return isInstantiatable(clazz) && (hasAccessibleFields(clazz) || hasAccessibleSetters(clazz));
        }

        private static boolean isInstantiatable(final Class<?> clazz) {
            return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()) && hasDefaultConstructor(clazz);
        }

        private static boolean hasDefaultConstructor(final Class<?> clazz) {
            try {
                clazz.getDeclaredConstructor();
                return true;
            } catch (final Exception e) {
                return false;
            }
        }

        private interface Accessor {
            void set(Object instance, Object value) throws Exception;
            default Class<?> type() {
                return Object.class;
            }
            static Accessor forType(final Class<?> type, final Accessor accessor) {
                return new Accessor() {
                    @Override
                    public void set(final Object instance, final Object value) throws Exception {
                        accessor.set(instance, value);
                    }

                    @Override
                    public Class<?> type() {
                        return type;
                    }
                };
            }
        }

        private static boolean hasAccessibleSetters(final Class<?> clazz) {
            return !inspectSetters(clazz, new HashMap<>()).isEmpty();
        }
        private static final Map<String, Accessor> inspectSetters(final Class<?> clazz, final Map<String, Accessor> accessorByName) {
            if (clazz == null || Object.class.equals(clazz)) {
                return accessorByName;
            }
            for (final Method method : clazz.getDeclaredMethods()) {
                final int mod = method.getModifiers();
                final String name = method.getName();
                if (name.length() > 3 && name.startsWith("set") && method.getParameterCount() == 1 &&
                        !method.isSynthetic() && !Modifier.isStatic(mod) && !Modifier.isPrivate(mod) && !Modifier.isProtected(mod)) {
                    final String propertyName = normalizeFieldName(name.substring(3));
                    accessorByName.put(propertyName, Accessor.forType(method.getParameterTypes()[0], method::invoke));
                }
            }
            return inspectSetters(clazz.getSuperclass(), accessorByName);
        }

        private static boolean hasAccessibleFields(final Class<?> clazz) {
            return !inspectFields(clazz, new HashMap<>()).isEmpty();
        }
        private static final Map<String, Accessor> inspectFields(final Class<?> clazz, final Map<String, Accessor> accessorByName) {
            if (clazz == null || Object.class.equals(clazz)) {
                return accessorByName;
            }
            for (final Field field : clazz.getDeclaredFields()) {
                final int mod = field.getModifiers();
                if (!field.isSynthetic() && !Modifier.isFinal(mod) && !Modifier.isStatic(mod) && !Modifier.isPrivate(mod) && !Modifier.isProtected(mod)) {
                    accessorByName.put(field.getName(), Accessor.forType(field.getType(), field::set));
                }
            }
            return inspectFields(clazz.getSuperclass(), accessorByName);
        }
    }

    private static String normalizeFieldName(final String name) {
        if (name.length() > 0 && Character.isUpperCase(name.charAt(0))) {
            return Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        return name;
    }

    private static final String[] parseKeyValue(final String pair) {
        final String[] split = UNESCAPED_EQUAL.split(pair);
        if (split.length == 1) {
            return UNESCAPED_COLON.split(pair);
        }
        return split;
    }

    private static Class<?> actualTypeForTypeParam(final Type type, final int paramIndex, final int paramCount) {
        if (type instanceof ParameterizedType) {
            final Type[] actualTypeArgs = ((ParameterizedType) type).getActualTypeArguments();
            if (actualTypeArgs.length == paramCount) {
                Type actualType = actualTypeArgs[paramIndex];
                if (actualType instanceof WildcardType) {
                    final Type[] bounds = ((WildcardType) actualType).getLowerBounds();
                    if (bounds.length == 1) {
                        actualType = bounds[0];
                    }
                }
                if (actualType instanceof Class) {
                    return (Class<?>) actualType;
                }
            }
        }
        throw new IllegalArgumentException("Could not derive actual generic type [" + paramIndex + "] for " + type);
    }

    private static String removeStartAndEndChars(final String s, final char startQuoteChar, final char endQuoteChar) {
        final int len = s.length();
        if (len >= 2 && s.charAt(0) == startQuoteChar && s.charAt(len - 1) == endQuoteChar) {
            return s.substring(1, len - 1);
        }
        return s;
    }

    private Converters() {
        throw new RuntimeException("No Converters for you!");
    }
}
