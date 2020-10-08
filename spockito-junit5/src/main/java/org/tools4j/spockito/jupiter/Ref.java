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
package org.tools4j.spockito.jupiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for fields or parameters of a test method or of the test constructor. Fields
 * need only be annotated if the field name differs from the column name of the test data.
 * Constructor or test method parameters need to be annotated if they are not in the same
 * order as the columns in the test data.
 * <p>
 * The following reference types are supported:
 * <pre>
 * "row" - the current row index (zero based), assignable to an integer type
 * "*" - indicating that all rows are to be used, assignable to a collection type, a map or a Bean
 * "ColumnA" - the value in the "ColumnA" column
 * "ColumnB" - the value in the "ColumnB" column
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.PARAMETER})
public @interface Ref {
    /**
     * Returns the column name, or "row" for row index and "*" to map all column values to the annotated variable.
     * Can be omitted when annotating fields and the field name is identical to the column name.
     *
     * @return the column name, or "row" for the row index, or "*" to indicate that all all column values should
     *         be mapped to the annotated variable (for list, map and bean types)
     */
    String value() default "";
}