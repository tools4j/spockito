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
package org.tools4j.spockito.jupiter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.tools4j.spockito.table.Column;
import org.tools4j.spockito.table.Row;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpockitoExtension.class)
public class SpockitoExtensionTest {

    public static class DataRow {
        public Operation operation;
        public char sign;
        public int operand1;
        public int operand2;
        public int result;
        public int neutralOperand;

        @Override
        public String toString() {
            return operation.name();
        }
    }

    @TableSource({
            "| Operation | Sign | Operand1 | Operand2 | Result | NeutralOperand |",
            "|-----------|------|----------|----------|--------|----------------|",
            "| Add       |   +  |        4 |        7 |     11 |              0 |",
            "| Subtract  |   -  |      111 |       12 |     99 |              0 |",
            "| Multiply  |   *  |       24 |        5 |    120 |              1 |",
            "| Divide    |   /  |       24 |        3 |      8 |              1 |"
    })
    private static DataRow[] staticTestData;

    @TableSource({
            "| Operation |",
            "|-----------|",
            "| Add       |",
            "| Subtract  |",
            "| Multiply  |",
            "| Divide    |"
    })
    private Operation[] operation;

    @Test
    public void testCoversAllOperations() {
        final Operation[] constants = Operation.values();
        assertArrayEquals(constants, operation);
        assertEquals(staticTestData.length, operation.length);
        assertArrayEquals(Arrays.stream(staticTestData).map(row -> row.operation).toArray(), operation);
    }

    @ParameterizedTest(name = "[{index}]: {0}")
    @MethodSource("staticTestData")
    public void testOperation(DataRow data) {
        assertEquals(data.result, data.operation.evaluate(data.operand1, data.operand2),
                "" + data.operand1 + data.sign + data.operand2);
    }

    @ParameterizedTest(name = "[{index}]: {0}")
    @MethodSource("staticTestData")
    public void testNeutralOperand(DataRow data) {
        assertEquals(data.operand1, data.operation.evaluate(data.operand1, data.neutralOperand),
                "" + data.operand1 + data.sign + data.neutralOperand);
        assertEquals(data.operand2, data.operation.evaluate(data.operand2, data.neutralOperand),
                "" + data.operand2 + data.sign + data.neutralOperand);
    }

    @ParameterizedTest(name = "[{index}]: {1}")
    @TableSource({
            "| Value |",
            "| Row 1 |",
            "| Row 2 |"
    })
    public void independentTableSourceTest(final @Row int rowIndex, @Column("Value") final String value) {
        assertTrue(value.startsWith("Row"));
        assertEquals("Row " + (rowIndex + 1), value);
    }

    @TableSource({
            "| Name  | Age |",
            "| Harry |  12 |",
            "| Maya  |  14 |"
    })
    public static void staticInject(final String name, final int age) {
        nameToAge.put(name, age);
    }
    private static final Map<String, Integer> nameToAge = new LinkedHashMap<>();

    @AfterAll
    static void validateNameToAge() {
        assertEquals(2, nameToAge.size());
    }

    @TableSource({
            "| Object    | Vertices |",
            "| Triangle  |    3     |",
            "| Square    |    4     |",
            "| Rectangle |    4     |"
    })
    public void instanceInject(final String object, final int vertices) {
        vertexSum += vertices;
    }
    private int vertexSum;

    @AfterEach
    void validateVertexSum() {
        assertEquals(11, vertexSum);
    }

    static Stream<Arguments> staticTestData() {
        return Arrays.stream(staticTestData).map(Arguments::of);
    }
}
