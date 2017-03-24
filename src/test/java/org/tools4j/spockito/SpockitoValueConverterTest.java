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
import org.omg.CORBA.Object;

import static org.junit.Assert.*;

/**
 * Unit test for {@link SpockitoValueConverter}.
 */
public class SpockitoValueConverterTest {

    private enum TestEnum {
        CONST_A,
        CONST_B,
        CONST_FINAL;
    }

    //under test
    private ValueConverter converter = new SpockitoValueConverter();

    @Test
    public void convertNull() {
        assertNull("Should return null", converter.convert(null, null, null));
        assertNull("Should return null", converter.convert(Object.class, Object.class, null));
        assertNull("Should return null", converter.convert(String.class, String.class, null));
        assertNull("Should return null", converter.convert(TestEnum.class, TestEnum.class, null));
        assertNull("Should return null", converter.convert(Double.class, Double.class, null));
    }

    @Test
    public void convertString() {
        assertNull("Should return string unchanged", converter.convert(String.class, String.class, null));
        assertNull("Should return null", converter.convert(String.class, String.class, null));
        assertNull("Should return null", converter.convert(TestEnum.class, String.class, null));
        assertNull("Should return null", converter.convert(Double.class, String.class, null));
    }
}