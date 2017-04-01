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

final class Primitives {
    public static <T> Class<T> boxingTypeFor(final Class<T> type) {
        if (double.class.equals(type)) {
            return (Class<T>)Double.class;
        }
        if (float.class.equals(type)) {
            return (Class<T>)Float.class;
        }
        if (long.class.equals(type)) {
            return (Class<T>)Long.class;
        }
        if (int.class.equals(type)) {
            return (Class<T>)Integer.class;
        }
        if (short.class.equals(type)) {
            return (Class<T>)Short.class;
        }
        if (byte.class.equals(type)) {
            return (Class<T>)Byte.class;
        }
        if (boolean.class.equals(type)) {
            return (Class<T>)Boolean.class;
        }
        if (char.class.equals(type)) {
            return (Class<T>)Character.class;
        }
        if (void.class.equals(type)) {
            return (Class<T>)Void.class;
        }
        throw new IllegalArgumentException("Not a primitive type: " + type.getName());
    }

    private Primitives() {
        throw new RuntimeException("No Primitives for you!");
    }
}
