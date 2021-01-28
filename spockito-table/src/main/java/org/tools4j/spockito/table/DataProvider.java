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
package org.tools4j.spockito.table;

/**
 * A {@code DataProvider} is responsible for {@linkplain #provideData providing} data to be injected to a field or to
 * parameters of a method.
 *
 * <p>A {@code DataProvider} can be registered via the {@link Data @Data} annotation.
 *
 * <p>Implementations must provide a no-args constructor.
 */
public interface DataProvider {

    default boolean applicable(final InjectionContext context) {
        return true;
    }

    /**
     * Provides the data to be injected to a field or to method parameters.
     *
     * @param context the current injection context; never {@code null}
     * @return the data to inject; never {@code null}
     * @throws Exception if data provision fails
     */
    Object provideData(InjectionContext context) throws Exception;

}
