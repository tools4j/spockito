/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2022 tools4j.org (Marco Terzer)
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
package org.tools4j.spockito.table;

import org.tools4j.spockito.table.GenericTypes.ActualType;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.tools4j.spockito.table.GenericTypes.actualTypeForTypeParam;
import static org.tools4j.spockito.table.GenericTypes.genericComponentType;
import static org.tools4j.spockito.table.GenericTypes.genericListType;

/**
 * Contains conversion functions and value converters used by {@link SpockitoValueConverter}.
 */
public final class Converters {

    public static final Function<? super String, Object> OBJECT_CONVERTER = Function.identity();
    public static final Function<? super String, Long> LONG_CONVERTER = Long::valueOf;
    public static final Function<? super String, Integer> INTEGER_CONVERTER = Integer::valueOf;
    public static final Function<? super String, Short> SHORT_CONVERTER = Short::valueOf;
    public static final Function<? super String, Byte> BYTE_CONVERTER = Byte::valueOf;
    public static final Function<? super String, Double> DOUBLE_CONVERTER = Double::valueOf;
    public static final Function<? super String, Float> FLOAT_CONVERTER = Float::valueOf;
    public static final Function<? super String, Boolean> BOOLEAN_CONVERTER = Boolean::valueOf;

    public static final Function<? super String, BigInteger> BIG_INTEGER_CONVERTER = BigInteger::new;
    public static final Function<? super String, BigDecimal> BIG_DECIMAL_CONVERTER = BigDecimal::new;

    public static final Function<? super String, LocalDate> LOCAL_DATE_CONVERTER = LocalDate::parse;
    public static final Function<? super String, LocalTime> LOCAL_TIME_CONVERTER = LocalTime::parse;
    public static final Function<? super String, LocalDateTime> LOCAL_DATE_TIME_CONVERTER = LocalDateTime::parse;
    public static final Function<? super String, OffsetTime> OFFSET_TIME_CONVERTER = OffsetTime::parse;
    public static final Function<? super String, OffsetDateTime> OFFSET_DATE_TIME_CONVERTER = OffsetDateTime::parse;
    public static final Function<? super String, ZonedDateTime> ZONED_DATE_TIME_CONVERTER = ZonedDateTime::parse;
    public static final Function<? super String, Instant> INSTANT_CONVERTER = Instant::parse;
    public static final Function<? super String, Month> MONTH_CONVERTER = s -> {
        //try numeric
        if (s.length() == 1 || s.length()== 2) {
            final int digit = s.charAt(0) - '0';
            if (s.length() == 1 && digit >= 1 && digit <= 9) {
                return Month.of(digit);
            }
            if (s.length() == 2 && digit == 1) {
                final int digit2 = s.charAt(1) - '0';
                if (digit2 >= 0 && digit2 <= 2) {
                    return Month.of(10 + digit2);
                }
            }
        }
        //try by name (or prefix of name)
        if (s.length() >= 3) {
            final String upper = s.toUpperCase();
            for (final Month month : Month.values()) {
                if (month.name().startsWith(upper)) {
                    return month;
                }
            }
        }
        throw new IllegalArgumentException("Cannot convert string to Month: " + s);
    };
    public static final Function<? super String, Year> YEAR_CONVERTER = Year::parse;
    public static final Function<? super String, YearMonth> YEAR_MONTH_CONVERTER = YearMonth::parse;
    public static final Function<? super String, MonthDay> MONTH_DAY_CONVERTER = MonthDay::parse;
    public static final Function<? super String, DayOfWeek> DAY_OF_WEEK_CONVERTER = new EnumNamePrefixConverter<>(DayOfWeek.class, 3, false);

    public static final Function<? super String, Duration> DURATION_CONVERTER = Duration::parse;
    public static final Function<? super String, Period> PERIOD_CONVERTER = Period::parse;
    public static final Function<? super String, ZoneOffset> ZONE_OFFSET_CONVERTER = ZoneOffset::of;
    public static final Function<? super String, ZoneId> ZONE_ID_CONVERTER = ZoneId::of;
    public static final Function<? super String, DateTimeFormatter> DATE_TIME_FORMATTER_CONVERTER = DateTimeFormatter::ofPattern;

    public static final Function<? super String, Date> DATE_CONVERTER = s -> Date.from(Timestamp.valueOf(s).toInstant());
    public static final Function<? super String, java.sql.Date> SQL_DATE_CONVERTER = java.sql.Date::valueOf;
    public static final Function<? super String, Time> SQL_TIME_CONVERTER = Time::valueOf;
    public static final Function<? super String, Timestamp> SQL_TIMESTAMP_CONVERTER = Timestamp::valueOf;
    public static final Function<? super String, TimeUnit> TIME_UNIT_CONVERTER = timeUnitConverter();
    private static Function<? super String, TimeUnit> timeUnitConverter() {
        final Map<String, TimeUnit> unitsByName = new HashMap<>();
        unitsByName.put("ns", TimeUnit.NANOSECONDS);
        unitsByName.put("us", TimeUnit.MICROSECONDS);
        unitsByName.put("ms", TimeUnit.MILLISECONDS);
        unitsByName.put("s", TimeUnit.SECONDS);
        unitsByName.put("m", TimeUnit.MINUTES);
        unitsByName.put("h", TimeUnit.HOURS);
        unitsByName.put("d", TimeUnit.DAYS);
        final EnumNamePrefixConverter<TimeUnit> unitsByPrefix = new EnumNamePrefixConverter<>(TimeUnit.class, 3, false);
        return s -> {
            final TimeUnit value = unitsByName.get(s);
            if (value != null) {
                return value;
            }
            return unitsByPrefix.apply(s);
        };
    }

    public static final Function<? super String, Pattern> PATTERN_CONVERTER = Pattern::compile;

    public static final Function<? super String, String> STRING_CONVERTER = s -> Strings.removeStartAndEndChars(s, '\'', '\'');
    public static final Function<? super String, StringBuilder> STRING_BUILDER_CONVERTER = s -> new StringBuilder(STRING_CONVERTER.apply(s));
    public static final Function<? super String, StringBuffer> STRING_BUFFER_CONVERTER = s -> new StringBuffer(STRING_CONVERTER.apply(s));
    public static final Function<? super String, Character> CHAR_CONVERTER = s -> {
        if (s.length() == 1) {
            return s.charAt(0);
        }
        if (s.length() == 3 && s.charAt(0) == '\'' && s.charAt(2) == '\'') {
            return s.charAt(1);
        }
        throw new IllegalArgumentException("Cannot convert string to char: " + s);
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
    public static final ValueConverter ENUM_CONVERTER = new ValueConverter() {
        @Override
        public <T> T convert(final Class<T> type, final Type genericType, final String value) {
            final Enum<?> e = Enum.valueOf(type.asSubclass(Enum.class), value);
            return type.cast(e);
        }
    };

    public static class EnumNamePrefixConverter<E extends Enum<E>> implements Function<String, E> {
        private final Class<E> enumType;
        private final int prefixMinLength;
        private final boolean caseSensitive;
        private final Map<String, E> valuesPyPrefix;

        public EnumNamePrefixConverter(final Class<E> enumType,
                                       final int prefixMinLength,
                                       final boolean caseSensitive) {
            this.enumType = requireNonNull(enumType);
            this.prefixMinLength = prefixMinLength;
            this.caseSensitive = caseSensitive;
            final E[] values = enumType.getEnumConstants();
            this.valuesPyPrefix = Arrays.stream(values).collect(Collectors.toMap(
                    caseSensitive ?
                            e -> e.name().substring(0, prefixMinLength) :
                            e -> e.name().substring(0, prefixMinLength).toUpperCase(),
                    Function.identity()
            ));
            if (valuesPyPrefix.size() != values.length) {
                throw new IllegalArgumentException("Enum name prefixes of length " + prefixMinLength +
                        " are not unique for " + enumType.getName());
            }
        }

        @Override
        public E apply(final String s) {
            if (s.length() >= prefixMinLength) {
                final String key = caseSensitive ? s : s.toUpperCase();
                final E candidate = valuesPyPrefix.get(key.substring(0, prefixMinLength));
                if (candidate != null && candidate.name().startsWith(key)) {
                    return candidate;
                }
            }
            throw new IllegalArgumentException("Cannot convert string to " + enumType.getName() + ": " + s);
        }
    }

    /**
     * Value converter for target type {@link Optional}.
     */
    public static class OptionalConverter implements ValueConverter {
        private final ValueConverter elementConverter;

        public OptionalConverter(final ValueConverter elementConverter) {
            this.elementConverter = requireNonNull(elementConverter);
        }

        @Override
        public <T> T convert(final Class<T> type, final Type genericType, final String value) {
            if (!Optional.class.equals(type)) {
                throw new IllegalArgumentException("Type must be Optional: " + type.getName());
            }
            final String trimmed = value.trim();
            final Object elementValue;
            if ("empty".equals(trimmed) || trimmed.isEmpty()) {
                elementValue = null;
            } else {
                final ActualType elementType = actualTypeForTypeParam(genericType, 0, 1);
                elementValue = elementConverter.convert(elementType.rawType(), elementType.genericType(), trimmed);
            }
            return type.cast(Optional.ofNullable(elementValue));
        }

    }

    /**
     * Value converter for target type {@link Collection} or sub-interfaces and implementations of it.
     */
    public static class CollectionConverter implements ValueConverter {
        private final ValueConverter elementConverter;
        public CollectionConverter(final ValueConverter elementConverter) {
            this.elementConverter = requireNonNull(elementConverter);
        }

        @Override
        public <T> T convert(final Class<T> type, final Type genericType, final String value) {
            if (!Collection.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException("Type must be a collection: " + type.getName());
            }
            final ActualType elementType = actualTypeForTypeParam(genericType, 0, 1);
            final List<?> list = toList(elementType, value);
            return convert(type, genericType, list, value);
        }

        public <T> T convert(final Class<T> type, final Type genericType, final List<?> list) {
            return convert(type, genericType, list, list);
        }

        public <T> T convert(final Class<T> type, final Type genericType, final List<?> list, final Object value) {
            if (type.isInstance(list)) {
                return type.cast(list);
            }
            if (type.isAssignableFrom(ArrayList.class)) {
                return type.cast(new ArrayList<>(list));
            }
            if (type.isAssignableFrom(Vector.class)) {
                return type.cast(new Vector<>(list));
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
                final ActualType elementType = actualTypeForTypeParam(genericType, 0, 1);
                final EnumSet<?> enumSet = enumSet(elementType.rawType().asSubclass(Enum.class), list);
                return type.cast(enumSet);
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

        private List<Object> toList(final ActualType elementType, final String value) {
            final String plainValue = Strings.removeStartAndEndChars(value, '[', ']');
            if (plainValue.trim().isEmpty()) {
                return Collections.emptyList();
            }
            final String[] parts = parseListValues(plainValue);
            final List<Object> list = new ArrayList<>(parts.length);
            for (final String part : parts) {
                list.add(elementConverter.convert(elementType.rawType(), elementType.genericType(), part.trim()));
            }
            return list;
        }

        private static <E extends Enum<E>> EnumSet<E> enumSet(final Class<E> enumType, final List<?> list) {
            final EnumSet<E> set = EnumSet.noneOf(enumType);
            list.forEach(v -> set.add(enumType.cast(v)));
            return set;
        }
    }

    /**
     * Value converter for array target types including primitive arrays.
     */
    public static class ArrayConverter implements ValueConverter {
        private final CollectionConverter collectionConverter;
        public ArrayConverter(final ValueConverter elementConverter) {
            this.collectionConverter = new CollectionConverter(elementConverter);
        }

        @Override
        public <T> T convert(final Class<T> type, final Type genericType, final String value) {
            if (!type.isArray()) {
                throw new IllegalArgumentException("Type must be an array: " + type.getName());
            }
            final ActualType componentType = genericComponentType(type, genericType);
            final Type genericListType = genericListType(componentType.genericType());
            final List<?> list = collectionConverter.convert(List.class, genericListType, value);
            final Object array = Array.newInstance(componentType.rawType(), list.size());
            for (int i = 0; i < list.size(); i++) {
                final Object val = list.get(i);
                Array.set(array, i, val);
            }
            return type.cast(array);
        }
    }

    /**
     * Value converter for target type {@link Map} or sub-interfaces and implementations of it.
     */
    public static class MapConverter implements ValueConverter {
        private final ValueConverter elementConverter;
        public MapConverter(final ValueConverter elementConverter) {
            this.elementConverter = requireNonNull(elementConverter);
        }

        @Override
        public <T> T convert(final Class<T> type, final Type genericType, final String value) {
            final ActualType keyType = actualTypeForTypeParam(genericType, 0, 2);
            final ActualType valueType = actualTypeForTypeParam(genericType, 1, 2);
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
            if (type.isAssignableFrom(Hashtable.class)) {
                return type.cast(new Hashtable<>(map));
            }
            if (type.isAssignableFrom(EnumMap.class)) {
                final EnumMap<?,?> enumMap = enumMap(keyType.rawType().asSubclass(Enum.class), map);
                return type.cast(enumMap);
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

        private Map<Object, Object> toMap(final ActualType keyType, final ActualType valueType, final String value) {
            final String plainValue = Strings.removeStartAndEndChars(value, '{', '}');
            if (plainValue.trim().isEmpty()) {
                return Collections.emptyMap();
            }
            final String[] parts = parseListValues(plainValue);
            final Map<Object, Object> map = new LinkedHashMap<>();
            for (final String part : parts) {
                final String[] keyAndValue = parseKeyValue(part.trim());
                if (keyAndValue.length != 2) {
                    throw new IllegalArgumentException("Invalid map key/value pair: " + part);
                }
                try {
                    final Object key = elementConverter.convert(keyType.rawType(), keyType.genericType(), keyAndValue[0].trim());
                    final Object val = elementConverter.convert(valueType.rawType(), valueType.genericType(), keyAndValue[1].trim());
                    map.put(key, val);
                } catch (final Exception e) {
                    throw new IllegalArgumentException("Conversion to map key/value failed: " + part, e);
                }
            }
            return map;
        }

        private static <K extends Enum<K>, V> EnumMap<K, V> enumMap(final Class<K> enumType, final Map<?,V> map) {
            final EnumMap<K,V> enumMap = new EnumMap<>(enumType);
            map.forEach((k,v) -> enumMap.put(enumType.cast(k), v));
            return enumMap;
        }
    }

    /**
     * Value converter for Java beans as target type. The beans either have getters and setters to access the fields
     * or otherwise fields are accessed directly.
     */
    public static class BeanConverter implements ValueConverter {

        private final ValueConverter elementConverter;
        public BeanConverter(final ValueConverter elementConverter) {
            this.elementConverter = requireNonNull(elementConverter);
        }

        @Override
        public boolean isMultiValueType(final Class<?> type, final Type genericType) {
            return true;
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

        private <T> T newInstance(final Class<T> type, final String ignoredValue) {
            try {
                final Constructor<T> constructor = type.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (final Exception e) {
                throw new IllegalArgumentException("Could not instantiate bean " + type.getName(), e);
            }
        }

        private void injectValues(final Object instance, final Map<String, Accessor> accessorByName, final String value) {
            final String plainValue = Strings.removeStartAndEndChars(value, '{', '}');
            final String[] parts = parseListValues(plainValue);
            final Map<String, String> valueByName = new LinkedHashMap<>();
            for (final String part : parts) {
                final String[] nameAndValue = parseKeyValue(part.trim());
                if (nameAndValue.length != 2) {
                    throw new IllegalArgumentException("Invalid name/value pair: " + part);
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
                    final Type genericType = e.getValue().genericType();
                    final Object convertedVal = elementConverter.convert(type, genericType, val);
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
            default Type genericType() {
                return Object.class;
            }
            static Accessor forType(final Class<?> type, final Type genericType, final Accessor accessor) {
                return new Accessor() {
                    @Override
                    public void set(final Object instance, final Object value) throws Exception {
                        accessor.set(instance, value);
                    }

                    @Override
                    public Class<?> type() {
                        return type;
                    }

                    @Override
                    public Type genericType() {
                        return genericType;
                    }
                };
            }
        }

        private static boolean hasAccessibleSetters(final Class<?> clazz) {
            return !inspectSetters(clazz, new HashMap<>()).isEmpty();
        }
        private static Map<String, Accessor> inspectSetters(final Class<?> clazz, final Map<String, Accessor> accessorByName) {
            if (clazz == null || Object.class.equals(clazz)) {
                return accessorByName;
            }
            for (final Method method : clazz.getDeclaredMethods()) {
                final int mod = method.getModifiers();
                final String name = method.getName();
                if (name.length() > 3 && name.startsWith("set") && method.getParameterCount() == 1 &&
                        !method.isSynthetic() && !Modifier.isStatic(mod) && !Modifier.isPrivate(mod) && !Modifier.isProtected(mod)) {
                    final String propertyName = normalizeFieldName(name.substring(3));
                    accessorByName.put(propertyName, Accessor.forType(method.getParameterTypes()[0], method.getGenericParameterTypes()[0], method::invoke));
                }
            }
            return inspectSetters(clazz.getSuperclass(), accessorByName);
        }

        private static boolean hasAccessibleFields(final Class<?> clazz) {
            return !inspectFields(clazz, new HashMap<>()).isEmpty();
        }
        private static Map<String, Accessor> inspectFields(final Class<?> clazz, final Map<String, Accessor> accessorByName) {
            if (clazz == null || Object.class.equals(clazz)) {
                return accessorByName;
            }
            for (final Field field : clazz.getDeclaredFields()) {
                final int mod = field.getModifiers();
                if (!field.isSynthetic() && !Modifier.isFinal(mod) && !Modifier.isStatic(mod) && !Modifier.isPrivate(mod) && !Modifier.isProtected(mod)) {
                    accessorByName.put(field.getName(), Accessor.forType(field.getType(), field.getGenericType(), field::set));
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

    private static String[] parseKeyValue(final String pair) {
        return Strings.split(pair, Strings.UNESCAPED_EQUAL, Strings.UNESCAPED_COLON);
    }

    private static String[] parseListValues(final String pair) {
        return Strings.split(pair, Strings.UNESCAPED_COMMA, Strings.UNESCAPED_SEMICOLON);
    }

    private Converters() {
        throw new RuntimeException("No Converters for you!");
    }
}
