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
package org.tools4j.spockito.jupiter;

import org.junit.platform.commons.annotation.Testable;
import org.tools4j.spockito.table.InjectionContext;
import org.tools4j.spockito.table.InjectionContext.Phase;
import org.tools4j.spockito.table.Table;
import org.tools4j.spockito.table.TableDataProvider;
import org.tools4j.spockito.table.TableRowConverter;
import org.tools4j.spockito.table.ValueConverter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.tools4j.spockito.table.SpockitoAnnotations.annotationDirectOrMeta;

public class TableSourceDataProvider extends TableDataProvider {

    public static final TableSourceDataProvider DEFAULT_INSTANCE = new TableSourceDataProvider();

    public TableSourceDataProvider() {
        super(TableSourceDataProvider::table, TableSourceDataProvider::valueConverter);
    }

    private static Table table(final InjectionContext injectionContext) {
        final TableSource tableData = injectionContext.annotatedElement().getAnnotation(TableSource.class);
        return Table.parse(tableData.value());
    }

    private static Class<? extends ValueConverter> valueConverter(final InjectionContext injectionContext) {
        final TableSource tableData = injectionContext.annotatedElement().getAnnotation(TableSource.class);
        return tableData.valueConverter();
    }

    @Override
    public boolean applicable(final InjectionContext context) {
        if (context.phase() == Phase.INIT) {
            final AnnotatedElement element = context.annotatedElement();
            if (element instanceof Method) {
                final Testable testable = annotationDirectOrMeta(element, Testable.class);
                //invoke test methods during TEST phase
                return testable == null;
            }
        }
        return true;
    }

    @Override
    protected TableRowConverter tableRowConverter(final Parameter parameter, final int index, final ValueConverter valueConverter) {
        return TableRowConverters.create(parameter, index, valueConverter);
    }
}
