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
        Assert.assertEquals("Expected 4 special string test runs", 4, SPECIAL_STRING_COUNT.get());
    }

    /** Question related to issue #1 */
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

    /** Question related to issue #2 */
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

}
