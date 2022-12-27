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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public class SpockitoTableJoiner implements TableJoiner {

    private final Table child;
    private final TableRow parent;

    public SpockitoTableJoiner(final Table child, final TableRow parent) {
        this.child = requireNonNull(child);
        this.parent = requireNonNull(parent);
    }

    @Override
    public JoinBuilder onAllCommonColumns() {
        final LinkedHashSet<String> common = new LinkedHashSet<>(parent.getTable().getColumnNames());
        common.retainAll(child.getColumnNames());
        JoinBuilder result = null;
        for (final String name : common) {
            result = result == null ? on(name, name) : result.and(name, name);
        }
        if (result != null) {
            return result;
        }
        throw new IllegalStateException("No common columns found in parent and child table: " +
                parent.getTable().getColumnNames() + " / " + child.getColumnNames());
    }

    @Override
    public JoinBuilder on(final int child, final int parent) {
        return new Builder().and(child, parent);
    }

    @Override
    public JoinBuilder on(final String child, final String parent) {
        return new Builder().and(child, parent);
    }

    @Override
    public JoinBuilder on(final String common) {
        return on(common, common);
    }

    private class Builder implements JoinBuilder {

        final List<String> children = new ArrayList<>();
        final List<String> parents = new ArrayList<>();

        @Override
        public JoinBuilder and(final int childColumn, final int parentColumn) {
            if (childColumn < 0 || childColumn >= child.getColumnCount()) {
                throw new IllegalArgumentException("Invalid child column index: " + childColumn);
            }
            if (parentColumn < 0 || parentColumn >= parent.getColumnCount()) {
                throw new IllegalArgumentException("Invalid parent column index: " + parentColumn);
            }
            children.add(child.getColumnName(childColumn));
            parents.add(parent.getTable().getColumnName(parentColumn));
            return this;
        }

        @Override
        public JoinBuilder and(final String childColumn, final String parentColumn) {
            if (!child.hasColumn(childColumn)) {
                throw new IllegalArgumentException("Invalid child column name: " + childColumn);
            }
            if (!parent.getTable().hasColumn(parentColumn)) {
                throw new IllegalArgumentException("Invalid parent column name: " + parentColumn);
            }
            children.add(childColumn);
            parents.add(parentColumn);
            return this;
        }

        @Override
        public JoinBuilder and(final String common) {
            return and(common, common);
        }

        @Override
        public Table apply() {
            final String[] parentValues = parents.stream().map(parent::get).toArray(String[]::new);
            final Predicate<TableRow> matcher = childRow -> {
                final String[] childValues = children.stream().map(childRow::get).toArray(String[]::new);
                return Arrays.equals(parentValues, childValues);
            };
            final int childRows = child.getRowCount();
            final List<List<String>> rows = new ArrayList<>();
            for (int i = 0; i < childRows; i++) {
                final TableRow row = child.getRow(i);
                if (matcher.test(row)) {
                    rows.add(row.toList());
                }
            }
            return new SpockitoTable(child.getColumnNames(), rows);
        }
    }
}
