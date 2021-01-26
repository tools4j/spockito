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
package org.tools4j.spockito;

import org.junit.runners.model.FrameworkMethod;
import org.tools4j.spockito.table.TableRow;
import org.tools4j.spockito.table.ValueConverter;

import java.lang.reflect.Method;

import static java.util.Objects.requireNonNull;

/**
 * Extension of framework method representing a test method with a data row.
 */
public class UnrolledTestMethod extends FrameworkMethod {

    private final TableRow tableRow;
    private final ValueConverter valueConverter;

    public UnrolledTestMethod(final Method method, final TableRow tableRow, final ValueConverter valueConverter) {
        super(method);
        this.tableRow = requireNonNull(tableRow);
        this.valueConverter = requireNonNull(valueConverter);
    }

    @Override
    public Object invokeExplosively(final Object target, final Object... params) throws Throwable {
        return super.invokeExplosively(target, getTestArgs());
    }

    protected TableRow getTableRow() {
        return tableRow;
    }

    protected Object[] getTestArgs() {
        return TableRowConverters.convert(tableRow, getMethod(), valueConverter);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public int hashCode() {
        int code = super.hashCode();
        code = 31 * code + tableRow.getTable().hashCode();
        code = 31 * code + tableRow.getRowIndex();
        return code;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass() || !super.equals(obj)) {
            return false;
        }
        final UnrolledTestMethod other = (UnrolledTestMethod)obj;
        return this.tableRow.getTable().equals(other.tableRow.getTable()) &&
                this.tableRow.getRowIndex() == other.tableRow.getRowIndex();
    }

    @Override
    public String toString() {
        return super.toString() + "[" + tableRow.getRowIndex() + "]";
    }
}
