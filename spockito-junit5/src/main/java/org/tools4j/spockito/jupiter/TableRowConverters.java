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
package org.tools4j.spockito.jupiter;

import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.converter.ConvertWith;
import org.tools4j.spockito.table.Data;
import org.tools4j.spockito.table.DataProvider;
import org.tools4j.spockito.table.InjectionContext;
import org.tools4j.spockito.table.InjectionContext.Phase;
import org.tools4j.spockito.table.SpockitoException;
import org.tools4j.spockito.table.SpockitoTableRowConverter;
import org.tools4j.spockito.table.Table;
import org.tools4j.spockito.table.TableData;
import org.tools4j.spockito.table.TableDataProvider;
import org.tools4j.spockito.table.TableJoiner;
import org.tools4j.spockito.table.TableJoiner.JoinBuilder;
import org.tools4j.spockito.table.TableRow;
import org.tools4j.spockito.table.TableRowConverter;
import org.tools4j.spockito.table.ValueConverter;

import java.lang.reflect.Parameter;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;
import static org.tools4j.spockito.table.SpockitoAnnotations.annotationDirectOrMeta;

/**
 * Similar to the converters created by the factory methods in {@link SpockitoTableRowConverter} but with added support
 * for the following parameter-level annotations:
 * <ul>
 *     <li>{@linkplain ConvertWith @ConvertWith} for parameter specific conversion</li>
 *     <li>{@linkplain AggregateWith @AggregateWith} for parameter level aggregation, with special consideration of
 *         {@link TableRowAggregator}</li>
 *     <li>{@linkplain JoinOn @JoinOn} for parameter level child tables joined to the test level parent table</li>
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
        final JoinOn joinOn = annotationDirectOrMeta(parameter, JoinOn.class);
        if (joinOn != null) {
            final Data data = annotationDirectOrMeta(parameter, Data.class);
            if (data != null) {
                final Class<? extends DataProvider> dataProvider = data.value();
                if (TableDataProvider.class.isAssignableFrom(dataProvider)) {
                    final TableRowConverter joinedConverter = joinedConverterOrNull(parameter, joinOn);
                    if (joinedConverter != null) {
                        return joinedConverter;
                    }
                }
            }
        }
        return SpockitoTableRowConverter.create(context, parameter, index, valueConverter);
    }

    private static TableRowConverter objConverter(final InjectionContext context,
                                                  final Parameter parameter,
                                                  final int index,
                                                  final ValueConverter valueConverter) {
        return new SpockitoTableRowConverter(context.createSubContextOrNull(parameter, Data.class), parameter,
                parameter.getName(), index, Object.class, Object.class, valueConverter);
    }

    private static TableRowConverter joinedConverterOrNull(final Parameter parameter, final JoinOn joinedOn) {
        requireNonNull(parameter);
        requireNonNull(joinedOn);
        final Data data = annotationDirectOrMeta(parameter, Data.class);
        try {
            final DataProvider dataProvider = data.value().newInstance();
            if (dataProvider instanceof TableDataProvider) {
                final TableDataProvider tableDataProvider = (TableDataProvider)dataProvider;
                final InjectionContext ctxt = InjectionContext.create(Phase.INIT, parameter);
                if (dataProvider.applicable(ctxt)) {
                    final Table table = tableDataProvider.provideTable(ctxt);
                    final ValueConverter valueConverter = tableDataProvider.provideValueConverter(ctxt);
                    return tableRow -> tableDataProvider.provideData(ctxt, join(table, tableRow, joinedOn), valueConverter);
                }
            }
            return null;
        } catch (final Exception e) {
            throw new SpockitoException("Cannot provide data for " + parameter + " annotated with @"
                    + TableData.class.getSimpleName() + " (or meta annotation)", e);
        }
    }

    private static Table join(final Table child, final TableRow parent, final JoinOn joinOn) {
        final String[] children = joinOn.child();
        final String[] parents = joinOn.parent();
        if (children.length != parents.length) {
            throw new IllegalArgumentException("JoinOn.parent=" + Arrays.toString(joinOn.parent()) + " and JoinOn.child="
                    + Arrays.toString(joinOn.parent()) + " must have matching number of entries");
        }
        final TableJoiner joiner = child.join(parent);
        JoinBuilder builder = null;
        for (final String common : joinOn.value()) {
            builder = builder == null ? joiner.on(common) : builder.and(common);
        }
        for (int i = 0; i < children.length; i++) {
            builder = builder == null ? joiner.on(children[i], parents[i]) :
                    builder.and(children[i], parents[i]);
        }
        if (builder != null) {
            return builder.apply();
        }
        throw new IllegalArgumentException("JoinOn must define at least one value");
    }
}
