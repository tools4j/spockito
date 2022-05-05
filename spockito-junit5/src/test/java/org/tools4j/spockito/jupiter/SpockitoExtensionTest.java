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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.tools4j.spockito.table.TableRow;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpockitoExtension.class)
@Disabled
public class SpockitoExtensionTest {

    public static class DataRow {
        Operation operation;
        char sign;
        int operand1;
        int operand2;
        int result;
        int neutralOperand;
    }

    @TableSource({
            "| Operation | Sign | Operand1 | Operand2 | Result | NeutralOperand |",
            "|-----------|------|----------|----------|--------|----------------|",
            "| Add       |   +  |        4 |        7 |     11 |              0 |",
            "| Subtract  |   -  |      111 |       12 |     99 |              0 |",
            "| Multiply  |   *  |       24 |        5 |    120 |              1 |",
            "| Divide    |   /  |       24 |        3 |      8 |              1 |"
    })
    private TableRow[] testData;

    @ParameterizedTest(name = "[{index}]: {2} {1} {3} = {4}")
    public void testOperation(Operation operation, char sign, int operand1, int operand2, int result) {
        assertEquals(result, operation.evaluate(operand1, operand2), "Result is wrong!");
    }

    @ParameterizedTest(name = "[{index}]: {2} {1} {3} = {2}")
    public void testNeutralOperand(Operation operation, char sign, int operand1, int neutralOperand) {
        assertEquals(operand1, operation.evaluate(operand1, neutralOperand), "Result with neutral operand is wrong!");
    }
}
