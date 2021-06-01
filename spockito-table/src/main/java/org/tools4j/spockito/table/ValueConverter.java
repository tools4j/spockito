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

import java.lang.reflect.Type;

/**
 * Handles the conversion of string values to arbitrary target types; supports conversion into generic types such as
 * {@code List<String>}.
 */
public interface ValueConverter {
    /**
     * Converts the given string value into the target type specified by raw and generic type.
     *
     * @param type          the target type in raw form, for instance {@code int.class}, {@code List.class} etc.
     * @param genericType   the generic target type, same as type for non-generic types; generic type examples are
     *                      {@code List<String>}, {@code Map<String, Integer>} etc.
     * @param value         the value to convert, may be null
     * @param <T> the target type parameter
     * @return the converted value
     */
    <T> T convert(Class<T> type, Type genericType, String value);

    /**
     * Converts the given string value into the target type.  Use this method for non-generic types, and
     * {@link #convert(Class, Type, String)} for generic types.
     *
     * @param type          the target type, a non-generic type such as {@code String.class}
     * @param value         the value to convert, may be null
     * @param <T> the target type parameter
     * @return the converted value
     */
    default <T> T convert(Class<T> type, String value) {
        return convert(type, type, value);
    }

    static ValueConverter create(final Class<? extends ValueConverter> type) {
        if (SpockitoValueConverter.class.equals(type)) {
            return SpockitoValueConverter.DEFAULT_INSTANCE;
        }
        try {
            return type.newInstance();
        } catch (final Exception e) {
            throw new SpockitoException("Could not create value converter instance of type " + type.getName(), e);
        }
    }
}
