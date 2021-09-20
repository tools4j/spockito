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
package org.tools4j.spockito.jupiter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.tools4j.spockito.table.Column;
import org.tools4j.spockito.table.Row;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class AdvancedTableSourceTest {

    public static class FieldBean {
        public int index;
        public String name;

        @Override
        public String toString() {
            return "FieldBean{index=" + index + ", name='" + name + '\'' + '}';
        }
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

        @Override
        public String toString() {
            return "AccessorBean{index=" + index + ", name='" + name + '\'' + '}';
        }
    }

    @TableSource({
            "| Index | Name  |",
            "|   0   | Peter |",
            "|   1   | Frank |",
            "|   2   | Henry |"
    })
    private List<FieldBean> fieldBeansAsList;

    @TableSource({
            "| Index | Name  |",
            "|   0   | Peter |",
            "|   1   | Frank |",
            "|   2   | Henry |"
    })
    private AccessorBean[] accessorBeansAsArray;

    @Test
    @ExtendWith(SpockitoExtension.class)
    public void testFieldInjection() {
        assertFieldsAreInjected();
    }

    private void assertFieldsAreInjected() {
        assertNotNull(fieldBeansAsList,"fieldBeansAsList should not be null");
        assertNotNull(accessorBeansAsArray,"accessorBeansAsArray should not be null");
        assertEquals(3, fieldBeansAsList.size(),"fieldBeansAsList has wrong number of elements");
        assertEquals(3, accessorBeansAsArray.length,"fieldBeansAsList has wrong number of elements");
        assertEquals(2, fieldBeansAsList.get(2).index,"fieldBeansAsList.get(2).index");
        assertEquals("Frank", fieldBeansAsList.get(1).name,"fieldBeansAsList.get(1).name");
        assertEquals(1, accessorBeansAsArray[1].index,"accessorBeansAsArray[1].index");
        assertEquals("Peter", accessorBeansAsArray[0].name,"fieldBeansAsList.get(1).name");
    }

    @TableSource({
            "| List      | Count |",
            "| 1,2,3,4,5 |   5   |",
            "| 17,99,101 |   3   |"
    })
    @ExtendWith(SpockitoExtension.class)
    @ParameterizedTest(name = "[{index}]: {0}")
    public void testUnrollSingleColumnsIntoList(final List<Integer> list, final int count) {
        assertNotNull(list,"list should not be null");
        assertEquals( count, list.size(),"list has wrong number of elements");
        assertFieldsAreInjected();
    }

    @TableSource({
            "| Map                        | Count |",
            "| 1:blue, 2:green, 3: yellow | 3     |",
            "| 1:sky, 2:hello world       | 2     |"
    })
    @ParameterizedTest(name = "[{index}]: {0}")
    public void testUnrollSingleColumnsIntoMap(final Map<Integer, String> map, final int count) {
        assertNotNull(map,"map should not be null");
        assertEquals( count, map.size(),"map has wrong number of elements");
    }

    @TableSource({
            "| FieldBean           | Name     |",
            "| index:1,name:Test 1 | Test 1   |",
            "| index:2,name:Test 2 | Test 2   |",
    })
    @ParameterizedTest(name = "[{index}]: {1}")
    public void testUnrollSingleColumnsIntoFieldBean(final FieldBean bean, final String testName) {
        assertNotNull(bean,"bean should not be null");
        assertEquals( testName, bean.name,"bean.name not as expected");
    }

    @TableSource({
            "| Index | Name     |",
            "| 1     | Test 1   |",
            "| 2     | Test 2   |"
    })
    @ParameterizedTest
    public void testUnrollAllColumnsIntoFieldBeanWithAggregator(final @AggregateTableRow FieldBean bean) {
        assertNotNull(bean,"bean should not be null");
        assertTrue(bean.index > 0,"bean.index should be greater than zero");
        assertNotNull(bean.name,"bean.name should not be null");
    }

    @TableSource({
            "| Index | Name     |",
            "| 1     | Test 1   |",
            "| 2     | Test 2   |"
    })
    @ParameterizedTest
    public void testUnrollAllColumnsIntoFieldBeanWithRow(final @Row FieldBean bean) {
        assertNotNull(bean,"bean should not be null");
        assertTrue(bean.index > 0,"bean.index should be greater than zero");
        assertNotNull(bean.name,"bean.name should not be null");
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = {ElementType.FIELD, ElementType.PARAMETER})
    @Column("Name")
    @Documented
    @interface Name {
    }

    @TableSource({
            "| Index | Name     |",
            "| 1     | Test 1   |",
            "| 2     | Test 2   |"
    })
    @ParameterizedTest(name = "[{index}]: {1}")
    public void testUnrollAllColumnsIntoAccessorBean(final @Name String name, final @AggregateTableRow AccessorBean bean) {
        assertNotNull(bean,"bean should not be null");
        assertTrue(bean.index > 0,"bean.index should be greater than zero");
        assertNotNull(bean.getName(),"bean.name should not be null");
        assertEquals(name, bean.getName(),"name should equal bean.name");
    }

    @TableSource({
            "| Index | Age |",
            "| 1     | 44  |",
            "| 2     | 55  |"
    })
    @ParameterizedTest(name = "[{index}]: {0}")
    public void testUnrollAllColumnsIntoMap(final @AggregateTableRow Map<String, Integer> map) {
        assertNotNull(map,"map should not be null");
        assertTrue(map.get("Index") > 0,"map.Index should be greater than zero");
        assertTrue(map.get("Age") > 40,"map.Age should be greater than zero");
    }

    @TableSource({
            "| Index | Age |",
            "| 1     | 44  |",
            "| 2     | 55  |"
    })
    @ParameterizedTest(name = "[{index}]: {0}")
    public void testUnrollAllColumnsIntoMapWithRow(final @Row Map<String, Integer> map) {
        assertNotNull(map,"map should not be null");
        assertTrue(map.get("Index") > 0,"map.Index should be greater than zero");
        assertTrue(map.get("Age") > 40,"map.Age should be greater than zero");
    }

    @TableSource({
            "| List |",
            "| [ { index=1 ; name=cherry }, { index=2 ; name=apple } ] |",
            "| [ { index=10 ; name=rose }, { index=20 ; name=tulip } , { index=30 ; name=erika } ] |"
    })
    @ParameterizedTest(name = "[{index}]: {0}")
    public void testUnrollListOfBeans(final List<FieldBean> list) {
        assertNotNull(list,"list should not be null");
        assertTrue(2 <= list.size(),"list size should be at least 2");
    }

    public static class Person {
        public String first;
        public String last;
        public int age;
    }
    final @TableSource({
            "| First | Last   | Age |",
            "| Peter | Mayer  | 36  |",
            "| Lizzy | Finley | 41  |"
    })
    @ParameterizedTest(name = "[{index}]: {2}")
    public void assertParentChild(final @Row int rowIndex,
                                  final @TableSource({
                                          "| First | Last   | Age |",
                                          "| Nina  | Mayer  |   3 |",
                                          "| Gary  | Mayer  |   1 |",
                                          "| Linda | Finley |  10 |",
                                  }) @JoinOn("Last") List<Person> children,
                                  final @AggregateTableRow Person parent) {
        switch (rowIndex) {
            case 0:
                assertEquals("Peter", parent.first, "parent.first");
                assertEquals("Mayer", parent.last, "parent.last");
                assertEquals(36, parent.age, "parent.age");
                assertEquals(2, children.size(), "myChildren.size");
                assertEquals("Nina", children.get(0).first, "myChildren[0].first");
                assertEquals(3, children.get(0).age, "myChildren[0].age");
                assertEquals("Gary", children.get(1).first, "myChildren[1].first");
                assertEquals(1, children.get(1).age, "myChildren[1].age");
                break;
            case 1:
                assertEquals("Lizzy", parent.first, "parent.first");
                assertEquals("Finley", parent.last, "parent.last");
                assertEquals(41, parent.age, "parent.age");
                assertEquals(1, children.size(), "myChildren.size");
                assertEquals("Linda", children.get(0).first, "myChildren[0].first");
                assertEquals(10, children.get(0).age, "myChildren[0].age");
                break;
            default:
                fail("Unexpected row: " + rowIndex);
                break;
        }
    }
}