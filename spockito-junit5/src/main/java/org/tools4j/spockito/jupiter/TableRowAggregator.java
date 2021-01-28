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

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.tools4j.spockito.table.Table;
import org.tools4j.spockito.table.TableRow;
import org.tools4j.spockito.table.ValueConverter;

import java.lang.reflect.Parameter;

import static org.tools4j.spockito.jupiter.TableValueConverter.valueConverter;

/**
 * Aggregator used by {@linkplain AggregateTableRow @AggregateTableRow} or if used with the
 * {@linkplain org.junit.jupiter.params.aggregator.AggregateWith @AggregateWith} annotation.
 *
 * <p>The annotated method parameter references a whole {@link TableRow} of a {@link Table}.
 */
public class TableRowAggregator implements ArgumentsAggregator {

    @Override
    public Object aggregateArguments(final ArgumentsAccessor accessor, final ParameterContext context) throws ArgumentsAggregationException {
        final Parameter parameter = context.getParameter();
        final ValueConverter valueConverter = valueConverter(context);
        final TableRow tableRow = accessor.get(context.getIndex(), TableRow.class);
        return tableRow.to(parameter.getType(), parameter.getParameterizedType(), valueConverter);
    }

}
