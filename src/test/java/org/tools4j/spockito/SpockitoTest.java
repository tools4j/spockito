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

@RunWith(Spockito.class)
public class SpockitoTest {

    @Test
    public void testNormal() {
        Assert.assertTrue("hello world", true);
    }

    @Test
    @Spockito.Unroll({
            "| TestName |",
            "| Test 1   |",
            "| Test 2   |"
    })
    public void testUnroll() {
        Assert.assertTrue("hello world", true);
    }

    @Test
    @Spockito.Unroll({
            "| TestName |",
            "| Test 1   |",
            "| Test 2   |"
    })
    public void testUnrollWithString(final String name) {
        Assert.assertNotNull("name should be TestName", name);
        Assert.assertTrue("name should start with Test", name.startsWith("Test "));
    }

    @Test
    @Spockito.Unroll({
            "| Index | TestName |",
            "| 1     | Test 1   |",
            "| 2     | Test 2   |"
    })
    @Spockito.Name("[{row}]: index={0}, name={1}")
    public void testUnrollWithIndex(int index, String name) {
        Assert.assertTrue("index should be greater than 0", index > 0);
        Assert.assertNotNull("name should be TestName", name);
        Assert.assertTrue("name should start with Test", name.startsWith("Test "));
    }

    @Test
    @Spockito.Unroll({
            "| Index | TestName |",
            "|-------|----------|",
            "| 1     | Test 1   |",
            "| 2     | Test 2   |",
            "| 3     | Test 3   |",
            "| 4     | Test 4   |",
    })
    @Spockito.Name("[{row}]: index={Index}, name={TestName}")
    public void testUnrollWithSeparatorRowAndColumnRef(@Spockito.Ref("TestName") String name, @Spockito.Ref("Index") int index, @Spockito.Ref("row") int row) {
        Assert.assertTrue("index should be greater than 0", index > 0);
        Assert.assertTrue("row should be greated or equal to 0", row >= 0);
        Assert.assertTrue("index should be row + 1", index == row + 1);
        Assert.assertNotNull("name should be TestName", name);
        Assert.assertTrue("name should start with Test", name.startsWith("Test "));
    }
}