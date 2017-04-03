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

import org.junit.runners.model.FrameworkField;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Represents a single row of a {@link Table} defined via {@link org.tools4j.spockito.Spockito.Unroll} annotaiton.
 */
public class TableRow {

    public static final String REF_ROW = "row";
    public static final String REF_ALL = "*";

    private static final Pattern UNESCAPED_PIPE = Pattern.compile("(?<=[^\\\\])\\|");

    private final Table table;
    private final List<String> values = new ArrayList<>();

    public TableRow(final Table table) {
        this.table = Objects.requireNonNull(table);
    }

    public static TableRow empty(final Table table) {
        return new TableRow(table);
    }

    public static TableRow parse(final Table table, final String rowString) {
        final String noBars = trimChar(rowString, '|');
        final String[] parts = UNESCAPED_PIPE.split(noBars);
        final TableRow tableRow = new TableRow(table);
        for (final String part : parts) {
            tableRow.values.add(Converters.STRING_CONVERTER.apply(part.trim()));
        }
        for (int i = parts.length; i < table.getColumnCount(); i++) {
            tableRow.values.add(null);
        }
        return tableRow;
    }

    public Table getTable() {
        return table;
    }

    public boolean isSeparatorRow() {
        return values.stream().allMatch(s -> trimChar(s, '-').isEmpty() || trimChar(s, '=').isEmpty());
    }

    public boolean isValidRefName(final String refName) {
        return REF_ROW.equals(refName) || REF_ALL.equals(refName) || table.hasColumn(refName);
    }

    public Object[] convertValues(final Executable executable, final ValueConverter valueConverter) {
        final Object[] converted = new Object[executable.getParameterCount()];
        final Parameter[] parameters = executable.getParameters();
        for (int i = 0; i < converted.length; i++) {
            final String refName = Spockito.parameterRefNameOrNull(parameters[i]);
            converted[i] = convertValue(refName, i, parameters[i].getType(), parameters[i].getParameterizedType(), valueConverter);
        }
        return converted;
    }

    public Object[] convertValues(final List<FrameworkField> fields, final ValueConverter valueConverter) {
        final Object[] converted = new Object[fields.size()];
        for (int i = 0; i < converted.length; i++) {
            final Field field = fields.get(i).getField();
            final String refValue = fields.get(i).getAnnotation(Spockito.Ref.class).value();
            final String refName = refValue.isEmpty() ? field.getName() : refValue;
            converted[i] = convertValue(refName, -1, field.getType(), field.getGenericType(), valueConverter);
        }
        return converted;
    }

    private Object convertValue(final String refNameOrNull, final int defaultColumnIndex,
                                final Class<?> type, final Type genericType, final ValueConverter valueConverter) {
        final String value;
        if (REF_ROW.equals(refNameOrNull)) {
            value = String.valueOf(getRowIndex());
        } else if (REF_ALL.equals(refNameOrNull)) {
            value = asMap().toString();
        } else {
            try {
                final int columnIndex = refNameOrNull == null ? defaultColumnIndex : table.getColumnIndexByName(refNameOrNull);
                value = get(columnIndex);
            } catch (final Exception e) {
                throw new IllegalArgumentException("Could not access column value " + refName(refNameOrNull, defaultColumnIndex), e);
            }
        }
        try {
            return valueConverter.convert(type, genericType, value);
        } catch (final Exception e) {
            throw new IllegalArgumentException("Conversion to " + genericType + " failed for column '" +
                    refName(refNameOrNull, defaultColumnIndex) + "' value: " + value, e);
        }
    }

    final Object refName(final String refNameOrNull, final int defaultColumnIndex) {
        return refNameOrNull != null ? refNameOrNull : defaultColumnIndex;
    }

    public int size() {
        return values.size();
    }

    public int distinctCount() {
        return (int)values.stream().distinct().count();
    }

    public String get(final int index) {
        return values.get(index);
    }

    public int indexOf(final String value) {
        return values.indexOf(value);
    }

    public int getRowIndex() {
        return table.getRowIndex(this);
    }

    public Map<String, String> asMap() {
        final Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < size(); i++) {
            map.put(table.getColumnName(i), get(i));
        }
        return map;
    }

    @Override
    public String toString() {
        return "TableRow" + values;
    }

    private static String trimChar(final String s, final char trim) {
        int start = 0;
        int end = s.length();
        while (start < end && s.charAt(start) == trim) {
            start++;
        }
        while (end > start && s.charAt(end - 1) == trim) {
            end--;
        }
        return s.substring(start, end);
    }}
