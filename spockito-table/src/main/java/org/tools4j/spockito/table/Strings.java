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

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Contains static utility methods dealing with strings.
 */
enum Strings {
    ;
    static final String[] EMPTY_STRING_ARRAY = new String[0];
    static final Pattern UNESCAPED_COMMA = Pattern.compile("(?<=[^\\\\]),");
    static final Pattern UNESCAPED_SEMICOLON = Pattern.compile("(?<=[^\\\\]);");
    static final Pattern UNESCAPED_COLON = Pattern.compile("(?<=[^\\\\]):");
    static final Pattern UNESCAPED_EQUAL = Pattern.compile("(?<=[^\\\\])=");
    static final Pattern UNESCAPED_PIPE = Pattern.compile("(?<=[^\\\\])\\|");

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

    static String[] split(final String s, final Pattern delimRegex1, final Pattern delimRegex2) {
        String[] parts = delimRegex1.split(s);
        if (parts.length == 1 && s.equals(parts[0])) {
            //delimiter was not contained in string, try second delimiter
            parts = delimRegex2.split(s);
            if (parts.length == 1 && s.equals(parts[0])) {
                //delimiter was not contained in string, we're done
                return parts;
            }
        }
        if (!s.startsWith(parts[0])) {
            //must be delimiter at the start of the string, meaning we have an empty string at the start
            final String[] newParts = new String[parts.length + 1];
            System.arraycopy(parts, 0, newParts, 1, parts.length);
            newParts[0] = "";
            parts = newParts;
        }
        if (!s.endsWith(parts[parts.length - 1])) {
            //must be delimiter at the end of the string, meaning we have an empty string at the end
            parts = Arrays.copyOf(parts, parts.length + 1);
            parts[parts.length - 1] = "";
        }
        return parts;
    }
}
