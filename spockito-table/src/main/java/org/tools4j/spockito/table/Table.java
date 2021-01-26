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

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

/**
 * Represents a table with a header row and zero to many data or separator rows; all table elements are strings.
 */
public interface Table extends Iterable<TableRow> {

    int getColumnCount();
    int getRowCount();

    String getColumnName(int index);
    boolean hasColumn(String columnName);
    int getColumnIndexByName(String columnName);

    TableRow getRow(int rowIndex);
    int getRowIndex(TableRow row);

    String getValue(int rowIndex, int columnIndex);
    String getValue(int rowIndex, String columnName);

    @Override
    Iterator<TableRow> iterator();
    default Stream<TableRow> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    default <T> T to(final Class<T> type) {
        return to(type, type);
    }

    default <T> T to(final Class<T> type, final Type genericType) {
        return to(type, genericType, SpockitoValueConverter.DEFAULT_INSTANCE);
    }

    default <T> T to(final Class<T> type, final Type genericType, final ValueConverter valueConverter) {
        final TableConverter tableConverter = new SpockitoTableConverter(type, genericType, valueConverter);
        return type.cast(tableConverter.convert(this));
    }

    default List<String[]> toList() {
        return stream().map(TableRow::toArray).collect(Collectors.toList());
    }

    default <T> List<T> toList(final Class<T> rowType) {
        return toList(rowType, SpockitoValueConverter.DEFAULT_INSTANCE);
    }

    default <T> List<T> toList(final Class<T> rowType, final ValueConverter valueConverter) {
        requireNonNull(rowType);
        requireNonNull(valueConverter);
        return stream().map(row -> row.to(rowType, valueConverter)).collect(Collectors.toList());
    }
    default <T> List<T> toList(final Class<T> rowType, final Type genericType, final ValueConverter valueConverter) {
        requireNonNull(rowType);
        requireNonNull(genericType);
        requireNonNull(valueConverter);
        return stream().map(row -> row.to(rowType, genericType, valueConverter)).collect(Collectors.toList());
    }

    static Table parse(final String[] headerAndRows) {
        return SpockitoTable.parse(headerAndRows);
    }
}
