/**
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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

/**
 * Contains static helper methods to inspect and specify generic types.
 */
public enum GenericTypes {
    ;
    public interface ActualType {
        Class<?> rawType();
        Type genericType();

        static ActualType create(final Class<?> rawType, final Type genericType) {
            requireNonNull(rawType);
            requireNonNull(genericType);
            return new ActualType() {
                @Override
                public Class<?> rawType() {
                    return rawType;
                }

                @Override
                public Type genericType() {
                    return genericType;
                }

                @Override
                public String toString() {
                    return "ActualType{rawType=" + rawType + ", genericType=" + genericType + "}";
                }
            };
        }
    }

    public static ActualType actualTypeForTypeParam(final Type type, final int paramIndex, final int paramCount) {
        if (type instanceof ParameterizedType) {
            final Type[] actualTypeArgs = ((ParameterizedType) type).getActualTypeArguments();
            if (actualTypeArgs.length == paramCount) {
                Type actualType = actualTypeArgs[paramIndex];
                if (actualType instanceof WildcardType) {
                    final Type[] bounds = ((WildcardType) actualType).getUpperBounds();
                    if (bounds.length == 1) {
                        actualType = bounds[0];
                    }
                }
                if (actualType instanceof Class) {
                    return ActualType.create((Class<?>) actualType, actualType);
                }
                if (actualType instanceof ParameterizedType) {
                    final ParameterizedType parameterizedType = (ParameterizedType)actualType;
                    if (parameterizedType.getRawType() instanceof Class) {
                        return ActualType.create((Class<?>)parameterizedType.getRawType(), parameterizedType);
                    }
                }
            }
        }
        if (Properties.class.equals(type) && paramCount == 2) {
            return ActualType.create(String.class, String.class);
        }
        throw new IllegalArgumentException("Could not derive actual generic type [" + paramIndex + "] for " + type);
    }

    public static ActualType genericComponentType(final Class<?> arrayType, final Type genericType) {
        final Class<?> componentType = arrayType.getComponentType();
        if (componentType == null) {
            throw new IllegalArgumentException("Must be an array type: " + arrayType);
        }
        if (genericType instanceof GenericArrayType) {
            return ActualType.create(componentType, ((GenericArrayType)genericType).getGenericComponentType());
        }
        return ActualType.create(componentType, componentType);
    }

    public static ParameterizedType genericListType(final Type listElementType) {
        requireNonNull(listElementType);
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{listElementType};
            }
            @Override
            public Type getRawType() {
                return List.class;
            }
            @Override
            public Type getOwnerType() {
                return null;
            }
            @Override
            public String toString() {
                return List.class.getName() + "<" + listElementType + ">";
            }
        };
    }
}
