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

import org.tools4j.spockito.table.Converters.CollectionConverter;
import org.tools4j.spockito.table.GenericTypes.ActualType;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.tools4j.spockito.table.GenericTypes.actualTypeForTypeParam;
import static org.tools4j.spockito.table.GenericTypes.genericComponentType;

public class SpockitoTableConverter implements TableConverter {

    private final Class<?> targetClass;
    private final Type targetType;
    private final ValueConverter valueConverter;

    @SuppressWarnings("unused")
    public SpockitoTableConverter(final Class<?> targetClass, final Type targetType) {
        this(targetClass, targetType, SpockitoValueConverter.DEFAULT_INSTANCE);
    }

    public SpockitoTableConverter(final Class<?> targetClass, final Type targetType, final ValueConverter valueConverter) {
        this.targetClass = requireNonNull(targetClass);
        this.targetType = requireNonNull(targetType);
        this.valueConverter = requireNonNull(valueConverter);
    }

    public static TableConverter create(final Parameter parameter, final ValueConverter valueConverter) {
        return new SpockitoTableConverter(parameter.getType(), parameter.getParameterizedType(), valueConverter);
    }

    public static TableConverter create(final Field field, final ValueConverter valueConverter) {
        return new SpockitoTableConverter(field.getType(), field.getGenericType(), valueConverter);
    }

    @Override
    public Object convert(final Table table) {
        requireNonNull(table);
        if (targetClass.isInstance(table)) {
            return table;
        }
        final ActualType rowType;
        if (Collection.class.isAssignableFrom(targetClass)) {
            rowType = actualTypeForTypeParam(targetType, 0, 1);
        } else if (targetClass.isArray()) {
            rowType = genericComponentType(targetClass, targetType);
        } else {
            throw new IllegalArgumentException("No known conversion from Table to " + targetType);
        }
        final List<?> rows = table.toList(rowType.rawType(), rowType.genericType(), valueConverter);
        if (List.class.isAssignableFrom(targetClass)) {
            return targetClass.cast(rows);
        }
        if (Collection.class.isAssignableFrom(targetClass)) {
            return new CollectionConverter(valueConverter).convert(targetClass, targetType, rows);
        }
        if (targetClass.isArray()) {
            return toArray(rows, rowType.rawType());
        }
        //should not get here
        throw new IllegalArgumentException("No known conversion from Table to " + targetType);
    }

    private static Object toArray(final List<?> rows, final Class<?> componentType) {
        final int n = rows.size();
        final Object array = Array.newInstance(componentType, n);
        for (int i = 0; i < n; i++) {
            final Object val = rows.get(i);
            Array.set(array, i, val);
        }
        return array;
    }

}
