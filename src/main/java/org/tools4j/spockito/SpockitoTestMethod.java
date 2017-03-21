/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017 tools4j.org (Marco Terzer)
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

import org.junit.runners.model.FrameworkMethod;

import java.lang.reflect.Method;
import java.util.Objects;

public class SpockitoTestMethod extends FrameworkMethod {

    private final Table table;
    private final int row;
    private final ValueConverter valueConverter;

    /**
     * Returns a new {@code FrameworkMethod} for {@code method}
     *
     * @param method
     */
    public SpockitoTestMethod(final Method method, final Table table, final int row, final ValueConverter valueConverter) {
        super(method);
        this.table = Objects.requireNonNull(table);
        this.row = row;
        this.valueConverter = Objects.requireNonNull(valueConverter);
    }

    @Override
    public Object invokeExplosively(final Object target, final Object... params) throws Throwable {
        return super.invokeExplosively(target, getTestArgs());
    }

    protected Object[] getTestArgs() {
        return table.getRow(row).convertValues(getMethod(), valueConverter);
    }

    @Override
    public String getName() {
        return super.getName() + Spockito.getName(getMethod(), table, row);
    }

    @Override
    public int hashCode() {
        int code = super.hashCode();
        code = 31 * code + table.hashCode();
        code = 31 * code + row;
        return row;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass() || !super.equals(obj)) {
            return false;
        }
        final SpockitoTestMethod other = (SpockitoTestMethod)obj;
        return this.row == row && this.table.equals(table);
    }

    @Override
    public String toString() {
        return super.toString() + "[" + row + "]";
    }
}
