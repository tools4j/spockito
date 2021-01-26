/**
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
package org.tools4j.spockito.table;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for {@link SpockitoData}
 */
public class SpockitoDataTest {

    static class Person {
        String name;
        int age;
    }

    static class Car {
        String manufacturer;
        String model;
        int year;
    }

    @Test
    public void initDataField() {
        //given
        final class FieldData extends SpockitoData {
            @TableData({
                    "| A   | B   | C   |",
                    "| 0/0 | 0/1 | 0/2 |",
                    "| 1/0 | 1/1 | 1/2 |",
                    "| 2/0 | 2/1 | 2/2 |",
            })
            Table table;

            @TableData({
                    "| Name  | Age |",
                    "| Frank |  27 |",
                    "| James |  29 |"
            })
            Person[] persons;

            @TableData({
                    "| Manufacturer | Model | Year |",
                    "| Volkswagen   | Golf  | 2021 |",
                    "| BMW          | 528i  | 2020 |",
            })
            List<Car> cars;
        }

        //when
        final FieldData data = new FieldData();

        //then
        assertNotNull(data.table, "data.table");
        assertNotNull(data.persons, "data.persons");
        assertNotNull(data.cars, "data.cars");
        assertEquals(3, data.table.getRowCount(), "data.table.rowCount");
        assertEquals(3, data.table.getColumnCount(), "data.table.columnCount");
        assertEquals(2, data.persons.length, "data.persons.length");
        assertEquals(2, data.cars.size(), "data.cars.size");
        assertEquals("1/2", data.table.getValue(1, 2), "data.value[1, 2]");
        assertEquals("Frank", data.persons[0].name, "data.persons[0].name");
        assertEquals(29, data.persons[1].age, "data.persons[1].age");
        assertEquals("Golf", data.cars.get(0).model, "data.cars[0].model");
        assertEquals(2021, data.cars.get(0).year, "data.cars[0].year");
        assertEquals("BMW", data.cars.get(1).manufacturer, "data.cars[1].manufacturer");
        assertEquals(2020, data.cars.get(1).year, "data.cars[1].year");
    }

    @Test
    public void initDataMethod() throws Exception {
        //given
        final class MethodData extends SpockitoData {
            int personCount;
            int carCount;
            int parentChildCount;

            @TableData({
                    "| Name  | Age |",
                    "| Frank |  27 |",
                    "| James |  29 |"
            })
            void assertPersons(final String name, final int age, final @Row int row) {
                switch (row) {
                    case 0:
                        assertEquals("Frank", name, "name[0]");
                        assertEquals(27, age, "age[0]");
                        break;
                    case 1:
                        assertEquals("James", name, "name[1]");
                        assertEquals(29, age, "age[1]");
                        break;
                    default:
                        fail("Unexpected row: " + row);
                        break;
                }
                personCount++;
            }

            @TableData({
                    "| Manufacturer | Model | Year |",
                    "| Volkswagen   | Golf  | 2021 |",
                    "| BMW          | 528i  | 2020 |",
            })
            void assertCars(final String manufacturer, final String model, final int year, final @Row TableRow row) {
                switch (row.getRowIndex()) {
                    case 0:
                        assertEquals("Volkswagen", manufacturer, "manufacturer[0]");
                        assertEquals("Golf", model, "model[0]");
                        assertEquals(2021, year, "year[0]");
                        break;
                    case 1:
                        assertEquals("BMW", manufacturer, "manufacturer[1]");
                        assertEquals("528i", model, "model[1]");
                        assertEquals(2020, year, "year[1]");
                        break;
                    default:
                        fail("Unexpected row: " + row.getRowIndex());
                        break;
                }
                carCount++;
            }

            final @TableData({
                    "| First | Last   | Age |",
                    "| Peter | Mayer  | 36  |",
                    "| Lizzy | Finley | 41  |"
            })
            void assertParentChild(final TableRow parent,
                                   final @TableData({
                                           "| First | Last   | Age |",
                                           "| Nina  | Mayer  |   3 |",
                                           "| Gary  | Mayer  |   1 |",
                                           "| Linda | Finley |  10 |",
                                   }) Table children) {
                final List<TableRow> myChildren = children.stream().filter(
                        childRow -> Objects.equals(parent.get(1), childRow.get(1))
                ).collect(Collectors.toList());
                switch (parent.getRowIndex()) {
                    case 0:
                        assertEquals("Peter", parent.get(0), "parent[0]");
                        assertEquals("Mayer", parent.get(1), "parent[1]");
                        assertEquals("36", parent.get(2), "parent[2]");
                        assertEquals(2, myChildren.size(), "myChildren.size");
                        assertEquals("Nina", myChildren.get(0).get(0), "myChildren[0][0]");
                        assertEquals("3", myChildren.get(0).get(2), "myChildren[0][2]");
                        assertEquals("Gary", myChildren.get(1).get(0), "myChildren[1][0]");
                        assertEquals("1", myChildren.get(1).get(2), "myChildren[1][2]");
                        break;
                    case 1:
                        assertEquals("Lizzy", parent.get(0), "parent[0]");
                        assertEquals("Finley", parent.get(1), "parent[1]");
                        assertEquals("41", parent.get(2), "parent[2]");
                        assertEquals(1, myChildren.size(), "myChildren.size");
                        assertEquals("Linda", myChildren.get(0).get(0), "myChildren[0][0]");
                        assertEquals("10", myChildren.get(0).get(2), "myChildren[0][2]");
                        break;
                    default:
                        fail("Unexpected row: " + parent.getRowIndex());
                        break;
                }
                parentChildCount++;
            }
        }
        assertNotNull(MethodData.class.getDeclaredMethod("assertPersons", String.class, int.class, int.class)
            .getParameters()[2].getAnnotation(Row.class)
        );

        //when
        final MethodData data = new MethodData();

        //then
        assertEquals(2, data.personCount, "data.personCount");
        assertEquals(2, data.carCount, "data.carCount");
        assertEquals(2, data.parentChildCount, "data.parentChildCount");
    }
}
