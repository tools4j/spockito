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

import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A runner for the situation where a test method is to be run multiple times with all the rows of an unroll table.
 * This case applies if the {@link org.tools4j.spockito.Spockito.Unroll} annotation is present at test method level.
 */
public class SingleTestMultiRowRunner extends AbstractSpockitoTestRunner {

    private final FrameworkMethod testMethod;
    private final ValueConverter methodValueConverter;

    public SingleTestMultiRowRunner(final Class<?> clazz,
                                    final FrameworkMethod testMethod,
                                    final ValueConverter methodValueConverter) throws InitializationError {
        super(clazz);
        this.testMethod = Objects.requireNonNull(testMethod);
        this.methodValueConverter = Objects.requireNonNull(methodValueConverter);
        validate();
    }

    @Override
    protected String getName() {
        return testMethod.getName();
    }

    @Override
    protected String testName(final FrameworkMethod method) {
        if (method instanceof UnrolledTestMethod) {
            return method.getName() + Spockito.getName(method.getMethod(), ((UnrolledTestMethod)method).getTableRow());
        }
        return super.testName(method);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        final List<FrameworkMethod> testMethods = new ArrayList<>();
        final Spockito.Unroll unroll = testMethod.getAnnotation(Spockito.Unroll.class);
        if (unroll == null) {
            testMethods.add(testMethod);
        } else {
            final Table table = Table.parse(unroll.value());
            testMethods.addAll(unroll(table));
        }
        return testMethods;
    }

    private List<UnrolledTestMethod> unroll(final Table table) {
        final List<UnrolledTestMethod> unrolled = new ArrayList<>(table.getRowCount());
        for (final TableRow row : table) {
            final UnrolledTestMethod unrolledTestMethod = new UnrolledTestMethod(testMethod.getMethod(), row, methodValueConverter);
            unrolled.add(unrolledTestMethod);
        }
        return unrolled;
    }

    protected void validate() throws InitializationError {
        final List<Throwable> errors = new ArrayList<>();
        try {
            super.collectInitializationErrors(errors);
        } catch (final Exception e) {
            errors.add(e);
        }
        if (!errors.isEmpty()) {
            throw new InitializationError(testMethod + ": " + errors.get(0));
        }
    }

    @Override
    protected void validateTestMethods(final List<Throwable> errors) {
        final List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(Test.class);
        for (final FrameworkMethod method : methods) {
            final Spockito.Unroll unroll = method.getAnnotation(Spockito.Unroll.class);
            if (unroll == null) {
                method.validatePublicVoidNoArg(false, errors);
            } else {
                method.validatePublicVoid(false, errors);
                method.validateNoTypeParametersOnArgs(errors);
            }
        }
    }

}
