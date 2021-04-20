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
package org.tools4j.spockito.table;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Provides values defined by a {@link TableData @TableData} annotation.
 */
public class TableDataProvider implements DataProvider {

    private final Function<? super InjectionContext, ? extends Table> tableFactory;
    private final Function<? super InjectionContext, Class<? extends ValueConverter>> valueConverterTypeLookup;

    public TableDataProvider() {
        this(TableDataProvider::table, TableDataProvider::valueConverter);
    }

    public TableDataProvider(final Function<? super InjectionContext, ? extends Table> tableFactory,
                             final Function<? super InjectionContext, Class<? extends ValueConverter>> valueConverterTypeLookup) {
        this.tableFactory = requireNonNull(tableFactory);
        this.valueConverterTypeLookup = requireNonNull(valueConverterTypeLookup);
    }

    private static Table table(final InjectionContext injectionContext) {
        final TableData tableData = injectionContext.annotatedElement().getAnnotation(TableData.class);
        return Table.parse(tableData.value());
    }

    private static Class<? extends ValueConverter> valueConverter(final InjectionContext injectionContext) {
        final TableData tableData = injectionContext.annotatedElement().getAnnotation(TableData.class);
        return tableData.valueConverter();
    }

    @Override
    public Object provideData(final InjectionContext context) {
        final Table table = tableFactory.apply(context);
        final ValueConverter valueConverter = ValueConverter.create(valueConverterTypeLookup.apply(context));
        final AnnotatedElement element = context.annotatedElement();
        if (element instanceof Field) {
            final Field field = (Field)element;
            return table.to(field.getType(), field.getGenericType(), valueConverter);
        } else if (element instanceof Method) {
            final Method method = (Method)element;
            final Parameter[] parameters = method.getParameters();
            final Object[][] values = new Object[table.getRowCount()][parameters.length];
            for (int i = 0; i < table.getRowCount(); i++) {
                final TableRow row = table.getRow(i);
                for (int j = 0; j < parameters.length; j++) {
                    values[i][j] = tableRowConverter(context, parameters[j], j, valueConverter).convert(row);
                }
            }
            return values;
        } else if (element instanceof Parameter) {
            final Parameter parameter = (Parameter)element;
            return table.to(parameter.getType(), parameter.getParameterizedType(), valueConverter);
        }
        throw new SpockitoException("Annotated element is not supported: " + element);
    }

    protected TableRowConverter tableRowConverter(final InjectionContext context,
                                                  final Parameter parameter,
                                                  final int index,
                                                  final ValueConverter valueConverter) {
        return SpockitoTableRowConverter.create(context, parameter, index, valueConverter);
    }

}
