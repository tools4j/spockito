/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2022 tools4j.org (Marco Terzer)
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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import static java.util.Objects.requireNonNull;
import static org.tools4j.spockito.table.SpockitoAnnotations.annotationDirectOrMeta;

/**
 * Context passed to {@link DataProvider#provideData(InjectionContext)} in the process of initialising the
 * {@linkplain #annotatedElement() annotated element}.
 */
public interface InjectionContext {
    /**
     * The phase during which the injection occurs.
     */
    enum Phase {
        /**
         * Initialisation phase if injection is triggered at initialisation time, for instance when using the Junit-5
         * {@code SpockitoExtension}, when using {@link SpockitoData} or when directly invoking
         * {@link SpockitoAnnotations#initData(Object)}.
         */
        INIT,
        /**
         * Test phase if injection is triggered via a test framework such as junit when invoking the test.
         */
        TEST
    }

    /**
     * The phase in which the injection occurs
     * @return the injection phase
     */
    Phase phase();

    /**
     * The annotated element which is in process of being injected with a value.
     * @return the injection target annotated with the value to inject
     */
    AnnotatedElement annotatedElement();

    /**
     * Static factory method for injection context.
     *
     * @param phase             the injectin phase
     * @param annotatedElement  the annotated element
     * @return a new injection context instance for the provided arguments
     * @throws NullPointerException if any of the arguments is null
     */
    static InjectionContext create(final Phase phase, final AnnotatedElement annotatedElement) {
        requireNonNull(phase);
        requireNonNull(annotatedElement);
        return new InjectionContext() {
            @Override
            public Phase phase() {
                return phase;
            }

            @Override
            public AnnotatedElement annotatedElement() {
                return annotatedElement;
            }

            @Override
            public String toString() {
                return "InjectionContext{phase=" + phase + ", annotatedElement=" + annotatedElement + "}";
            }
        };
    }

    /**
     * Creates a sub-context of this context for the provided element using the same phase as this context, but only
     * if the element has annotated with an annotation of the provided type (via direct or meta); otherwise null is
     * returned.
     *
     * @param element           the element for which to return a sub-context
     * @param annotationClass   the type of annotation to look for on the provided element
     * @return a sub-context if element has the required annotation, and null otherwise
     */
    default InjectionContext createSubContextOrNull(final AnnotatedElement element,
                                                    final Class<? extends Annotation> annotationClass) {
        final Annotation annotation = annotationDirectOrMeta(element, annotationClass);
        if (annotation != null) {
            return InjectionContext.create(phase(), element);
        }
        return null;
    }
}
