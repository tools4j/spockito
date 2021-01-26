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

import org.junit.Test;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.tools4j.spockito.Spockito.Unroll;
import org.tools4j.spockito.table.Table;
import org.tools4j.spockito.table.TableRow;
import org.tools4j.spockito.table.ValueConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.tools4j.spockito.Spockito.Ref.ALL_COLUMNS;
import static org.tools4j.spockito.Spockito.Ref.ROW_INDEX;

/**
 * A runner for the case of a single data row applied to a set of test methods. This case applies if the
 * {@link Unroll} annotation is present at test class level.
 */
public class SingleRowMultiTestRunner extends AbstractSpockitoTestRunner {

    private final TableRow tableRow;
    private final ValueConverter defaultValueConverter;

    public SingleRowMultiTestRunner(final Class<?> clazz,
                                    final TableRow tableRow,
                                    final ValueConverter defaultValueConverter) throws InitializationError {
        super(clazz);
        this.tableRow = Objects.requireNonNull(tableRow);
        this.defaultValueConverter = Objects.requireNonNull(defaultValueConverter);
        validate();
    }

    @Override
    public Object createTest() throws Exception {
        final Object testInstance = createTestUsingConstructorInjection();
        return fieldsAreAnnotated() ? injectAnnotatedFields(testInstance) : testInstance;
    }

    private Object createTestUsingConstructorInjection() throws Exception {
        final Constructor<?> constructor = getTestClass().getOnlyConstructor();
        final ValueConverter valueConverter = Spockito.getValueConverter(constructor.getAnnotation(Spockito.UseValueConverter.class), defaultValueConverter);
        final Object[] args = TableRowConverters.convert(tableRow, constructor, valueConverter);
        return constructor.newInstance(args);
    }

    private Object injectAnnotatedFields(final Object testInstance) throws Exception {
        final List<FrameworkField> frameworkFields = getFieldsAnnotatedByRef();
        final Field[] fields = frameworkFields.stream().map(FrameworkField::getField).toArray(Field[]::new);
        final Object[] fieldValues = TableRowConverters.convert(tableRow, fields, defaultValueConverter);
        for (int i = 0; i < fields.length; i++) {
            final Field field = fields[i];
            try {
                field.setAccessible(true);
                field.set(testInstance, fieldValues[i]);
            } catch (final Exception e) {
                throw new Exception(getTestClass().getName()
                        + ": Trying to set " + field.getName()
                        + " with the value " + fieldValues[i], e);
            }
        }
        return testInstance;
    }

    @Override
    protected String getName() {
        return Spockito.getName(getTestClass().getOnlyConstructor(), tableRow);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        final List<FrameworkMethod> testMethods = super.computeTestMethods();
        final List<FrameworkMethod> spockitoMethods = new ArrayList<>(testMethods.size());
        for (final FrameworkMethod testMethod : testMethods) {
            if (testMethod.getMethod().getParameterCount() == 0) {
                spockitoMethods.add(testMethod);
            } else {
                final Spockito.UseValueConverter useValueConverter = testMethod.getAnnotation(Spockito.UseValueConverter.class);
                final ValueConverter methodValueConverter = Spockito.getValueConverter(useValueConverter, defaultValueConverter);
                final UnrolledTestMethod spockitoTestMethod = new UnrolledTestMethod(testMethod.getMethod(), tableRow, methodValueConverter);
                spockitoMethods.add(spockitoTestMethod);
            }
        }
        return spockitoMethods;
    }


    @Override
    protected String testName(final FrameworkMethod method) {
        final String testName = super.testName(method);
        //NOTE: we intentionally don't want class level Name annotation as a default here!
        final Spockito.Name name = method.getAnnotation(Spockito.Name.class);
        return name == null ? testName : testName + Spockito.getName(name, tableRow, "");
    }

    @Override
    protected void validateConstructor(List<Throwable> errors) {
        validateOnlyOneConstructor(errors);
        validateConstructorArgs(errors);
    }

    @Override
    protected void validateFields(List<Throwable> errors) {
        super.validateFields(errors);
        if (fieldsAreAnnotated()) {
            final List<FrameworkField> fields = getFieldsAnnotatedByRef();
            for (final FrameworkField field : fields) {
                final String refName = field.getField().getAnnotation(Spockito.Ref.class).value();
                if (!isValidParameterRefOrName(tableRow.getTable(), refName)) {
                    errors.add(new Exception("Invalid @Ref value: " + refName +
                            " does not reference a column of the table defined by @Unroll"));
                }
            }
        }
    }

    protected void validateConstructorArgs(List<Throwable> errors) {
        final Constructor<?> constructor = getTestClass().getOnlyConstructor();
        final java.lang.reflect.Parameter[] parameters = constructor.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            final String refName = Spockito.parameterRefOrNameOrNull(parameters[i]);
            if (refName != null && !isValidParameterRefOrName(tableRow.getTable(), refName)) {
                errors.add(new Exception("Invalid @Ref value or parameter name for argument " + i +
                        " of type " + parameters[i].getType() + " in the constructor: " + refName +
                        " does not reference a column of the table defined by @Unroll"));
            }
        }
    }

    protected void validate() throws InitializationError {
        final List<Throwable> errors = new ArrayList<>();
        super.collectInitializationErrors(errors);
    }

    @Override
    protected void validateTestMethods(final List<Throwable> errors) {
        final List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(Test.class);
        for (final FrameworkMethod method : methods) {
            method.validatePublicVoid(false, errors);
            method.validateNoTypeParametersOnArgs(errors);
        }
    }

    private List<FrameworkField> getFieldsAnnotatedByRef() {
        return getTestClass().getAnnotatedFields(Spockito.Ref.class);
    }

    private boolean fieldsAreAnnotated() {
        return !getFieldsAnnotatedByRef().isEmpty();
    }

    private static boolean isValidParameterRefOrName(final Table table, final String name) {
        return table.hasColumn(name) || ROW_INDEX.equals(name) || ALL_COLUMNS.equals(name);
    }

}
