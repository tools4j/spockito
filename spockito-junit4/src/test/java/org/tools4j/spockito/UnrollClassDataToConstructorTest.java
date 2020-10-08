/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2020 tools4j.org (Marco Terzer)
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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.tools4j.spockito.Spockito.Name;
import org.tools4j.spockito.Spockito.Unroll;

@Unroll({
        "| Operation | Sign | Operand1 | Operand2 | Result | NeutralOperand |",
        "|-----------|------|----------|----------|--------|----------------|",
        "| Add       |   +  |        4 |        7 |     11 |              0 |",
        "| Subtract  |   -  |      111 |       12 |     99 |              0 |",
        "| Multiply  |   *  |       24 |        5 |    120 |              1 |",
        "| Divide    |   /  |       24 |        3 |      8 |              1 |"
})
@Name("[{row}]: {Operation}")
@RunWith(Spockito.class)
public class UnrollClassDataToConstructorTest {

    private final Operation operation;
    private final char sign;
    private final int operand1;
    private final int operand2;
    private final int result;
    private final int neutralOperand;

    public UnrollClassDataToConstructorTest(final Operation operation, final char sign,
                                            final int operand1, final int operand2, final int result, final int neutralOperand) {
        this.operation = operation;
        this.sign = sign;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.result = result;
        this.neutralOperand = neutralOperand;
    }

    @Test
    @Spockito.Name("[{row}]: {Operand1} {Sign} {Operand2} = {Result}")
    public void testOperation() {
        Assert.assertEquals("Result is wrong!", result, operation.evaluate(operand1, operand2));
    }

    @Test
    @Spockito.Name("[{row}]: {Operand1} {Sign} {NeutralOperand} = {Operand1}")
    public void testNeutralOperand() {
        Assert.assertEquals("Result with neutral operand is wrong!",
                operand1, operation.evaluate(operand1, neutralOperand));
    }
}
