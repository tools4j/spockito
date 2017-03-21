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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SpockitoValueConverter implements ValueConverter {

    public static final SpockitoValueConverter DEFAULT_INSTANCE = new SpockitoValueConverter();

    public static final Function<String, Character> CHAR_CONVERTER = s -> {
        if (s.length() == 1) {
            return s.charAt(0);
        }
        if (s.length() == 3 && s.charAt(0) == '\'' && s.charAt(2) == '\'') {
            return s.charAt(1);
        }
        throw new IllegalArgumentException("Cannot convert string to char: " + s);
    };

    public static final Function<String, Date> DATE_CONVERTER = s -> {
        try {
            return DateFormat.getTimeInstance(DateFormat.SHORT).parse(s);
        } catch (final ParseException e) {
            throw new IllegalArgumentException("Cannot convert string to java.util.Date: " + s, e);
        }
    };

    private final Map<Class<?>, Function<? super String, ?>> convertersByType = new HashMap<>();

    public SpockitoValueConverter() {
        initConverterFunctions();
    }

    @Override
    public <T> T convert(final Class<T> type, final String value) {
        if (value == null || type.isInstance(value)) {
            return type.cast(value);
        }
        final Class<T> t = type.isPrimitive() ? (Class<T>)boxingTypeFor(type) : type;
        Function<? super String, ?> converter = convertersByType.get(type);
        if (converter == null && type.isPrimitive()) {
            converter = convertersByType.get(t);
        }
        try {
            if (converter == null) {
                throw new IllegalArgumentException("No value converter is defined for type " + type.getName());
            }
            final Object converted = converter.apply(value);
            return t.cast(converted);
        } catch (final Exception e) {
            throw new IllegalArgumentException("Conversion to " + type.getName() + " failed for value: " + value, e);
        }
    }

    protected void initConverterFunctions() {
        addConverterFunction(Long.class, Long::valueOf);
        addConverterFunction(Integer.class, Integer::valueOf);
        addConverterFunction(Short.class, Short::valueOf);
        addConverterFunction(Byte.class, Byte::valueOf);
        addConverterFunction(Double.class, Double::valueOf);
        addConverterFunction(Float.class, Float::valueOf);
        addConverterFunction(Character.class, CHAR_CONVERTER);
        addConverterFunction(Boolean.class, Boolean::valueOf);
        addConverterFunction(BigInteger.class, BigInteger::new);
        addConverterFunction(BigDecimal.class, BigDecimal::new);
        addConverterFunction(LocalDate.class, LocalDate::parse);
        addConverterFunction(LocalTime.class, LocalTime::parse);
        addConverterFunction(LocalDateTime.class, LocalDateTime::parse);
        addConverterFunction(ZonedDateTime.class, ZonedDateTime::parse);
        addConverterFunction(Instant.class, Instant::parse);
        addConverterFunction(Date.class, DATE_CONVERTER);
        addConverterFunction(java.sql.Date.class, java.sql.Date::valueOf);
        addConverterFunction(java.sql.Time.class, java.sql.Time::valueOf);
        addConverterFunction(java.sql.Timestamp.class, java.sql.Timestamp::valueOf);
    }

    protected <T> void addConverterFunction(final Class<T> type, final Function<? super String, ? extends T> converter) {
        convertersByType.put(type, converter);
    }

    private static <T> Class<T> boxingTypeFor(final Class<?> type) {
        if (double.class.equals(type)) {
            return (Class<T>)Double.class;
        }
        if (float.class.equals(type)) {
            return (Class<T>)Float.class;
        }
        if (long.class.equals(type)) {
            return (Class<T>)Long.class;
        }
        if (int.class.equals(type)) {
            return (Class<T>)Integer.class;
        }
        if (short.class.equals(type)) {
            return (Class<T>)Short.class;
        }
        if (byte.class.equals(type)) {
            return (Class<T>)Byte.class;
        }
        if (boolean.class.equals(type)) {
            return (Class<T>)Boolean.class;
        }
        if (char.class.equals(type)) {
            return (Class<T>)Character.class;
        }
        if (void.class.equals(type)) {
            return (Class<T>)Void.class;
        }
        throw new IllegalArgumentException("Not a primitive type: " + type.getName());
    }
}
