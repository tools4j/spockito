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
package org.tools4j.spockito.table;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static org.tools4j.spockito.table.Strings.EMPTY_STRING_ARRAY;
import static org.tools4j.spockito.table.Strings.UNESCAPED_PIPE;

public class SpockitoTableRow implements TableRow {

    private final Table table;
    private final List<String> values;

    public SpockitoTableRow(final Table table) {
        this(table, new ArrayList<>());
    }

    SpockitoTableRow(final Table table, final List<String> values) {
        this.table = requireNonNull(table);
        this.values = requireNonNull(values);
    }

    public static SpockitoTableRow empty(final Table table) {
        return new SpockitoTableRow(table);
    }

    public static SpockitoTableRow parse(final Table table, final String rowString) {
        final String noBars = Strings.removeSurroundingPipes(rowString);
        final String[] parts = UNESCAPED_PIPE.split(noBars);
        final SpockitoTableRow tableRow = new SpockitoTableRow(table);
        for (final String part : parts) {
            tableRow.values.add(Converters.STRING_CONVERTER.apply(Strings.unescape(part.trim())));
        }
        for (int i = parts.length; i < table.getColumnCount(); i++) {
            tableRow.values.add(null);
        }
        return tableRow;
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public boolean isSeparatorRow() {
        return values.stream().anyMatch(s -> s.contains("-") || s.contains("=")) &&
                values.stream().allMatch(s -> Strings.allCharsMatchingAnyOf(s, '-', '='));
    }

    @Override
    public int getColumnCount() {
        return values.size();
    }

    @Override
    public String get(final int index) {
        return values.get(index);
    }

    public String get(final String column) {
        final int index = table.getColumnIndexByName(column);
        if (index >= 0) {
            return get(index);
        }
        throw new IllegalArgumentException("No such column: " + column);
    }

    @Override
    public int indexOf(final String value) {
        return values.indexOf(value);
    }

    @Override
    public int getRowIndex() {
        return table.getRowIndex(this);
    }

    @Override
    public String[] toArray() {
        return values.toArray(EMPTY_STRING_ARRAY);
    }

    @Override
    public List<String> toList() {
        return new ArrayList<>(values);
    }

    @Override
    public Map<String, String> toMap() {
        final int cols = getColumnCount();
        final Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < cols; i++) {
            map.put(table.getColumnName(i), get(i));
        }
        return map;
    }

    @Override
    public <T> T to(final Class<T> type) {
        return to(type, SpockitoValueConverter.DEFAULT_INSTANCE);
    }

    @Override
    public <T> T to(final Class<T> type, final ValueConverter valueConverter) {
        return to(type, type, valueConverter);
    }

    @Override
    public <T> T to(final Class<T> type, Type genericType, final ValueConverter valueConverter) {
        return valueConverter.convert(type, genericType, toMap().toString());
    }

    @Override
    public Iterator<String> iterator() {
        return unmodifiableList(values).iterator();
    }

    @Override
    public Spliterator<String> spliterator() {
        return unmodifiableList(values).spliterator();
    }

    @Override
    public Stream<String> stream() {
        return unmodifiableList(values).stream();
    }

    @Override
    public String toString() {
        return "row(" + getRowIndex() + ")=" + values;
    }
}