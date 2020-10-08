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
package org.tools4j.spockito.jupiter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

import org.tools4j.spockito.table.SpockitoValueConverter;
import org.tools4j.spockito.table.Table;
import org.tools4j.spockito.table.ValueConverter;

/**
 * @since 2.0
 */
final class TableArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<TableSource> {

    private TableSource annotation;
    private Table table;

    @Override
    public void accept(final TableSource annotation) {
        this.annotation = annotation;
        this.table = parse(annotation.value());
    }

    @Override
    public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
        final ValueConverter valueConverter = getValueConverter(context);
        final Method testMethod = context.getRequiredTestMethod();
        final List<Arguments> arguments = new ArrayList<>(table.getRowCount());
        table.forEach(tableRow -> {
            if (!tableRow.isSeparatorRow()) {
                final Object[] rowValues = tableRow.convertValues(testMethod,
                        TableArgumentsProvider::parameterRefNameOrNull, valueConverter);
                arguments.add(Arguments.of(rowValues));
            }
        });
        return arguments.stream();
    }

    private static ValueConverter getValueConverter(final ExtensionContext extensionContext) {
        //FIXME make configurable
        return SpockitoValueConverter.DEFAULT_INSTANCE;
    }

    private static String parameterRefNameOrNull(final Parameter parameter) {
        final Ref ref = parameter.getAnnotation(Ref.class);
        if (ref == null) {
            return parameter.isNamePresent() ? parameter.getName() : null;
        } else {
            return ref.value();
        }
    }

    private static Table parse(final String[] headerAndRows) {
        try {
            return Table.parse(headerAndRows);
        } catch (final Exception e) {
            throw new SpockitoException("TableSource parsing failed", e);
        }
    }

}