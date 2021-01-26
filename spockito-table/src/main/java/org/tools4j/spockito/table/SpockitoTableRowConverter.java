/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2021 tools4j.org (Marco Terzer)
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

import org.tools4j.spockito.table.InjectionContext.Phase;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import static java.util.Objects.requireNonNull;
import static org.tools4j.spockito.table.SpockitoAnnotations.annotationDirectOrMeta;

public class SpockitoTableRowConverter implements TableRowConverter {

    private final Data dataOrNull;
    private final AnnotatedElement annotatedElementOrNull;
    private final String nameOrNull;
    private final int index;
    private final Class<?> targetClass;
    private final Type targetType;
    private final ValueConverter valueConverter;

    public SpockitoTableRowConverter(final Data dataOrNull,
                                     final AnnotatedElement annotatedElementOrNull,
                                     final String nameOrNull,
                                     final int index,
                                     final Class<?> targetClass,
                                     final Type targetType,
                                     final ValueConverter valueConverter) {
        this.dataOrNull = dataOrNull;
        this.annotatedElementOrNull = annotatedElementOrNull;
        this.nameOrNull = nameOrNull;
        this.index = index;
        this.targetClass = requireNonNull(targetClass);
        this.targetType = requireNonNull(targetType);
        this.valueConverter = requireNonNull(valueConverter);
    }

    public static TableRowConverter create(final Parameter parameter, final int index, final ValueConverter valueConverter) {
        return new SpockitoTableRowConverter(annotationDirectOrMeta(parameter, Data.class), parameter,
                parameterNameIfPresent(parameter), index, parameter.getType(), parameter.getParameterizedType(), valueConverter);
    }

    public static TableRowConverter create(final Field field, final ValueConverter valueConverter) {
        return new SpockitoTableRowConverter(null, field, field.getName(), -1, field.getType(),
                field.getGenericType(), valueConverter);
    }

    public static TableRowConverter create(final Class<?> rowType, final ValueConverter valueConverter) {
            return new SpockitoTableRowConverter(null, null, null, -1,
                    rowType, rowType, valueConverter);
    }

    private static String parameterNameIfPresent(final Parameter parameter) {
        return parameter.isNamePresent() ? parameter.getName() : null;
    }

    @Override
    public Object convert(final TableRow tableRow) {
        requireNonNull(tableRow);
        if (dataOrNull != null && annotatedElementOrNull != null) {
            final Object value = dataForAnnotatedElementOrNull(annotatedElementOrNull, dataOrNull);
            if (value != null) {
                return value;
            }
        }
        if (targetClass.isInstance(tableRow)) {
            return tableRow;
        }
        if (targetClass.isInstance(tableRow.getTable())) {
            return tableRow.getTable();
        }
        String name = nameOrNull;
        if (annotatedElementOrNull != null) {
            final Row row = annotatedElementOrNull.getAnnotation(Row.class);
            if (row != null) {
                if (int.class == targetClass) {
                    return tableRow.getRowIndex();
                }
                return convert(tableRow.toMap().toString(), "row(" + tableRow.getRowIndex() + ")");
            }
            final Column column = annotatedElementOrNull.getAnnotation(Column.class);
            if (column != null) {
                name = column.value();
            }
        }
        if (name != null) {
            return convert(valueByName(tableRow, name), name);
        }
        if (index == -1) {
            return tableRow;
        }
        return convert(valueByIndex(tableRow, index), index);
    }

    private String valueByName(final TableRow tableRow, final String name) {
        final int column;
        try {
            column = tableRow.getTable().getColumnIndexByName(name);
        } catch (final Exception e) {
            throw new IllegalArgumentException("Could not access table column " + name, e);
        }
        return valueByIndex(tableRow, column);
    }

    private String valueByIndex(final TableRow tableRow, final int index) {
        try {
            return tableRow.get(index);
        } catch (final Exception e) {
            throw new IllegalArgumentException("Could not access table value for column index " + index, e);
        }
    }

    private Object convert(final String value, final Object column) {
        try {
            return valueConverter.convert(targetClass, targetType, value);
        } catch (final Exception e) {
            throw new IllegalArgumentException("Conversion to " + targetClass + " failed for column '" + column +
                    "': " + value, e);
        }
    }

    private static Object dataForAnnotatedElementOrNull(final AnnotatedElement element, final Data data) {
        requireNonNull(element);
        requireNonNull(data);
        try {
            final DataProvider dataProvider = data.value().newInstance();
            final InjectionContext context = InjectionContext.create(Phase.INIT, element);
            if (!dataProvider.applicable(context)) {
                return null;
            }
            return dataProvider.provideData(InjectionContext.create(Phase.INIT, element));
        } catch (final Exception e) {
            throw new SpockitoException("Cannot provide data for " + element + " annotated with @" + Data.class.getSimpleName(), e);
        }
    }
}
