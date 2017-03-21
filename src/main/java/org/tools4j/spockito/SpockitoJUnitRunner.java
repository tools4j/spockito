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

import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link BlockJUnit4ClassRunner} with parameters support. Parameters can be
 * injected via constructor or into annotated fields. The parameter values are defined
 * by a {@link Table} row; the table is created by a {@link Spockito.Unroll} annotation
 * on the test class or on the constructor.
 */
public class SpockitoJUnitRunner extends BlockJUnit4ClassRunner {

    private final Table tableOrNull;
    private final int row;

    //lazy init cause it is used in parent constructor already
    private ValueConverter defaultValueConverter;

    public SpockitoJUnitRunner(final Class<?> clazz) throws InitializationError {
        this(clazz, null, -1);
    }
    public SpockitoJUnitRunner(final Class<?> clazz, final Table tableOrNull, final int row) throws InitializationError {
        super(clazz);
        this.tableOrNull = tableOrNull;
        this.row = row;
    }

    private ValueConverter getDefaultValueConverter() {
        if (defaultValueConverter == null) {
            Spockito.UseValueConverter useValueConverter = getTestClass().getOnlyConstructor().getAnnotation(Spockito.UseValueConverter.class);
            if (useValueConverter == null) {
                useValueConverter = getTestClass().getAnnotation(Spockito.UseValueConverter.class);
            }
            defaultValueConverter = Spockito.getValueConverter(useValueConverter, SpockitoValueConverter.DEFAULT_INSTANCE);
        }
        return defaultValueConverter;
    }

    @Override
    public Object createTest() throws Exception {
        if (tableOrNull == null) {
            return getTestClass().getOnlyConstructor().newInstance();
        }
        if (fieldsAreAnnotated()) {
            return createTestUsingFieldInjection();
        }
        return createTestUsingConstructorInjection();
    }

    private Object createTestUsingConstructorInjection() throws Exception {
        final Constructor<?> constructor = getTestClass().getOnlyConstructor();
        final ValueConverter valueConverter = Spockito.getValueConverter(constructor.getAnnotation(Spockito.UseValueConverter.class), getDefaultValueConverter());
        final Object[] args = tableOrNull.getRow(row).convertValues(constructor, valueConverter);
        return constructor.newInstance(args);
    }

    private Object createTestUsingFieldInjection() throws Exception {
        final Constructor<?> constructor = getTestClass().getOnlyConstructor();
        final List<FrameworkField> fields = getFieldsAnnotatedByRef();
        final Object testClassInstance = getTestClass().getJavaClass().newInstance();
        final ValueConverter valueConverter = Spockito.getValueConverter(constructor.getAnnotation(Spockito.UseValueConverter.class), getDefaultValueConverter());
        final Object[] fieldValues = tableOrNull.getRow(row).convertValues(fields, valueConverter);
        for (int i = 0; i < fields.size(); i++) {
            final Field field = fields.get(i).getField();
            try {
                field.set(testClassInstance, fieldValues[i]);
            } catch (final Exception e) {
                throw new Exception(getTestClass().getName()
                        + ": Trying to set " + field.getName()
                        + " with the value " + fieldValues[i], e);
            }
        }
        return testClassInstance;
    }

    @Override
    protected String getName() {
        if (tableOrNull == null) {
            return super.getName();
        }
        return super.getName() + Spockito.getName(getTestClass().getOnlyConstructor(), tableOrNull, row);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        final List<FrameworkMethod> testMethods = super.computeTestMethods();
        final List<FrameworkMethod> unrolledMethods = new ArrayList<>();
        for (final FrameworkMethod testMethod : testMethods) {
            final Spockito.Unroll unroll = testMethod.getAnnotation(Spockito.Unroll.class);
            if (unroll == null) {
                unrolledMethods.add(testMethod);
            } else {
                unrolledMethods.addAll(unrollTestMethod(testMethod, unroll));
            }
        }
        return unrolledMethods;
    }

    protected List<SpockitoTestMethod> unrollTestMethod(final FrameworkMethod testMethod, final Spockito.Unroll unroll) {
        final Table table = Table.parse(unroll.value());
        final ValueConverter valueConverter = Spockito.getValueConverter(testMethod.getAnnotation(Spockito.UseValueConverter.class), getDefaultValueConverter());
        final List<SpockitoTestMethod> unrolled = new ArrayList<>(table.getRowCount());
        for (int row = 0; row < table.getRowCount(); row++) {
            final SpockitoTestMethod unrolledTestMethod = new SpockitoTestMethod(testMethod.getMethod(), table, row, valueConverter);
            unrolled.add(unrolledTestMethod);
        }
        return unrolled;
    }

    @Override
    protected void validateConstructor(List<Throwable> errors) {
        validateOnlyOneConstructor(errors);
        if (tableOrNull == null || fieldsAreAnnotated()) {
            validateZeroArgConstructor(errors);
        } else {
            validateConstructorArgs(errors);
        }
    }

    @Override
    protected void validateFields(List<Throwable> errors) {
        super.validateFields(errors);
        if (tableOrNull != null && fieldsAreAnnotated()) {
            final List<FrameworkField> fields = getFieldsAnnotatedByRef();
            for (final FrameworkField field : fields) {
                final String refName = field.getField().getAnnotation(Spockito.Ref.class).value();
                if (!TableRow.REF_ROW.equals(refName) && !tableOrNull.hasColumn(refName)) {
                    errors.add(new Exception("Invalid @Ref value: " + refName +
                            " does not reference a column of the table defined by @Unroll"));
                }
            }
        }
    }

    protected void validateConstructorArgs(List<Throwable> errors) {
        if (tableOrNull != null && fieldsAreAnnotated()) {
            final Constructor<?> constructor = getTestClass().getOnlyConstructor();
            final java.lang.reflect.Parameter[] parameters = constructor.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                final String refName = Spockito.parameterRefNameOrNull(parameters[i]);
                if (refName != null && !TableRow.REF_ROW.equals(refName) && !tableOrNull.hasColumn(refName)) {
                    errors.add(new Exception("Invalid @Ref value or parameter name for argument " + i +
                            " of type " + parameters[i].getType() + " in the constructor: " + refName +
                            " does not reference a column of the table defined by @Unroll"));
                }
            }
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

    private List<FrameworkField> getFieldsAnnotatedByRef() {
        return getTestClass().getAnnotatedFields(Spockito.Ref.class);
    }

    private boolean fieldsAreAnnotated() {
        return !getFieldsAnnotatedByRef().isEmpty();
    }

}
