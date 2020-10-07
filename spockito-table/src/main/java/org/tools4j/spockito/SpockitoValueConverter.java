/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2020 tools4j.org (Marco Terzer)
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Default value converter implementation for Spockito. All supported conversions are defined as a constant in
 * {@link Converters}.
 * <p>
 * Constom value converter implementations may want to extend this class and add additional converters by overriding
 * {@link #initConverterFunctions()}. Converters for single types can be registered via
 * {@link #registerConverterFunction(Class, Function)} and the more generic
 * {@link #registerConverter(BiPredicate, ValueConverter)}.
 */
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

    /**
     * Registers converters for individual types via calls to
     * {@link #registerConverterFunction(Class, Function)} and the more generic
     * {@link #registerConverter(BiPredicate, ValueConverter)}. All converters registered by this method are defined by
     * {@link Converters}.
     */
    protected void initConverterFunctions() {
        registerConverterFunction(Object.class, Converters.OBJECT_CONVERTER);
        registerConverterFunction(String.class, Converters.STRING_CONVERTER);
        registerConverterFunction(long.class, Converters.LONG_CONVERTER);
        registerConverterFunction(int.class, Converters.INTEGER_CONVERTER);
        registerConverterFunction(short.class, Converters.SHORT_CONVERTER);
        registerConverterFunction(byte.class, Converters.BYTE_CONVERTER);
        registerConverterFunction(double.class, Converters.DOUBLE_CONVERTER);
        registerConverterFunction(float.class, Converters.FLOAT_CONVERTER);
        registerConverterFunction(char.class, Converters.CHAR_CONVERTER);
        registerConverterFunction(boolean.class, Converters.BOOLEAN_CONVERTER);
        registerConverterFunction(BigInteger.class, Converters.BIG_INTEGER_CONVERTER);
        registerConverterFunction(BigDecimal.class, Converters.BIG_DECIMAL_CONVERTER);
        registerConverterFunction(LocalDate.class, Converters.LOCAL_DATE_CONVERTER);
        registerConverterFunction(LocalTime.class, Converters.LOCAL_TIME_CONVERTER);
        registerConverterFunction(LocalDateTime.class, Converters.LOCAL_DATE_TIME_CONVERTER);
        registerConverterFunction(ZonedDateTime.class, Converters.ZONED_DATE_TIME_CONVERTER);
        registerConverterFunction(OffsetDateTime.class, Converters.OFFSET_DATE_TIME_CONVERTER);
        registerConverterFunction(Instant.class, Converters.INSTANT_CONVERTER);
        registerConverterFunction(Date.class, Converters.DATE_CONVERTER);
        registerConverterFunction(java.sql.Date.class, Converters.SQL_DATE_CONVERTER);
        registerConverterFunction(java.sql.Time.class, Converters.SQL_TIME_CONVERTER);
        registerConverterFunction(java.sql.Timestamp.class, Converters.SQL_TIMESTAMP_CONVERTER);
        registerConverterFunction(StringBuilder.class, Converters.STRING_BUILDER_CONVERTER);
        registerConverterFunction(StringBuffer.class, Converters.STRING_BUFFER_CONVERTER);

        registerConverter((t, g) -> t.isEnum(), Converters.ENUM_CONVERTER);
        registerConverter((t, g) -> t == Optional.class, new Converters.OptionalConverter(this));
        registerConverter((t, g) -> t.isArray(), new Converters.ArrayConverter(this));
        registerConverter((t, g) -> Class.class.isAssignableFrom(t), Converters.CLASS_CONVERTER);
        registerConverter((t, g) -> Collection.class.isAssignableFrom(t), new Converters.CollectionConverter(this));
        registerConverter((t, g) -> Map.class.isAssignableFrom(t), new Converters.MapConverter(this));
        registerConverter((t, g) -> Converters.BeanConverter.isBeanClass(t), new Converters.BeanConverter(this));
    }

    /**
     * Register a conversion fuction for the specified type. The function is applied for types exactly matching the
     * given type only (but not to supertypes of this type).
     *
     * @param type          the target type
     * @param converter     a function converting from string to target type
     * @param <T>           the targe parameter
     */
    protected <T> void registerConverterFunction(final Class<T> type, final Function<? super String, ? extends T> converter) {
        convertersByType.put(type, converter);
        if (type.isPrimitive()) {
            convertersByType.put(Primitives.boxingTypeFor(type), converter);
        }
    }

    /**
     * Register a value converter for some subgroup of types selected for by the given predicate. The two arguments
     * passed to the applicable predicate are type and generic type of the target value. The predicate returns true if
     * the specified value converter is applicable for such a value.
     * <p>
     * Value converters registered via this method will be queried in registration order. If two converters are
     * applicable to some value, the first whose predicate returns true will be used.
     *
     * @param applicable    a predicate returning true if the specified converter can be applied for a value of the
     *                      type/generic-type passed to the predicate, and false otherwise
     * @param converter     a value converter from string to target type
     */
    protected void registerConverter(final BiPredicate<Class<?>, ? super Type> applicable, final ValueConverter converter) {
        convertersByPredicate.add(new AbstractMap.SimpleImmutableEntry<>(applicable, converter));
    }

    private ValueConverter converterByTypeOrNull(final Class<?> type) {
        final Function<? super String, ?> function = convertersByType.get(type);
        if (function != null) {
            return new ValueConverter() {
                @Override
                public <T> T convert(final Class<T> type, final Type genericType, final String value) {
                    final Class<T> boxingType = type.isPrimitive() ? Primitives.boxingTypeFor(type) : type;
                    return boxingType.cast(function.apply(value));
                }
            };
        }
        return null;
    }

    private ValueConverter converterByPredicateOrNull(final Class<?> type, final Type genericType) {
        return convertersByPredicate.stream()
                .filter(e -> e.getKey().test(type, genericType))
                .findFirst()
                .map(Entry::getValue)
                .orElse(null);
    }

    private static String typeName(final Class<?> type, final Type genericType) {
        return type == genericType || genericType == null ? type.getName() : genericType.toString();
    }

}
