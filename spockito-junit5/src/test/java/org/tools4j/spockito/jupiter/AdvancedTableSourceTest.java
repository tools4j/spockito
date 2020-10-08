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

import java.util.List;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedTableSourceTest {

    public static class FieldBean {
        public int index;
        public String name;
    }

    public static class AccessorBean {
        private int index;
        private String name;
        public void setIndex(int index) {
            this.index = index;
        }

        private int getIndex() {
            return index;
        }
        public void setName(String name) {
            this.name = name;
        }

        private String getName() {
            return name;
        }
    }

    @ParameterizedTest
    @TableSource({
            "| List      | Count |",
            "| 1,2,3,4,5 |   5   |",
            "| 17,99,101 |   3   |"
    })
    //@Spockito.Name("[{row}]")
    public void testUnrollSingleColumnsIntoList(final List<Integer> list, final int count) {
        assertNotNull(list,"list should not be null");
        assertEquals( count, list.size(),"list has wrong number of elements");
    }

    @ParameterizedTest
    @TableSource({
            "| Map                        | Count |",
            "| 1:blue, 2:green, 3: yellow | 3     |",
            "| 1:sky, 2:hello world       | 2     |"
    })
    //@Spockito.Name("[{row}]")
    public void testUnrollSingleColumnsIntoMap(final Map<Integer, String> map, final int count) {
        assertNotNull(map,"map should not be null");
        assertEquals( count, map.size(),"map has wrong number of elements");
    }

    @ParameterizedTest
    @TableSource({
            "| FieldBean           | Name     |",
            "| index:1,name:Test 1 | Test 1   |",
            "| index:2,name:Test 2 | Test 2   |",
    })
    //@Spockito.Name("[{row}]: {1}")
    public void testUnrollSingleColumnsIntoFieldBean(final FieldBean bean, final String testName) {
        assertNotNull(bean,"bean should not be null");
        assertEquals( testName, bean.name,"bean.name not as expected");
    }

    @ParameterizedTest
    @TableSource({
            "| Index | Name     |",
            "| 1     | Test 1   |",
            "| 2     | Test 2   |"
    })
    //@Spockito.Name("[{row}]: {1}")
    public void testUnrollAllColumnsIntoFieldBean(final @Ref("*") FieldBean bean) {
        assertNotNull(bean,"bean should not be null");
        assertTrue(bean.index > 0,"bean.index should be greater than zero");
        assertNotNull(bean.name,"bean.name should not be null");
    }

    @ParameterizedTest
    @TableSource({
            "| Index | Name     |",
            "| 1     | Test 1   |",
            "| 2     | Test 2   |"
    })
    //@Spockito.Name("[{row}]: {1}")
    public void testUnrollAllColumnsIntoAccessorBean(final @Ref("*") AccessorBean bean) {
        assertNotNull(bean,"bean should not be null");
        assertTrue(bean.index > 0,"bean.index should be greater than zero");
        assertNotNull(bean.getName(),"bean.name should not be null");
    }

    @ParameterizedTest
    @TableSource({
            "| Index | Age |",
            "| 1     | 44  |",
            "| 2     | 55  |"
    })
    //@Spockito.Name("[{row}]: {0}:Age={1}")
    public void testUnrollAllColumnsIntoMap(final @Ref("*") Map<String, Integer> map) {
        assertNotNull(map,"map should not be null");
        assertTrue(map.get("Index") > 0,"map.Index should be greater than zero");
        assertTrue(map.get("Age") > 40,"map.Age should be greater than zero");
    }

    @ParameterizedTest
    @TableSource({
            "| List |",
            "| [ { index=1 ; name=cherry }, { index=2 ; name=apple } ] |",
            "| [ { index=10 ; name=rose }, { index=20 ; name=tulip } , { index=30 ; name=erika } ] |"
    })
    public void testUnrollListOfBeans(final @Ref("List") List<FieldBean> list) {
        assertNotNull(list,"list should not be null");
        assertTrue(2 <= list.size(),"list size should be at least 2");
    }
}