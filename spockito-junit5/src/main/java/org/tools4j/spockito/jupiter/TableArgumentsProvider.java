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

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.tools4j.spockito.table.InjectionContext;
import org.tools4j.spockito.table.InjectionContext.Phase;
import org.tools4j.spockito.table.SpockitoException;
import org.tools4j.spockito.table.Table;
import org.tools4j.spockito.table.TableData;
import org.tools4j.spockito.table.TableDataProvider;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Provides arguments defined by {@link TableData} using {@link Table}.
 */
final class TableArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
        final Method testMethod = context.getRequiredTestMethod();
        final InjectionContext injectionContext = InjectionContext.create(Phase.TEST, testMethod);
        final TableDataProvider tableDataProvider = TableSourceDataProvider.DEFAULT_INSTANCE;
        if (!tableDataProvider.applicable(injectionContext)) {
            //should not happen as it should always be applicable for TEST phase
            throw new SpockitoException("Not applicable: " + tableDataProvider);
        }
        final Object data = tableDataProvider.provideData(injectionContext);
        if (data instanceof Object[][]) {
            return Arrays.stream((Object[][])data).map(Arguments::of);
        }
        throw new SpockitoException("Table data provider " + tableDataProvider + " should return value of type Object[][], but found " + data);
    }

}