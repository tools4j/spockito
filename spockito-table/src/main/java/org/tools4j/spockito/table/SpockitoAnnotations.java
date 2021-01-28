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

import org.tools4j.spockito.table.InjectionContext.Phase;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains static methods to initialise instances with annotated members.
 */
public enum SpockitoAnnotations {
    ;

    /**
     * Initialises fields and invokes methods of the provided instance that are annotated with {@linkplain Data @Data}
     * providers.
     *
     * @param instance the instance to initialise
     */
    public static void initData(final Object instance) {
        initDataFields(instance);
        invokeDataMethods(instance);
    }

    /**
     * Initialises fields of the provided instance that are annotated with {@linkplain Data @Data} providers; annotated
     * methods are not invoked.
     *
     * @param instance the instance to initialise
     */
    public static void initDataFields(final Object instance) {
        initDataFields(instance, instance.getClass());
    }

    /**
     * Invokes methods of the provided instance that are annotated with {@linkplain Data @Data} providers; annotated
     * fields are not initialised.
     *
     * @param instance the instance to initialise
     */
    public static void invokeDataMethods(final Object instance) {
        invokeDataMethods(instance, instance.getClass());
    }

    /**
     * Returns direct and indirect (meta) annotations on a given element.
     *
     * @param element           the annotated element
     * @param annotationClass   the type of annotation to look for
     * @param <A>               generic type of desired annotation
     * @return the annotation if found on this element, or null if not found
     */
    public static <A extends Annotation> A annotationDirectOrMeta(final AnnotatedElement element, final Class<A> annotationClass) {
        return annotationDirectOrMeta(element, annotationClass, new HashSet<>());
    }

    private static <A extends Annotation> A annotationDirectOrMeta(final AnnotatedElement element,
                                                                   final Class<A> annotationClass,
                                                                   final Set<AnnotatedElement> visited) {
        if (!visited.add(element)) {
            return null;
        }
        final A annotation = element.getAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }
        for (final Annotation otherAnnotation : element.getAnnotations()) {
            final Class<? extends Annotation> annotationType = otherAnnotation.annotationType();
            final A indirectAnnotation = annotationDirectOrMeta(annotationType, annotationClass, visited);
            if (indirectAnnotation != null) {
                return indirectAnnotation;
            }
        }
        return null;
    }

    private static void initDataFields(final Object instance, final Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return;
        }
        for (final Field field : clazz.getDeclaredFields()) {
            final Data data = annotationDirectOrMeta(field, Data.class);
            if (data != null) {
                initDataField(instance, field, data);
            }
        }
        initDataFields(instance, clazz.getSuperclass());
    }

    private static void invokeDataMethods(final Object instance, final Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return;
        }
        for (final Method method : clazz.getDeclaredMethods()) {
            final Data data = annotationDirectOrMeta(method, Data.class);
            if (data != null) {
                invokeDataMethod(instance, method, data);
            }
        }
        invokeDataMethods(instance, clazz.getSuperclass());
    }

    private static void initDataField(final Object instance, final Field field, final Data data) {
        final boolean accessible = field.isAccessible();
        try {
            final DataProvider dataProvider = data.value().newInstance();
            final InjectionContext context = InjectionContext.create(Phase.INIT, field);
            if (!dataProvider.applicable(context)) {
                return;
            }
            final Object value = dataProvider.provideData(context);
            if (!accessible) {
                field.setAccessible(true);
            }
            field.set(instance, value);
        } catch (final Exception e) {
            throw new SpockitoException("Cannot assign field " + instance + "." + field.getName(), e);
        } finally {
            if (field.isAccessible() != accessible) {
                field.setAccessible(accessible);
            }
        }
    }

    private static void invokeDataMethod(final Object instance, final Method method, final Data data) {
        final boolean accessible = method.isAccessible();
        Object rowValues = null;
        try {
            final DataProvider dataProvider = data.value().newInstance();
            final InjectionContext context = InjectionContext.create(Phase.INIT, method);
            if (!dataProvider.applicable(context)) {
                return;
            }
            final Object value = dataProvider.provideData(context);
            if (!(value instanceof Object[])) {
                throw new SpockitoException("Data provider " + dataProvider + " should return an array of values for method " +
                        method + " on instance " + instance + " but it returned " + value);
            }
            final Object[] rowData = (Object[])value;
            if (!accessible) {
                method.setAccessible(true);
            }
            for (final Object rowVals : rowData) {
                rowValues = rowVals;
                switch (method.getParameterCount()) {
                    case 0:
                        method.invoke(instance);
                        break;
                    case 1:
                        method.invoke(instance, rowVals);
                        break;
                    default:
                        method.invoke(instance, (Object[]) rowVals);
                        break;
                }
            }
        } catch (final Exception e) {
            throw new SpockitoException("Cannot invoke method " + method + " on instance " + instance +
                    " with arguments " + (rowValues instanceof Object[] ? Arrays.toString((Object[])rowValues) : rowValues), e);
        } finally {
            if (method.isAccessible() != accessible) {
                method.setAccessible(accessible);
            }
        }
    }
}
