/*
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
package org.tools4j.spockito;

import org.tools4j.spockito.Spockito.Ref;
import org.tools4j.spockito.table.Data;
import org.tools4j.spockito.table.InjectionContext;
import org.tools4j.spockito.table.InjectionContext.Phase;
import org.tools4j.spockito.table.SpockitoTableRowConverter;
import org.tools4j.spockito.table.TableRow;
import org.tools4j.spockito.table.TableRowConverter;
import org.tools4j.spockito.table.ValueConverter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import static org.tools4j.spockito.Spockito.fieldRefOrName;
import static org.tools4j.spockito.Spockito.parameterRefOrNameOrNull;
import static org.tools4j.spockito.table.SpockitoAnnotations.annotationDirectOrMeta;

/**
 * Similar to the converters created by the factory methods in {@link TableRowConverter} but this time the parameters or
 * fields can be annotated to override the default name or index.
 */
enum TableRowConverters {
    ;
    static TableRowConverter create(final InjectionContext context, final Parameter parameter, final int index, final ValueConverter valueConverter) {
        final TableRowConverter special = specialRefConverterOrNull(parameter.getAnnotation(Ref.class),
                parameter.getType(), parameter.getParameterizedType(), valueConverter);
        if (special != null) {
            return special;
        }
        return new SpockitoTableRowConverter(dataSubContextOrNull(context, parameter), parameter,
                parameterRefOrNameOrNull(parameter), index, parameter.getType(), parameter.getParameterizedType(),
                valueConverter);
    }

    static TableRowConverter create(final Field field, final ValueConverter valueConverter) {
        final TableRowConverter special = specialRefConverterOrNull(field.getAnnotation(Ref.class), field.getType(),
                field.getGenericType(), valueConverter);
        if (special != null) {
            return special;
        }
        return new SpockitoTableRowConverter(null, field, fieldRefOrName(field), -1, field.getType(),
                field.getGenericType(), valueConverter);
    }

    private static TableRowConverter specialRefConverterOrNull(final Ref ref, final Class<?> type, final Type genericType, final ValueConverter valueConverter) {
        if (ref != null) {
            if (Ref.ALL_COLUMNS.equals(ref.value())) {
                return tableRow -> valueConverter.convert(type, genericType, tableRow.toMap().toString());
            }
            if (Ref.ROW_INDEX.equals(ref.value())) {
                return tableRow -> Integer.toString(tableRow.getRowIndex());
            }
        }
        return null;
    }

    static Object[] convert(final TableRow tableRow, final Executable executable, final ValueConverter valueConverter) {
        final Parameter[] parameters = executable.getParameters();
        final Object[] values = new Object[parameters.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = create(null, parameters[i], i, valueConverter).convert(tableRow);
        }
        return values;
    }

    static Object[] convert(final TableRow tableRow, final Field[] fields, final ValueConverter valueConverter) {
        final Object[] values = new Object[fields.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = create(fields[i], valueConverter).convert(tableRow);
        }
        return values;
    }

    private static InjectionContext dataSubContextOrNull(final InjectionContext context,
                                                         final AnnotatedElement annotatedElement) {
        final Data data = annotationDirectOrMeta(annotatedElement, Data.class);
        if (data != null) {
            return InjectionContext.create(context == null ? Phase.TEST : context.phase(), annotatedElement);
        }
        return null;
    }

}
