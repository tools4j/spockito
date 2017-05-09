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

import org.junit.*;
import org.junit.runner.RunWith;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(Spockito.class)
public class FaqTest {

    private static final AtomicInteger SPECIAL_STRING_COUNT = new AtomicInteger();
    private static final AtomicInteger UNPRINTABLE_CHARACTERS = new AtomicInteger();
    private static final AtomicInteger UNPRINTABLE_CHARACTERS_WRAPPED = new AtomicInteger();

    private static final Pattern UNPRINTABLE_PATTERN = Pattern.compile("[\u0001-\u0021]");

    @BeforeClass
    public static void initClass() {
        SPECIAL_STRING_COUNT.set(0);
        UNPRINTABLE_CHARACTERS.set(0);
        UNPRINTABLE_CHARACTERS_WRAPPED.set(0);
    }

    @AfterClass
    public static void assertSpecialStringTestCount() {
        Assume.assumeTrue("Assume that testSpecialStrings() has been run at least once", SPECIAL_STRING_COUNT.get() > 0);
        Assert.assertEquals("Expected 4 runs of testSpecialStrings()", 4, SPECIAL_STRING_COUNT.get());
        Assert.assertEquals("Expected 31 run from testUnprintableCharacters()", 1, UNPRINTABLE_CHARACTERS.get());
        Assert.assertEquals("Expected 31 run from testUnprintableCharactersWrapped()", 1, UNPRINTABLE_CHARACTERS_WRAPPED.get());
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

    /** Question related to issue # */
    @Test
    @Spockito.Unroll({
      "|Character|",
      "|\u0001|",
      "|\u0002|",
      "|\u0003|",
      "|\u0004|",
      "|\u0005|",
      "|\u0006|",
      "|\u0007|",
      "|\u0008|",
      "|\u0009|",
      "|\u000b|",
      "|\u000c|",
      "|\u000e|",
      "|\u000f|",
      "|\u0010|",
      "|\u0011|",
      "|\u0012|",
      "|\u0013|",
      "|\u0014|",
      "|\u0015|",
      "|\u0016|",
      "|\u0017|",
      "|\u0018|",
      "|\u0019|",
      "|\u001a|",
      "|\u001b|",
      "|\u001c|",
      "|\u001d|",
      "|\u001e|",
      "|\u001f|",
      "|\u0020|",
      "|\u0021|"
    })
    public void testUnprintableCharacters(String character){
      Matcher m = UNPRINTABLE_PATTERN.matcher(character);
      if (m.find()) {
        UNPRINTABLE_CHARACTERS.incrementAndGet();
      } else {
        Assert.fail("Found unexpected characters");
      }
    }

    @Test
    @Spockito.Unroll({
      "|Character|",
      "|a\u0001a|",
      "|a\u0002a|",
      "|a\u0003a|",
      "|a\u0004a|",
      "|a\u0005a|",
      "|a\u0006a|",
      "|a\u0007a|",
      "|a\u0008a|",
      "|a\u0009a|",
      "|a\u000ba|",
      "|a\u000ca|",
      "|a\u000ea|",
      "|a\u000fa|",
      "|a\u0010a|",
      "|a\u0011a|",
      "|a\u0012a|",
      "|a\u0013a|",
      "|a\u0014a|",
      "|a\u0015a|",
      "|a\u0016a|",
      "|a\u0017a|",
      "|a\u0018a|",
      "|a\u0019a|",
      "|a\u001aa|",
      "|a\u001ba|",
      "|a\u001ca|",
      "|a\u001da|",
      "|a\u001ea|",
      "|a\u001fa|",
      "|a\u0020a|",
      "|a\u0021a|"
    })
    public void testUnprintableCharactersWrapped(String character){
      Matcher m = UNPRINTABLE_PATTERN.matcher(character);
      if (m.find()) {
        UNPRINTABLE_CHARACTERS_WRAPPED.incrementAndGet();
      } else {
        Assert.fail("Found unexpected characters");
      }
    }

}
