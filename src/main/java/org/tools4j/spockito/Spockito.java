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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import java.lang.annotation.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * The custom runner <code>Spockito</code> implements parameterized tests where the test data
 * is defined in a table-like structure via {@link Unroll} annotation.
 */
public class Spockito extends Suite {

    private static final String DEFAULT_NAME = "[{row}]: {0}";

    /**
     * Annotation for a test or a test method which provides test data for the tests. If the
     * annotation made at the test class level, then the test data is applied to all tests
     * methods. Test data on method level is applied for this method only.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = {ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
    public @interface Unroll {
        /**
         * Annotation with test case values declared in a table like structure as follows:
         * <pre>
         * | colA      | colB      | colC      |
         * | value_1_A | value_1_B | value_1_C |
         * | value_2_A | value_2_B | value_2_C |
         * etc...
         * </pre>
         *
         * @return  An array of strings represented as table data; string[0] contains the header
         *          row with column names
         * @see MessageFormat
         */
        String[] value();
    }

    /**
     * Annotation for a test or a test method to indicate an alternative name for the parameterized
     * test. Default name is "{row}[{0}]".
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = {ElementType.TYPE, ElementType.METHOD})
    public @interface Name {
        /**
         * Optional pattern to derive the test's name from the parameters. Use
         * numbers in braces to refer to the parameters or the additional data
         * as follows:
         * <pre>
         * {row} - the current row index
         * {0} - the row's value in the first column
         * {1} - the row's value in the second column
         * {colA} - the row's value in the "colA" column
         * {colB} - the row's value in the "colB" column
         * etc...
         * </pre>
         * <p>
         * Default value is "[{row}]: {0}".
         *
         * @return A patter n string similar to {@link MessageFormat}
         */
        String value() default DEFAULT_NAME;
    }

    /**
     * Annotation for fields or parameters of a test method or of the test constructor. Fields
     * need only be annotated if the field name differs from the column name of the test data.
     * Constructor or test method parameters need to be anotated if they are not in the same
     * order as the columns in the test data.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = {ElementType.FIELD, ElementType.PARAMETER})
    public @interface Ref {
        /**
         * @return the column name
         */
        String value() default "";
    }

    /**
     * Add this annotation to your test class or method if you want to specify custom value
     * converters from string to typed parameters. The converter must have a public zero-arg
     * constructor.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Target(value = {ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
    public @interface UseValueConverter {
        /**
         * @return a {@link ValueConverter} class (must have a default constructor)
         */
        Class<? extends ValueConverter> value() default SpockitoValueConverter.class;
    }

    /**
     * Only called reflectively. Do not use programmatically.
     * @param clazz the test class
     * @throws InitializationError when a problem occurs during the initialisation of the runner
     */
    public Spockito(final Class<?> clazz) throws InitializationError {
        super(clazz, createRunners(clazz));
    }

    @Override
    public void filter(final Filter filter) throws NoTestsRemainException {
        super.filter(new MethodLevelFilter(filter));
    }

    private static Table classWideTableOrNull(final Class<?> clazz) {
        Unroll unroll = getOnlyConstructor(clazz).getAnnotation(Unroll.class);
        if (unroll == null) {
            unroll = clazz.getAnnotation(Unroll.class);
        }
        return unroll == null ? null : Table.parse(unroll.value());
    }

    private static List<Runner> createRunners(final Class<?> clazz) throws InitializationError {
        final ValueConverter defaultValueConverter = getDefaultValueConverter(clazz);
        final List<Runner> runners = new ArrayList<>();
        final Table classWideTable = classWideTableOrNull(clazz);
        if (classWideTable != null) {
            for (final TableRow row : classWideTable) {
                runners.add(new SingleRowMultiTestRunner(clazz, row, defaultValueConverter));
            }
        } else {
            for (final FrameworkMethod testMethod : new TestClass(clazz).getAnnotatedMethods(Test.class)) {
                runners.add(new SingleTestMultiRowRunner(clazz, testMethod, defaultValueConverter));
            }
        }
        return runners;
    }

    private static Constructor<?> getOnlyConstructor(final Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        Assert.assertEquals(1, constructors.length);
        return constructors[0];
    }

    private static ValueConverter getDefaultValueConverter(final Class<?> clazz) {
        final Spockito.UseValueConverter useValueConverter = clazz.getAnnotation(Spockito.UseValueConverter.class);
        return Spockito.getValueConverter(useValueConverter, SpockitoValueConverter.DEFAULT_INSTANCE);
    }

    static String parameterRefNameOrNull(final Parameter parameter) {
        final Spockito.Ref ref = parameter.getAnnotation(Spockito.Ref.class);
        if (ref == null) {
            return parameter.isNamePresent() ? parameter.getName() : null;
        } else {
            return ref.value();
        }
    }

    static String getName(final Executable executable, final TableRow tableRow) {
        final Name name = executable.getAnnotation(Name.class);
        final String unresolved = name != null ? name.value() : DEFAULT_NAME;
        String resolved = unresolved;
        resolved = resolved.replaceAll("\\{row\\}", String.valueOf(tableRow.getRowIndex()));
        final Table table = tableRow.getTable();
        for (int col = 0; col < table.getColumnCount(); col++) {
            final String value = tableRow.get(col);
            resolved = resolved.replaceAll("\\{" + col + "\\}", value);
            resolved = resolved.replaceAll("\\{" + table.getColumnName(col) + "\\}", value);
        }
        return resolved;
    }

    static ValueConverter getValueConverter(final Spockito.UseValueConverter useValueConverter, final ValueConverter defaultValueConverter) {
        if (useValueConverter != null) {
            try {
                return useValueConverter.value().newInstance();
            } catch (final Exception e) {
                throw new IllegalArgumentException("Could not instantiate ValueConverter of type " + useValueConverter.value(), e);
            }
        }
        return defaultValueConverter;
    }

}