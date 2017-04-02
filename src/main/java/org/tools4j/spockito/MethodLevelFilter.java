/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 tools4j.org (Marco Terzer)
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

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

import java.util.Objects;

/**
 * Method filter wrapped around an original filter in {@link Spockito#filter(Filter)} to work around an Intellij problem
 * when re-running individual tests.
 */
public final class MethodLevelFilter extends Filter {

    private Filter delegate;

    public MethodLevelFilter(final Filter delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public boolean shouldRun(final Description description) {
        if (description.isTest()) {
            return delegate.shouldRun(description);
        }

        // explicitly check if any children want to run
        for (Description child : description.getChildren()) {
            if (shouldRun(child)) {
                return true;
            }
            //Intellij bug, we get the wrong description, let us test with a slightly modified one now
            final Description relaxed = Description.createTestDescription(
                    child.getTestClass(), getMethodName(child.getDisplayName())
            );
            if (shouldRun(relaxed)) {
                return true;
            }
        }
        return false;
    }

    private static String getMethodName(final String name) {
        return name.substring(0, findFirstNonNameChar(name));
    }

    private static int findFirstNonNameChar(final String name) {
        int index = 0;
        if (index < name.length() && Character.isJavaIdentifierStart(name.charAt(index))) {
            index++;
            while (index < name.length() && Character.isJavaIdentifierPart(name.charAt(index))) {
                index++;
            }
        }
        return index;
    }

    @Override
    public String describe() {
        return delegate.describe();
    }
}
