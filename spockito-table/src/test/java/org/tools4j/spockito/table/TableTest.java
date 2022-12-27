/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2022 tools4j.org (Marco Terzer)
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

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for {@link Table}
 */
public class TableTest {

    static final class Row {
        int index;
        List<Integer> listOfInteger;
        String emptyString;
    }

    @Test
    public void parseAndAssert() {
        //when
        final Table table = Table.parse(new String[] {
                "| index | listOfInteger | emptyString |",
                "|   0   | [1;2;3;4;5;6] |             |",
                "|   1   | [9;8;7;6;5;4] |     ''      |"
        });

        //then
        assertEquals(2, table.getRowCount(), "unexpected row count");
        assertEquals(3, table.getColumnCount(), "unexpected column count");
        assertEquals("0", table.getValue(0, 0), "value[0][0]");
        assertEquals("0", table.getValue(0, "index"), "value[0]['index']");
        assertEquals("1", table.getValue(1, "index"), "value[1]['index']");
        assertEquals("", table.getValue(0, "emptyString"), "value[1]['index']");
        assertEquals("", table.getValue(1, "emptyString"), "value[1]['index']");
        assertEquals(table.getRow(0), table.toRowList().get(0));
        assertEquals(table.getRow(1), table.toRowList().get(1));
        assertArrayEquals(new String[] {"0", "[1;2;3;4;5;6]", ""}, table.toList().get(0));
        assertArrayEquals(new String[] {"1", "[9;8;7;6;5;4]", ""}, table.toList().get(1));

        //when
        final List<Row> rows = table.toList(Row.class);

        //then
        assertEquals(2, rows.size(), "unexpected row count");
        for (int row = 0; row < rows.size(); row++) {
            assertEquals(row, rows.get(row).index, "unexpected index");
            assertEquals(6, rows.get(row).listOfInteger.size(), "unexpected list size");
            assertEquals("", rows.get(row).emptyString, "string should be empty");
        }
    }

    @Test
    public void filterAndSort() {
        //given
        final Table table = Table.parse(new String[]{
                "| index | listOfInteger | emptyString |",
                "|   0   | [1;1;2;2;3;3] |             |",
                "|   1   | [3;3;4;4;5;5] |     ''      |",
                "|   2   | [4;4;4;5;5;5] |     ''      |",
                "|   3   | [5;5;5;6;7;8] |     ''      |"
        });

        //when
        final Table filtered = table.filter(row -> row.to(Row.class).listOfInteger.contains(5));

        //then
        assertEquals(3, filtered.getRowCount());
        assertEquals(1, filtered.getRow(0).to(Row.class).index);
        assertEquals(2, filtered.getRow(1).to(Row.class).index);
        assertEquals(3, filtered.getRow(2).to(Row.class).index);

        //when
        final Table reversed = filtered.sort(Comparator.<TableRow, String>comparing(row -> row.get("listOfInteger")).reversed());

        //then
        assertEquals(3, reversed.getRowCount());
        assertEquals(3, reversed.getRow(0).to(Row.class).index);
        assertEquals(2, reversed.getRow(1).to(Row.class).index);
        assertEquals(1, reversed.getRow(2).to(Row.class).index);
    }
}
