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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class SpockitoValueConverter implements ValueConverter {

    public static final SpockitoValueConverter DEFAULT_INSTANCE = new SpockitoValueConverter();

    private final Map<Class<?>, Function<? super String, ?>> convertersByType = new HashMap<>();
    private final List<Map.Entry<BiPredicate<Class<?>, ? super Type>, ValueConverter>> convertersByPredicate = new ArrayList<>();

    public SpockitoValueConverter() {
        initConverterFunctions();
    }

    @Override
    public <T> T convert(final Class<T> type, final Type genericType, final String value) {
        if (value == null || "null".equals(value)) {
            return null;
        }
        ValueConverter converter = converterByTypeOrNull(type);;
        if (converter == null) {
            converter = converterByPredicateOrNull(type, genericType);
        }
        if (converter == null) {
            throw new IllegalArgumentException("No value converter is defined for type " + typeName(type, genericType));
        }
        try {
            return converter.convert(type, genericType, value);
        } catch (final Exception e) {
            throw new IllegalArgumentException("Conversion to " + typeName(type, genericType) + " failed for value: " + value, e);
        }
    }

    protected void initConverterFunctions() {
        addConverterFunction(Object.class, Function.identity());
        addConverterFunction(String.class, Converters.STRING_CONVERTER);
        addConverterFunction(long.class, Long::valueOf);
        addConverterFunction(int.class, Integer::valueOf);
        addConverterFunction(short.class, Short::valueOf);
        addConverterFunction(byte.class, Byte::valueOf);
        addConverterFunction(double.class, Double::valueOf);
        addConverterFunction(float.class, Float::valueOf);
        addConverterFunction(char.class, Converters.CHAR_CONVERTER);
        addConverterFunction(boolean.class, Boolean::valueOf);
        addConverterFunction(BigInteger.class, BigInteger::new);
        addConverterFunction(BigDecimal.class, BigDecimal::new);
        addConverterFunction(LocalDate.class, LocalDate::parse);
        addConverterFunction(LocalTime.class, LocalTime::parse);
        addConverterFunction(LocalDateTime.class, LocalDateTime::parse);
        addConverterFunction(ZonedDateTime.class, ZonedDateTime::parse);
        addConverterFunction(Instant.class, Instant::parse);
        addConverterFunction(Date.class, Converters.DATE_CONVERTER);
        addConverterFunction(java.sql.Date.class, java.sql.Date::valueOf);
        addConverterFunction(java.sql.Time.class, java.sql.Time::valueOf);
        addConverterFunction(java.sql.Timestamp.class, java.sql.Timestamp::valueOf);
        addConverterFunction(StringBuilder.class, StringBuilder::new);
        addConverterFunction(StringBuffer.class, StringBuffer::new);

        addConverterFunction((t,g) -> t.isEnum(), converter((t, g, s) -> Enum.valueOf(t.asSubclass(Enum.class), s)));
        addConverterFunction((t,g) -> Collection.class.isAssignableFrom(t), new Converters.CollectionConverter(this));
        addConverterFunction((t,g) -> Map.class.isAssignableFrom(t), new Converters.MapConverter(this));
        addConverterFunction((t,g) -> Class.class.isAssignableFrom(t), Converters.CLASS_CONVERTER);
        addConverterFunction((t,g) -> Converters.BeanConverter.isBeanClass(t), new Converters.BeanConverter(this));
    }

    protected <T> void addConverterFunction(final Class<T> type, final Function<? super String, ? extends T> converter) {
        convertersByType.put(type, converter);
        if (type.isPrimitive()) {
            convertersByType.put(Primitives.boxingTypeFor(type), converter);
        }
    }

    protected void addConverterFunction(final BiPredicate<Class<?>, ? super Type> applicable, final ValueConverter converter) {
        convertersByPredicate.add(new AbstractMap.SimpleImmutableEntry<>(applicable, converter));
    }

    private ValueConverter converterByTypeOrNull(final Class<?> type) {
        Function<? super String, ?> function = convertersByType.get(type);
        if (function == null && type.isPrimitive()) {
            function = convertersByType.get(type);
        }
        if (function != null) {
            final Function<? super String, ?> f = function;
            return new ValueConverter() {
                @Override
                public <T> T convert(final Class<T> type, final Type genericType, final String value) {
                    final Class<T> boxingType = type.isPrimitive() ? Primitives.boxingTypeFor(type) : type;
                    return boxingType.cast(f.apply(value));
                }
            };
        }
        return null;
    }

    private ValueConverter converterByPredicateOrNull(final Class<?> type, final Type genericType) {
        return convertersByPredicate.stream()
                .filter(e -> e.getKey().test(type, genericType))
                .findFirst()
                .map(e -> e == null ? null : e.getValue())
                .orElse(null);
    }

    private interface TriFunction<T, U, V, W> {
        W apply(T t, U u, V v);
    }
    private static ValueConverter converter(final TriFunction<Class<?>, Type, String, Object> converter) {
        return new ValueConverter() {
            @Override
            public <T> T convert(final Class<T> type, final Type genericType, final String value) {
                return type.cast(converter.apply(type, genericType, value));
            }
        };
    }

    private static String typeName(final Class<?> type, final Type genericType) {
        return type == genericType ? type.getName() : genericType.toString();
    }

}
