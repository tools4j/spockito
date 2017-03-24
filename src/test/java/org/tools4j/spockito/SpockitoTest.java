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

import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

@RunWith(Spockito.class)
public class SpockitoTest {

    private static class FieldBean {
        int index;
        String testName;
    }
    private static class SetterBean {
        private int index;
        private String testName;
        void setIndex(int index) {
            this.index = index;
        }
        void setTestName(String testName) {
            this.testName = testName;
        }
    }

    @Rule
    public final TestName testName = new TestName();

    @BeforeClass
    public static void beforeClass() {
        System.out.println("before-class");
    }
    @AfterClass
    public static void afterClass() {
        System.out.println("after-class");
    }
    @Before
    public void beforeEach() {
        System.out.println("before-each: " + testName.getMethodName());
    }
    @After
    public void afterEach() {
        System.out.println("after-each: " + testName.getMethodName());
    }

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
        Assert.assertNotNull("name should not be null", name);
        Assert.assertTrue("name should start with Test", name.startsWith("Test "));
    }

    @Test
    @Spockito.Unroll({
            "| List      | Count |",
            "| 1,2,3,4,5 |   5   |",
            "| 17,99,101 |   3   |"
    })
    public void testUnrollWithList(final List<Integer> list, final int count) {
        Assert.assertNotNull("list should not be null", list);
        Assert.assertEquals("list has wrong number of elements", count, list.size());
    }

    @Test
    @Spockito.Unroll({
            "| Map                        | Count |",
            "| 1:blue, 2:green, 3: yellow | 3     |",
            "| 1:sky, 2:hello world       | 2     |"
    })
    public void testUnrollWithMap(final Map<Integer, String> map, final int count) {
        Assert.assertNotNull("map should not be null", map);
        Assert.assertEquals("map has wrong number of elements", count, map.size());
    }

    @Test
    @Spockito.Unroll({
            "| FieldBean               | TestName |",
            "| index:1,testName:Test 1 | Test 1   |",
            "| index:2,testName:Test 2 | Test 2   |",
    })
    public void testUnrollWithBean(final FieldBean bean, final String testName) {
        Assert.assertNotNull("bean should not be null", bean);
        Assert.assertEquals("bean.testName not as expected", testName, bean.testName);
    }

    @Test
    @Spockito.Unroll({
            "| Index | TestName |",
            "| 1     | Test 1   |",
            "| 2     | Test 2   |"
    })
    public void testUnrollWithAllFieldBean(final @Spockito.Ref("*") FieldBean bean) {
        Assert.assertNotNull("bean should not be null", bean);
        Assert.assertTrue("bean.index should be greater than zero", bean.index > 0);
        Assert.assertNotNull("bean.testName should not be null", bean.testName);
    }

    @Test
    @Spockito.Unroll({
            "| Index | Age |",
            "| 1     | 44  |",
            "| 2     | 55  |"
    })
    public void testUnrollWithAllMap(final @Spockito.Ref("*") Map<String, Integer> map) {
        Assert.assertNotNull("map should not be null", map);
        Assert.assertTrue("map.Index should be greater than zero", map.get("Index") > 0);
        Assert.assertTrue("map.Age should be greater than zero", map.get("Age") > 40);
    }

    @Test
    @Spockito.Unroll({
            "| Index | TestName |",
            "| 1     | Test 1   |",
            "| 2     | Test 2   |"
    })
    public void testUnrollWithAllSetterBean(final @Spockito.Ref("*") SetterBean bean) {
        Assert.assertNotNull("bean should not be null", bean);
        Assert.assertTrue("bean.index should be greater than zero", bean.index > 0);
        Assert.assertNotNull("bean.testName should not be null", bean.testName);
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