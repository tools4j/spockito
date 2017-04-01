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
import org.junit.runner.RunWith;

import java.lang.reflect.Type;

@RunWith(Spockito.class)
@Spockito.UseValueConverter(ConstomConverterTest.MyIntegerConverter.class)
public class ConstomConverterTest {

    static class MyInteger {
        int intValue;
        static MyInteger parse(final String s) {
            final int intValue = Integer.parseInt(s.trim());
            final MyInteger myInteger = new MyInteger();
            myInteger.intValue = intValue;
            return myInteger;
        }
    }
    static class Point {
        int x;
        int y;
        static Point parse(final String s) {
            final String trimmed = s.trim();
            final int comma = trimmed.indexOf(',');
            if (comma >= 0 && trimmed.startsWith("(") && trimmed.endsWith(")")) {
                final int x = Integer.parseInt(trimmed.substring(1, comma));
                final int y = Integer.parseInt(trimmed.substring(comma + 1, trimmed.length() - 1));
                final Point p = new Point();
                p.x = x;
                p.y = y;
                return p;
            }
            throw new IllegalArgumentException("Cannot convert string intValue to Point: " + s);
        }
    }

    static class MyIntegerConverter implements ValueConverter {
        @Override
        public <T> T convert(final Class<T> type, final Type genericType, final String value) {
            if (type == MyInteger.class) {
                return type.cast(MyInteger.parse(value));
            }
            throw new IllegalArgumentException("Conversion not supported for type: " + type);
        }
    }

    static class PointConverter implements ValueConverter {
        @Override
        public <T> T convert(final Class<T> type, final Type genericType, final String value) {
            if (type == Point.class) {
                return type.cast(Point.parse(value));
            }
            throw new IllegalArgumentException("Conversion not supported for type: " + type);
        }
    }

    @Test
    @Spockito.Unroll({
            "| MyInteger |",
            "|-----------|",
            "|     1     |",
            "|     2     |"
    })
    @Spockito.Name("[{row}]: myInteger={0}")
    public void testConverterOnClassLevel(final MyInteger myInteger) {
        Assert.assertNotNull("myInteger should not be null", myInteger);
        Assert.assertTrue("myInteger.inValue should be greater than 0", myInteger.intValue > 0);
    }

    @Test
    @Spockito.Unroll({
            "| Point |",
            "|-------|",
            "| (1,2) |",
            "| (3,4) |"
    })
    @Spockito.Name("[{row}]: point={0}")
    @Spockito.UseValueConverter(ConstomConverterTest.PointConverter.class)
    public void testConverterOnMethodLevel(final Point point) {
        Assert.assertNotNull("point should not be null", point);
        Assert.assertTrue("point.x should be greater than 0", point.x > 0);
        Assert.assertTrue("point.y should be greater than 0", point.y > 0);
    }
}