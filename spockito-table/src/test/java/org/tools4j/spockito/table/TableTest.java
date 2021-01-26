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
    public void parse() {
        //when
        final List<Row> rows = Table.parse(new String[] {
                "| index | listOfInteger | emptyString |",
                "|   0   | [1;2;3;4;5;6] |             |",
                "|   1   | [9;8;7;6;5;4] |     ''      |"
        }).toList(Row.class);

        //then
        assertEquals(2, rows.size(), "unexpected row count");
        for (int row = 0; row < rows.size(); row++) {
            assertEquals(row, rows.get(row).index, "unexpected index");
            assertEquals(6, rows.get(row).listOfInteger.size(), "unexpected list size");
            assertEquals("", rows.get(row).emptyString, "string should be empty");
        }
    }
}
