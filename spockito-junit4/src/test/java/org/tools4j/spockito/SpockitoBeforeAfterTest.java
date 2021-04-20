/*
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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.tools4j.spockito.Spockito.Unroll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SpockitoBeforeAfterTest extends UnrollMethodDataTest {

    private static final List<String> BEFORE_TESTS = new ArrayList<>();
    private static final List<String> AFTER_TESTS = new ArrayList<>();
    private static final List<String> EXPECTED_TESTS = Arrays.asList(
            "oneMore",
            "oneMoreUnroll[0]", "oneMoreUnroll[1]",
            "testUnrollAngularSums[Pentagon]", "testUnrollAngularSums[Square]", "testUnrollAngularSums[Triangle]",
            "testUnrollBirthdays[0]", "testUnrollBirthdays[1]",
            "testUnrollNameAndYearOnly[0]", "testUnrollNameAndYearOnly[1]",
            "testUnrollNames[0]", "testUnrollNames[1]"
    );

    @Rule
    public final TestName testName = new TestName();

    private String testMethodNameWithIndex() {
        final String methodName = testName.getMethodName();
        final int index = methodName == null ? -1 : methodName.indexOf(']');
        return index < 0 ? methodName : methodName.substring(0, index + 1);
    }

    @BeforeClass
    public static void beforeClass() {
        System.out.println("before-class");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("after-class");
        final List<String> exp = new ArrayList<>(EXPECTED_TESTS);
        final List<String> bef = new ArrayList<>(BEFORE_TESTS);
        final List<String> aft = new ArrayList<>(AFTER_TESTS);
        Collections.sort(exp);
        Collections.sort(bef);
        Collections.sort(aft);
        assertEquals("Unexpected before test invocations", exp, bef);
        assertEquals("Unexpected after test invocations", exp, aft);
    }

    @Before
    public void beforeEach() {
        System.out.println("before-each: " + testName.getMethodName());
        BEFORE_TESTS.add(testMethodNameWithIndex());
    }

    @After
    public void afterEach() {
        System.out.println("after-each: " + testName.getMethodName());
        AFTER_TESTS.add(testMethodNameWithIndex());
    }

    @Test
    public void oneMore() {
        Assert.assertTrue("hello world one more time", true);
    }

    @Test
    @Unroll({
            "| Name     |",
            "| Test 1   |",
            "| Test 2   |"
    })
    public void oneMoreUnroll(final String name) {
        Assert.assertNotNull("name should not be null", name);
        Assert.assertTrue("name should start with Test", name.startsWith("Test "));
    }
}