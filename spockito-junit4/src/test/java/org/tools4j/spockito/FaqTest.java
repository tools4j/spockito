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
package org.tools4j.spockito;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(Spockito.class)
public class FaqTest {

    private static final AtomicInteger SPECIAL_STRING_COUNT = new AtomicInteger();

    @BeforeClass
    public static void initClass() {
        SPECIAL_STRING_COUNT.set(0);
    }

    @AfterClass
    public static void assertSpecialStringTestCount() {
        if (SPECIAL_STRING_COUNT.get() > 0) {
            Assert.assertEquals("Expected 4 runs of testSpecialStrings()", 4, SPECIAL_STRING_COUNT.get());
        }
    }

    /** Example related to issue #1 */
    @Test(expected = IllegalArgumentException.class)
    @Spockito.Unroll({
            "|Input|",
            "||",
            "|''|",
            "|'|",
            "|' '|"
    })
    @Spockito.Name("[row]: Input=<{Input}>")
    public void testSpecialStrings(String input) {
        SPECIAL_STRING_COUNT.incrementAndGet();
        if (input.isEmpty()) throw new IllegalArgumentException("empty");
        if (input.trim().isEmpty()) throw new IllegalArgumentException("spaces");
        if (input.equals("'")) throw new IllegalArgumentException("single quote");
        Assert.fail("should have thrown an exception");
    }

    /** Example related to issue #2 */
    @Test
    @Spockito.Unroll({
            "|Input|Location ID|Event ID|",
            "|123\\|321|123|321|",
            "|123%7c321|123|321|"
    })
    public void testWithPipe(String input, BigInteger locationId, BigInteger eventId) {
        Assert.assertTrue(!input.contains("\\"));
        Assert.assertTrue(input.contains("|") || input.startsWith("123") && input.endsWith("321"));
    }

    /** Example related to issue #6 */
    @Test
    @Spockito.Unroll({
            "|Character|",
            "|'\u0001'|",
            "|'\u0002'|",
            "|'\u0003'|",
            "|'\u0004'|",
            "|'\u0005'|",
            "|'\u0006'|",
            "|'\u0007'|",
            "|'\u0008'|",
            "|'\u0009'|",
            "|'\u000b'|",
            "|'\u000c'|",
            "|'\u000e'|",
            "|'\u000f'|",
            "|'\u0010'|",
            "|'\u0011'|",
            "|'\u0012'|",
            "|'\u0013'|",
            "|'\u0014'|",
            "|'\u0015'|",
            "|'\u0016'|",
            "|'\u0017'|",
            "|'\u0018'|",
            "|'\u0019'|",
            "|'\u001a'|",
            "|'\u001b'|",
            "|'\u001c'|",
            "|'\u001d'|",
            "|'\u001e'|",
            "|'\u001f'|",
            "|'\u0020'|"
    })
    public void testNonPrintableOrWhitespaceChar(char ch) {
        Assert.assertTrue("Char should be non-printable or whitespace", ch <= '\u0021');
    }

    /** Example related to issue #6 */
    @Test
    @Spockito.Unroll({
            "|Character|",
            "|'\u0001'|",
            "|'\u0002'|",
            "|'\u0003'|",
            "|'\u0004'|",
            "|'\u0005'|",
            "|'\u0006'|",
            "|'\u0007'|",
            "|'\u0008'|",
            "|'\u0009'|",
            "|'\u000b'|",
            "|'\u000c'|",
            "|'\u000e'|",
            "|'\u000f'|",
            "|'\u0010'|",
            "|'\u0011'|",
            "|'\u0012'|",
            "|'\u0013'|",
            "|'\u0014'|",
            "|'\u0015'|",
            "|'\u0016'|",
            "|'\u0017'|",
            "|'\u0018'|",
            "|'\u0019'|",
            "|'\u001a'|",
            "|'\u001b'|",
            "|'\u001c'|",
            "|'\u001d'|",
            "|'\u001e'|",
            "|'\u001f'|",
            "|'\u0020'|"
    })
    public void testNonPrintableOrWhitespaceCharsAsString(String s) {
        Assert.assertEquals("String length should be 1", 1, s.length());
        Assert.assertTrue("Char should be non-printable or whitespace", s.charAt(0) <= '\u0021');
    }

}
