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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a table with header row and data rows.
 */
public class SpockitoTable implements Table {

    private static final SpockitoTable EMPTY = new SpockitoTable();

    private final TableRow headers;
    private final List<TableRow> data = new ArrayList<>();

    private SpockitoTable() {
        this.headers = SpockitoTableRow.empty(this);
    }

    private SpockitoTable(final String headerString) {
        this.headers = parseRow(this, 0, headerString);
        if (headers.stream().distinct().count() < headers.getColumnCount()) {
            throw new IllegalArgumentException("Duplicate column headers: " + headers);
        }
    }

    public int getColumnCount() {
        //null in constructor when parsing header row
        return headers == null ? 0 : headers.getColumnCount();
    }

    public int getColumnIndexByName(final String columnName) {
        int columnIndex = headers.indexOf(columnName);
        if (columnIndex < 0) {
            if (columnName.length() > 0 && Character.isLowerCase(columnName.charAt(0))) {
                columnIndex = headers.indexOf(Strings.firstCharToUpperCase(columnName));
            }
        }
        if (columnIndex < 0) {
            throw new IllegalArgumentException("No such column: " + columnName);
        }
        return columnIndex;
    }

    public boolean hasColumn(final String columnName) {
        return 0 <= headers.indexOf(columnName);
    }

    public int getRowCount() {
        return data.size();
    }

    public String getColumnName(final int index) {
        return headers.get(index);
    }

    public TableRow getRow(final int rowIndex) {
        return data.get(rowIndex);
    }

    public int getRowIndex(final TableRow row) {
        return data.indexOf(row);
    }

    public String getValue(final int rowIndex, final int columnIndex) {
        return data.get(rowIndex).get(columnIndex);
    }

    public String getValue(final int rowIndex, final String columnName) {
        final int columnIndex = getColumnIndexByName(columnName);
        return data.get(rowIndex).get(columnIndex);
    }

    @Override
    public Iterator<TableRow> iterator() {
        return Collections.unmodifiableList(data).iterator();
    }

    public static <T> T[] parse(final Class<T> rowType, final String[] headerAndRows) {
        return parse(rowType, headerAndRows, new SpockitoValueConverter());
    }

    public static <T> T[] parse(final Class<T> rowType, final String[] headerAndRows, final ValueConverter valueConverter) {
        final SpockitoTable table = parse(headerAndRows);
        final int rows = table.getRowCount();
        final T[] result = (T[])Array.newInstance(rowType, rows);
        for (int row = 0; row < rows; row++) {
            result[row] = valueConverter.convert(rowType, rowType, table.getRow(row).toMap().toString());
        }
        return result;
    }

    public static SpockitoTable parse(final String[] headerAndRows) {
        if (headerAndRows.length > 0) {
            final SpockitoTable table = new SpockitoTable(headerAndRows[0]);
            for (int i = 1; i < headerAndRows.length; i++) {
                final TableRow tableRow = parseRow(table, i, headerAndRows[i]);
                if (!tableRow.isSeparatorRow()) {
                    table.data.add(tableRow);
                }
            }
            return table;
        }
        return SpockitoTable.EMPTY;
    }

    private static TableRow parseRow(final SpockitoTable table, final int row, final String rowString) {
        final String trimmed = rowString.trim();
        if (trimmed.length() < 2 || trimmed.charAt(0) != '|' || trimmed.charAt(trimmed.length() - 1) != '|') {
            throw new IllegalArgumentException("Invalid table data: row " + row + " must start and end with '|'");
        }
        final TableRow tableRow = SpockitoTableRow.parse(table, trimmed);
        if (row != 0) {
            if (tableRow.getColumnCount() > table.getColumnCount()) {
                throw new IllegalArgumentException("Invalid table data: row " + row + " has more columns than header row: " + tableRow.getColumnCount() + " > " + table.getColumnCount());
            }
        }
        return tableRow;
    }

    @Override
    public String toString() {
        return "SpockitoTable{" +
                "headers=" + headers +
                (data.isEmpty() ? "" : data.toString()) +
                '}';
    }
}
