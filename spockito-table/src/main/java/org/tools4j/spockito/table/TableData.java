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

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to inject table data into a field, method or method parameter; the table data is define2 in a
 * structure as follows:
 * <pre>
 * | ColumnA   | ColumnB   | ColumnC   |
 * |-----------|-----------|-----------|
 * | value_1_A | value_1_B | value_1_C |
 * | value_2_A | value_2_B | value_2_C |
 * etc...
 * </pre>
 * The separator row after the column headers is optional and = instead of - can be used. Separator rows can be
 * placed anywhere in the table and are ignored when the table is parsed.
 */
@Target({ANNOTATION_TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Inherited
@Data(TableDataProvider.class)
public @interface TableData {
    /**
     * Test case data for parameterized tests structured as follows:
     * <pre>
     * | ColumnA   | ColumnB   | ColumnC   |
     * |-----------|-----------|-----------|
     * | value_1_A | value_1_B | value_1_C |
     * | value_2_A | value_2_B | value_2_C |
     * etc...
     * </pre>
     * The separator row after the column headers is optional and = instead of - can be used. Separator rows can be
     * placed anywhere in the table and are ignored when the table is parsed.
     *
     * @return  An array of strings represented as table data; string[0] contains the header
     *          row with column names
     */
    String[] value();

    /**
     * Converter to use for individual values;  conversion is done purely based on the value type.
     * Returned classes must have a public no-argument constructor.
     *
     * @return the value converter to use
     * @see SpockitoValueConverter
     */
    Class<? extends ValueConverter> valueConverter() default SpockitoValueConverter.class;
}
