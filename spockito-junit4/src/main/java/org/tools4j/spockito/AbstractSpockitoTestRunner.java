/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2020 tools4j.org (Marco Terzer)
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

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Common base for spockito test runners.
 */
abstract public class AbstractSpockitoTestRunner extends BlockJUnit4ClassRunner {

    public AbstractSpockitoTestRunner(final Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        return childrenInvoker(notifier);
    }

    @Override
    protected Annotation[] getRunnerAnnotations() {
        return new Annotation[0];
    }

    @Override
    public void filter(final Filter filter) throws NoTestsRemainException {
        if (filter instanceof MethodLevelFilter) {
            //Spockito does the necessary filtering with MethodLevelFilter
            super.filter(Filter.ALL);
        } else {
            super.filter(filter);
        }
    }

    @Override
    protected Description describeChild(final FrameworkMethod method) {
        final Spockito.Name name = Spockito.nameAnnotationOrNull(method.getMethod());
        if (name != null && name.shortFormat()) {
            return Description.createSuiteDescription(testName(method), method.getAnnotations());
        } else {
            return super.describeChild(method);
        }
    }

    @Override
    protected void collectInitializationErrors(final List<Throwable> errors) {
        //don't do here, do validation in our own constructor
    }

}
