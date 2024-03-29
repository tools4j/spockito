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

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents a single row of a {@link Table}; cell values are kept as strings but convenience methods exist to convert
 * them to a value of any desired type.
 */
public interface TableRow extends Iterable<String> {

    Table getTable();
    int getColumnCount();
    int getRowIndex();
    boolean isSeparatorRow();

    String get(int index);
    String get(String name);
    int indexOf(String value);

    String[] toArray();
    List<String> toList();
    Map<String, String> toMap();
    <T> T to(Class<T> type);
    <T> T to(Class<T> type, ValueConverter valueConverter);
    <T> T to(Class<T> type, Type genericType, ValueConverter valueConverter);

    @Override
    Iterator<String> iterator();
    default Stream<String> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

}