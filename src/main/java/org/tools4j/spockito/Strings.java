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

import java.util.regex.Pattern;

/**
 * Contains static utility methods dealing with strings.
 */
final class Strings {

    static final Pattern UNESCAPED_COMMA = Pattern.compile("(?<=[^\\\\]),");
    static final Pattern UNESCAPED_SEMICOLON = Pattern.compile("(?<=[^\\\\]);");
    static final Pattern UNESCAPED_COLON = Pattern.compile("(?<=[^\\\\]):");
    static final Pattern UNESCAPED_EQUAL = Pattern.compile("(?<=[^\\\\])=");

    static String firstCharToUpperCase(final String value) {
        return value.length() > 0 ? Character.toUpperCase(value.charAt(0)) + value.substring(1) : value;
    }

    static String removeSurroundingPipes(final String s) {
        return removeStartAndEndChars(s, '|', '|');
    }

    static String removeStartAndEndChars(final String s, final char startQuoteChar, final char endQuoteChar) {
        final int len = s.length();
        if (len >= 2 && s.charAt(0) == startQuoteChar && s.charAt(len - 1) == endQuoteChar) {
            return s.substring(1, len - 1);
        }
        return s;
    }

    static boolean allCharsMatchingAnyOf(final String s, final char ch1, final char ch2) {
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) != ch1 && s.charAt(i) != ch2) {
                return false;
            }
        }
        return true;
    }

    static String unescape(final String s) {
        return unescape(s, ',', ';', '|', '=', '\\','\'');
    }

    static String unescape(final String s, final char... escapedChars) {
        int index = s.indexOf('\\');
        if (index < 0) {
            return s;
        }
        final StringBuilder sb = new StringBuilder(s);
        while (index >= 0 && index + 1 < sb.length()) {
            final char ch = sb.charAt(index + 1);
            if (isCharAnyOf(ch, escapedChars)) {
                sb.delete(index, index + 1);
                index = sb.indexOf("\\", index + 1);
            } else {
                index = sb.indexOf("\\", index + 2);
            }
        }
        return s.length() == sb.length() ? s : sb.toString();
    }

    static boolean isCharAnyOf(final char ch, final char... chars) {
        for (final char c : chars) {
            if (ch == c) {
                return true;
            }
        }
        return false;
    }

    private Strings() {
        throw new RuntimeException("No Strings for you!");
    }
}
