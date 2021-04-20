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
package org.tools4j.spockito.jupiter;

import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.converter.ConvertWith;
import org.tools4j.spockito.table.Data;
import org.tools4j.spockito.table.InjectionContext;
import org.tools4j.spockito.table.SpockitoTableRowConverter;
import org.tools4j.spockito.table.TableRowConverter;
import org.tools4j.spockito.table.ValueConverter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;

import static org.tools4j.spockito.table.SpockitoAnnotations.annotationDirectOrMeta;

/**
 * Similar to the converters created by the factory methods in {@link SpockitoTableRowConverter} but with added support
 * for the following parameter-level annotations:
 * <ul>
 *     <li>{@linkplain ConvertWith @ConvertWith} for parameter specific conversion</li>
 *     <li>{@linkplain AggregateWith @AggregateWith} for parameter level aggregation, with special consideration of
 *         {@link TableRowAggregator}</li>
 * </ul>
 */
enum TableRowConverters {
    ;
    static TableRowConverter create(final InjectionContext context,
                                    final Parameter parameter,
                                    final int index,
                                    final ValueConverter valueConverter) {
        if (annotationDirectOrMeta(parameter, ConvertWith.class) != null) {
            return objConverter(context, parameter, index, valueConverter);
        }
        final AggregateWith aggregateWith = annotationDirectOrMeta(parameter, AggregateWith.class);
        if (aggregateWith != null) {
            final Class<?> agg = aggregateWith.value();
            if (TableRowAggregator.class.isAssignableFrom(agg)) {
                return tableRow -> tableRow;
            }
            return objConverter(context, parameter, index, valueConverter);
        }
        return SpockitoTableRowConverter.create(context, parameter, index, valueConverter);
    }

    private static TableRowConverter objConverter(final InjectionContext context,
                                                  final Parameter parameter,
                                                  final int index,
                                                  final ValueConverter valueConverter) {
        return new SpockitoTableRowConverter(dataSubContextOrNull(context, parameter), parameter,
                parameter.getName(), index, Object.class, Object.class, valueConverter);
    }

    private static InjectionContext dataSubContextOrNull(final InjectionContext context,
                                                         final AnnotatedElement annotatedElement) {
        final Data data = annotationDirectOrMeta(annotatedElement, Data.class);
        if (data != null) {
            return InjectionContext.create(context.phase(), annotatedElement);
        }
        return null;
    }
}
