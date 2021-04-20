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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

public class SpockitoTableJoiner implements TableJoiner {

    private final Table left;
    private final Table right;

    public SpockitoTableJoiner(final Table left, final Table right) {
        this.left = requireNonNull(left);
        this.right = requireNonNull(right);
    }

    @Override
    public JoinTypes on(final int left, final int right) {
        return new SpockitoJoinTypes().and(left, right);
    }

    @Override
    public JoinTypes on(final String left, final String right) {
        return new SpockitoJoinTypes().and(left, right);
    }

    private class SpockitoJoinTypes implements JoinTypes {

        final IntArr lefts = new IntArr();
        final IntArr rights = new IntArr();

        @Override
        public JoinTypes and(final int leftColumn, final int rightColumn) {
            if (leftColumn < 0 || leftColumn >= left.getColumnCount()) {
                throw new IllegalArgumentException("Invalid left column index: " + leftColumn);
            }
            if (rightColumn < 0 || rightColumn >= right.getColumnCount()) {
                throw new IllegalArgumentException("Invalid right column index: " + rightColumn);
            }
            lefts.add(leftColumn);
            rights.add(rightColumn);
            return this;
        }

        @Override
        public JoinTypes and(final String leftColumn, final String rightColumn) {
            return and(left.getColumnIndexByName(leftColumn), right.getColumnIndexByName(rightColumn));
        }

        @Override
        public Table asInnerJoin() {
            return new SpockitoTable(mergeHeaders(), joinFromLeft(false));
        }

        @Override
        public Table asRightJoin() {
            return new SpockitoTable(mergeHeaders(), joinFromLeft(true));
        }

        @Override
        public Table asLeftJoin() {
            return new SpockitoTable(mergeHeaders(), joinFromRight(false));
        }

        @Override
        public Table asFullOuterJoin() {
            final List<List<String>> data = joinFromLeft(true);
            data.addAll(joinFromRight(true));
            return new SpockitoTable(mergeHeaders(), data);
        }

        private List<List<String>> joinFromLeft(final boolean outerJoin) {
            final int leftRows = left.getRowCount();
            final int rightRows = right.getRowCount();
            final IntArr leftIdx = new IntArr();
            final IntArr rightIdx = new IntArr();
            for (int i = 0; i < leftRows; i++) {
                final TableRow leftRow = left.getRow(i);
                final String[] leftValues = lefts.stream().mapToObj(leftRow::get).toArray(String[]::new);
                final Predicate<TableRow> matcher = rightRow -> {
                    final String[] rightValues = rights.stream().mapToObj(rightRow::get).toArray(String[]::new);
                    return Arrays.equals(leftValues, rightValues);
                };
                boolean empty = true;
                for (int j = 0; j < rightRows; j++) {
                    if (matcher.test(right.getRow(j))) {
                        leftIdx.add(i);
                        rightIdx.add(j);
                        empty = false;
                    }
                }
                if (empty && outerJoin) {
                    leftIdx.add(i);
                    rightIdx.add(-1);
                }
            }
            return mergeData(leftIdx.toArray(), rightIdx.toArray());
        }

        private List<List<String>> joinFromRight(final boolean outerJoinOnly) {
            final int leftRows = left.getRowCount();
            final int rightRows = right.getRowCount();
            final IntArr leftIdx = new IntArr();
            final IntArr rightIdx = new IntArr();
            for (int i = 0; i < rightRows; i++) {
                final TableRow rightRow = right.getRow(i);
                final String[] rightValues = rights.stream().mapToObj(rightRow::get).toArray(String[]::new);
                final Predicate<TableRow> matcher = leftRow -> {
                    final String[] leftValues = lefts.stream().mapToObj(leftRow::get).toArray(String[]::new);
                    return Arrays.equals(rightValues, leftValues);
                };
                boolean empty = true;
                for (int j = 0; j < rightRows; j++) {
                    if (matcher.test(right.getRow(j))) {
                        empty = false;
                        if (outerJoinOnly) {
                            break;
                        }
                        rightIdx.add(i);
                        leftIdx.add(j);
                    }
                }
                if (empty) {
                    rightIdx.add(i);
                    leftIdx.add(-1);
                }
            }
            return mergeData(leftIdx.toArray(), rightIdx.toArray());
        }

        private List<String> mergeHeaders() {
            final Set<String> uniqueNames = new LinkedHashSet<>();
            for (int i = 0; i < left.getColumnCount(); i++) {
                addName(uniqueNames, left.getColumnName(i));
            }
            for (int i = 0; i < right.getColumnCount(); i++) {
                addName(uniqueNames, right.getColumnName(i));
            }
            return new ArrayList<>(uniqueNames);
        }

        private List<List<String>> mergeData(final int[] leftRows, final int[] rightRows) {
            final int rows = Math.max(leftRows.length, rightRows.length);
            final int leftCols = left.getColumnCount();
            final int rightCols = right.getColumnCount();
            final int cols = leftCols + rightCols;
            final List<List<String>> data = new ArrayList<>(rows);
            for (int i = 0; i < rows; i++) {
                final List<String> row = new ArrayList<>(cols);
                for (int j = 0; j < leftCols; j++) {
                    row.add(leftRows[i] >= 0 ? left.getValue(leftRows[i], j) : null);
                }
                for (int j = 0; j < rightCols; j++) {
                    row.add(rightRows[i] >= 0 ? right.getValue(rightRows[i], j) : null);
                }
                data.add(row);
            }
            return data;
        }

        private void addName(final Set<String> uniqueNames, final String name) {
            if (uniqueNames.add(name)) {
                return;
            }
            for (int j = 0; j >= 0; j++) {
                String indexedName = name + "_" + j;
                if (uniqueNames.add(indexedName)) {
                    return;
                }
            }
            throw new IllegalArgumentException("Indexing of name failed: " + name);
        }
    }

    private static class IntArr {
        int[] array;
        int size = 0;
        void add(final int value) {
            ensureCapacity(size + 1);
            array[size++] = value;
        }
        int[] toArray() {
            return Arrays.copyOf(array, size);
        }
        IntStream stream() {
            return Arrays.stream(array, 0, size);
        }
        void ensureCapacity(final int capacity) {
            if (capacity > array.length) {
                final int newLen = Math.max(capacity, 2 * array.length);
                array = Arrays.copyOf(array, newLen);
            }
        }
    }
}
