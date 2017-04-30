/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 tools4j.org (Marco Terzer)
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

import java.util.List;
import java.util.Map;

@RunWith(Spockito.class)
public class AdvancedDataTypesTest {

    private static class FieldBean {
        int index;
        String name;
    }

    private static class AccessorBean {
        private int index;
        private String name;
        void setIndex(int index) {
            this.index = index;
        }

        private int getIndex() {
            return index;
        }
        void setName(String name) {
            this.name = name;
        }

        private String getName() {
            return name;
        }
    }

    @Test
    @Spockito.Unroll({
            "| List      | Count |",
            "| 1,2,3,4,5 |   5   |",
            "| 17,99,101 |   3   |"
    })
    @Spockito.Name("[{row}]")
    public void testUnrollSingleColumnsIntoList(final List<Integer> list, final int count) {
        Assert.assertNotNull("list should not be null", list);
        Assert.assertEquals("list has wrong number of elements", count, list.size());
    }

    @Test
    @Spockito.Unroll({
            "| Map                        | Count |",
            "| 1:blue, 2:green, 3: yellow | 3     |",
            "| 1:sky, 2:hello world       | 2     |"
    })
    @Spockito.Name("[{row}]")
    public void testUnrollSingleColumnsIntoMap(final Map<Integer, String> map, final int count) {
        Assert.assertNotNull("map should not be null", map);
        Assert.assertEquals("map has wrong number of elements", count, map.size());
    }

    @Test
    @Spockito.Unroll({
            "| FieldBean           | Name     |",
            "| index:1,name:Test 1 | Test 1   |",
            "| index:2,name:Test 2 | Test 2   |",
    })
    @Spockito.Name("[{row}]: {1}")
    public void testUnrollSingleColumnsIntoFieldBean(final FieldBean bean, final String testName) {
        Assert.assertNotNull("bean should not be null", bean);
        Assert.assertEquals("bean.name not as expected", testName, bean.name);
    }

    @Test
    @Spockito.Unroll({
            "| Index | Name     |",
            "| 1     | Test 1   |",
            "| 2     | Test 2   |"
    })
    @Spockito.Name("[{row}]: {1}")
    public void testUnrollAllColumnsIntoFieldBean(final @Spockito.Ref("*") FieldBean bean) {
        Assert.assertNotNull("bean should not be null", bean);
        Assert.assertTrue("bean.index should be greater than zero", bean.index > 0);
        Assert.assertNotNull("bean.name should not be null", bean.name);
    }

    @Test
    @Spockito.Unroll({
            "| Index | Name     |",
            "| 1     | Test 1   |",
            "| 2     | Test 2   |"
    })
    @Spockito.Name("[{row}]: {1}")
    public void testUnrollAllColumnsIntoAccessorBean(final @Spockito.Ref("*") AccessorBean bean) {
        Assert.assertNotNull("bean should not be null", bean);
        Assert.assertTrue("bean.index should be greater than zero", bean.index > 0);
        Assert.assertNotNull("bean.name should not be null", bean.getName());
    }

    @Test
    @Spockito.Unroll({
            "| Index | Age |",
            "| 1     | 44  |",
            "| 2     | 55  |"
    })
    @Spockito.Name("[{row}]: {0}:Age={1}")
    public void testUnrollAllColumnsIntoMap(final @Spockito.Ref("*") Map<String, Integer> map) {
        Assert.assertNotNull("map should not be null", map);
        Assert.assertTrue("map.Index should be greater than zero", map.get("Index") > 0);
        Assert.assertTrue("map.Age should be greater than zero", map.get("Age") > 40);
    }

    @Test
    @Spockito.Unroll({
            "| List |",
            "| [ { index=1 ; name=cherry }, { index=2 ; name=apple } ] |",
            "| [ { index=10 ; name=rose }, { index=20 ; name=tulip } , { index=30 ; name=erika } ] |"
    })
    public void testUnrollListOfBeans(final @Spockito.Ref("List") List<FieldBean> list) {
        Assert.assertNotNull("list should not be null", list);
        Assert.assertTrue("list size should be at least 2", 2 <= list.size());
    }
}