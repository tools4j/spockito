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
package org.tools4j.spockito.table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.omg.CORBA.Object;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for {@link SpockitoValueConverter}.
 */
public class ValueConverterTest {

    private static final Random RND = new Random();

    private enum TestEnum {
        CONST_A,
        CONST_B,
        CONST_C
    }

    private static class TestCollection<E> extends ArrayList<E> {
        public TestCollection(final Collection<? extends E> coll) {
            super(coll);
        }
    }

    private static class TestMap<K,V> extends HashMap<K,V> {
        public TestMap(final Map<? extends K, ? extends V> map) {
            super(map);
        }
    }

    //under test
    private final ValueConverter converter = new SpockitoValueConverter();

    @Test
    public void convertNull() {
        assertNull(converter.convert(null, null, null));
        assertNull(converter.convert(Object.class, Object.class, null));
        assertNull(converter.convert(String.class, String.class, null));
        assertNull(converter.convert(TestEnum.class, TestEnum.class, null));
        assertNull(converter.convert(Double.class, Double.class, null));

        assertNull(converter.convert(null, null, "null"));
        assertNull(converter.convert(Object.class, Object.class, "null"));
        assertNull(converter.convert(String.class, String.class, "null"));
        assertNull(converter.convert(TestEnum.class, TestEnum.class, "null"));
        assertNull(converter.convert(Double.class, Double.class, "null"));
    }

    @Test
    public void convertString() {
        assertEquals("hello world", converter.convert(String.class, String.class, "hello world"),"Expected string unchanged");
        assertEquals("hello world", converter.convert(String.class, String.class, "'hello world'"),"Expected string without quotes");
        assertEquals("hello world", converter.convert(String.class, String.class, "\'hello world\'"),"Expected string without quotes again");
        assertEquals("'hello world'", converter.convert(String.class, String.class, "''hello world''"),"Expected string with quotes");
        assertEquals("'hello world", converter.convert(String.class, String.class, "'hello world"),"Expected string with left quotes");
        assertEquals("hello world'", converter.convert(String.class, String.class, "hello world'"),"Expected string with right quotes");
        assertEquals("", converter.convert(String.class, String.class, ""),"Expected empty string");
        assertEquals("", converter.convert(String.class, String.class, "''"),"Expected empty string");
    }

    @Test
    public void convertStringBuilder() {
        assertNotNull(converter.convert(StringBuilder.class, StringBuilder.class, "hello world"), "Expected StringBuilder");
        assertEquals("hello world", converter.convert(StringBuilder.class, StringBuilder.class, "hello world").toString(),"Expected string unchanged");
        assertEquals("hello world", converter.convert(StringBuilder.class, StringBuilder.class, "'hello world'").toString(),"Expected string without quotes");
        assertEquals("hello world", converter.convert(StringBuilder.class, StringBuilder.class, "\'hello world\'").toString(),"Expected string without quotes again");
        assertEquals("'hello world'", converter.convert(StringBuilder.class, StringBuilder.class, "''hello world''").toString(),"Expected string with quotes");
        assertEquals("'hello world", converter.convert(StringBuilder.class, StringBuilder.class, "'hello world").toString(),"Expected string with left quotes");
        assertEquals("hello world'", converter.convert(StringBuilder.class, StringBuilder.class, "hello world'").toString(),"Expected string with right quotes");
        assertEquals("", converter.convert(StringBuilder.class, StringBuilder.class, "").toString(),"Expected empty string");
        assertEquals("", converter.convert(StringBuilder.class, StringBuilder.class, "''").toString(),"Expected empty string");
    }

    @Test
    public void convertStringBuffer() {
        assertNotNull(converter.convert(StringBuffer.class, StringBuffer.class, "hello world"), "Expected StringBuffer");
        assertEquals("hello world", converter.convert(StringBuffer.class, StringBuffer.class, "hello world").toString(),"Expected string unchanged");
        assertEquals("hello world", converter.convert(StringBuffer.class, StringBuffer.class, "'hello world'").toString(),"Expected string without quotes");
        assertEquals("hello world", converter.convert(StringBuffer.class, StringBuffer.class, "\'hello world\'").toString(),"Expected string without quotes again");
        assertEquals("'hello world'", converter.convert(StringBuffer.class, StringBuffer.class, "''hello world''").toString(),"Expected string with quotes");
        assertEquals("'hello world", converter.convert(StringBuffer.class, StringBuffer.class, "'hello world").toString(),"Expected string with left quotes");
        assertEquals("hello world'", converter.convert(StringBuffer.class, StringBuffer.class, "hello world'").toString(),"Expected string with right quotes");
        assertEquals("", converter.convert(StringBuffer.class, StringBuffer.class, "").toString(),"Expected empty string");
        assertEquals("", converter.convert(StringBuffer.class, StringBuffer.class, "''").toString(),"Expected empty string");
    }

    @Test
    public void convertChar() {
        assertEquals(Character.valueOf('A'), converter.convert(char.class, char.class, "A"),"Expected char unchanged");
        assertEquals(Character.valueOf('A'), converter.convert(Character.class, Character.class, "A"),"Expected Character unchanged");
        assertEquals(Character.valueOf('A'), converter.convert(char.class, char.class, "'A'"),"Expected char without quotes");
        assertEquals(Character.valueOf('A'), converter.convert(Character.class, Character.class, "'A'"),"Expected char without quotes");
        assertEquals(Character.valueOf('\''), converter.convert(char.class, char.class, "'"),"Expected quote char");
        assertEquals(Character.valueOf('\''), converter.convert(Character.class, Character.class, "'"),"Expected quote Character");
        assertEquals(Character.valueOf('\''), converter.convert(char.class, char.class, "'''"),"Expected quote char without quotes");
        assertEquals(Character.valueOf('\''), converter.convert(Character.class, Character.class, "'''"),"Expected quote Character without quotes");
    }

    @Test
    public void convertBoolean() {
        for (final Boolean exp : new Boolean[]{true, false}) {
            assertEquals(exp, converter.convert(boolean.class, boolean.class, exp.toString()),"Expected boolean unchanged");
            assertEquals(exp, converter.convert(Boolean.class, Boolean.class, exp.toString()),"Expected Boolean unchanged");
        }
    }

    @Test
    public void convertNumeric() throws Exception {
        final Class<?>[] primitive = new Class<?>[]{byte.class, short.class, int.class, long.class, float.class, double.class};
        final Class<?>[] boxed = new Class<?>[]{Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class};
        final Class<?>[] other = new Class<?>[]{BigInteger.class, BigDecimal.class};
        for (int i = 0; i < primitive.length; i++) {
            @SuppressWarnings("unchecked")
            final Class<? extends Number> pType = (Class<? extends Number>)primitive[i];
            @SuppressWarnings("unchecked")
            final Class<? extends Number> bType = (Class<? extends Number>)boxed[i];
            for (int val = Byte.MIN_VALUE; val <= Byte.MAX_VALUE; val++) {
                final Number exp = toNumber(pType, val);
                assertEquals(exp, converter.convert(pType, pType, String.valueOf(val)),"Unexpected number (primitive)");
                assertEquals(exp, converter.convert(bType, bType, String.valueOf(val)),"Unexpected number (boxed)");
            }
            final Number min = (Number)bType.getField("MIN_VALUE").get(null);
            final Number max = (Number)bType.getField("MAX_VALUE").get(null);
            assertEquals(min, converter.convert(pType, pType, min.toString()),"Unexpected min intValue");
            assertEquals(max, converter.convert(bType, bType, max.toString()),"Unexpected max intValue");
            if (float.class.equals(pType) || double.class.equals(pType)) {
                assertTrue(Double.isNaN(converter.convert(pType, pType, "NaN").doubleValue()),"Expected NaN intValue");
                assertTrue(Double.isNaN(converter.convert(bType, bType, "NaN").doubleValue()),"Expected NaN intValue");
                final int n = 10000;
                for (int j = 0; j < n; j++) {
                    final double val = RND.nextBoolean() ? RND.nextDouble() : RND.nextGaussian();
                    final Number exp = toNumber(pType, val);
                    assertEquals(exp, converter.convert(pType, pType, String.valueOf(val)),"Unexpected float number (primitive)");
                    assertEquals(exp, converter.convert(bType, bType, String.valueOf(val)),"Unexpected float number (boxed)");
                }
            }
        }
    }

    @Test
    public void convertBigInteger() {
        for (int val = Byte.MIN_VALUE; val <= Byte.MAX_VALUE; val++) {
            final BigInteger exp = BigInteger.valueOf(val);
            assertEquals(exp, converter.convert(BigInteger.class, null, exp.toString()),"Unexpected small BigInteger");
        }
        for (long val : new long[] {Integer.MIN_VALUE, Long.MIN_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE}) {
            final BigInteger exp = BigInteger.valueOf(val);
            assertEquals(exp, converter.convert(BigInteger.class, null, exp.toString()),"Unexpected min/max BigInteger");
        }
        for (int pow = 0; pow <= 50; pow++) {
            for (int base = -10; base <= 10; base++) {
                final BigInteger exp = BigInteger.valueOf(base).pow(pow);
                assertEquals(exp, converter.convert(BigInteger.class, null, exp.toString()),"Unexpected power BigInteger");
            }
        }
    }

    @Test
    public void convertBigDecimal() {
        for (int val = Byte.MIN_VALUE; val <= Byte.MAX_VALUE; val++) {
            final BigDecimal exp = BigDecimal.valueOf(val);
            assertEquals(exp, converter.convert(BigDecimal.class, null, exp.toString()),"Unexpected small BigDecimal");
        }
        for (long val : new long[] {Integer.MIN_VALUE, Long.MIN_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE}) {
            final BigDecimal exp = BigDecimal.valueOf(val);
            assertEquals(exp, converter.convert(BigDecimal.class, null, exp.toString()),"Unexpected min/max BigInteger");
        }
        for (int pow = -20; pow <= 20; pow++) {
            for (int base = -10; base <= 10; base++) {
                if (base != 0) {
                    final BigDecimal exp = BigDecimal.valueOf(base).pow(pow, MathContext.DECIMAL128);
                    assertEquals(exp, converter.convert(BigDecimal.class, null, exp.toString()),"Unexpected power BigDecimal");
                }
            }
        }
        final int n = 10000;
        for (int j = 0; j < n; j++) {
            final double val = RND.nextBoolean() ? RND.nextDouble() : RND.nextGaussian();
            final Number exp = BigDecimal.valueOf(val);
            assertEquals(exp, converter.convert(BigDecimal.class, null, exp.toString()),"Unexpected random BigDecimal");
        }
    }

    @Test
    public void convertLocalDate() {
        assertEquals(LocalDate.of(2017, 03, 22), converter.convert(LocalDate.class, null, "2017-03-22"),"Unexpected value");
        final LocalDate[] localDates = new LocalDate[] {
                LocalDate.of(2010, 10, 4),
                LocalDate.of(2017, 12, 31),
                LocalDate.of(2016, 9, 1),
                LocalDate.of(2000, 1, 1),
                LocalDate.of(0, 1, 1),
                LocalDate.of(1970, 2, 28),
        };
        for (final LocalDate exp : localDates) {
            assertEquals(exp, converter.convert(LocalDate.class, null, exp.toString()),"Unexpected value");
        }
    }

    @Test
    public void convertLocalTime() {
        assertEquals(LocalTime.of(17, 15, 31), converter.convert(LocalTime.class, null, "17:15:31"),"Unexpected intValue");
        assertEquals(LocalTime.of(17, 15, 31, 111000000), converter.convert(LocalTime.class, null, "17:15:31.111"),"Unexpected intValue");
        final LocalTime[] localTimes = new LocalTime[] {
                LocalTime.of(17, 10, 4),
                LocalTime.of(3, 12, 31),
                LocalTime.of(11, 9, 1),
                LocalTime.of(12, 1, 1),
                LocalTime.of(23, 59, 59, 123456789),
                LocalTime.of(00, 00, 00, 000000001),
        };
        for (final LocalTime exp : localTimes) {
            assertEquals(exp, converter.convert(LocalTime.class, null, exp.toString()),"Unexpected value");
        }
    }

    @Test
    public void convertLocalDateTime() {
        assertEquals(LocalDateTime.of(2017, 03, 22, 17, 15, 31), converter.convert(LocalDateTime.class, null, "2017-03-22T17:15:31"),"Unexpected value");
        assertEquals(LocalDateTime.of(2017, 03, 22, 17, 15, 31, 111000000), converter.convert(LocalDateTime.class, null, "2017-03-22T17:15:31.111"),"Unexpected value");
        final LocalDateTime[] localDateTimes = new LocalDateTime[] {
                LocalDateTime.of(2010, 10, 4, 17, 10, 4),
                LocalDateTime.of(2017, 12, 31, 3, 12, 31),
                LocalDateTime.of(2016, 9, 1, 11, 9, 1),
                LocalDateTime.of(2000, 1, 1, 12, 1, 1),
                LocalDateTime.of(0, 1, 1, 23, 59, 59, 123456789),
                LocalDateTime.of(1970, 2, 28, 00, 00, 00, 000000001),
        };
        for (final LocalDateTime exp : localDateTimes) {
            assertEquals(exp, converter.convert(LocalDateTime.class, null, exp.toString()),"Unexpected value");
        }
    }

    @Test
    public void convertZonedDateTime() {
        assertEquals(ZonedDateTime.of(2017, 03, 22, 17, 15, 31, 0, ZoneOffset.UTC), converter.convert(ZonedDateTime.class, null, "2017-03-22T17:15:31+00:00"),"Unexpected value");
        assertEquals(ZonedDateTime.of(2017, 03, 22, 17, 15, 31, 111000000, ZoneOffset.UTC), converter.convert(ZonedDateTime.class, null, "2017-03-22T17:15:31.111+00:00"),"Unexpected value");
        for (final String id : ZoneId.getAvailableZoneIds()) {
            final ZoneId zoneId = ZoneId.of(id);
            if ("GMT0".equals(zoneId.getId())) continue;//JDK bug, fixed in Java 9
            final ZonedDateTime[] zonedDateTimes = new ZonedDateTime[]{
                    ZonedDateTime.of(2010, 10, 4, 17, 10, 4, 0, zoneId),
                    ZonedDateTime.of(2017, 12, 31, 3, 12, 31, 0, zoneId),
                    ZonedDateTime.of(2016, 9, 1, 11, 9, 1, 0, zoneId),
                    ZonedDateTime.of(2000, 1, 1, 12, 1, 1, 0, zoneId),
                    ZonedDateTime.of(0, 1, 1, 23, 59, 59, 123456789, zoneId),
                    ZonedDateTime.of(1970, 2, 28, 00, 00, 00, 000000001, zoneId),
            };
            for (final ZonedDateTime exp : zonedDateTimes) {
                assertEquals(exp, converter.convert(ZonedDateTime.class, null, exp.toString()),"Unexpected value");
            }
        }
    }

    @Test
    public void convertOffsetDateTime() {
        assertEquals(OffsetDateTime.of(2017, 03, 22, 17, 15, 31, 0, ZoneOffset.UTC), converter.convert(OffsetDateTime.class, null, "2017-03-22T17:15:31+00:00"),"Unexpected value");
        assertEquals(OffsetDateTime.of(2017, 03, 22, 17, 15, 31, 111000000, ZoneOffset.UTC), converter.convert(OffsetDateTime.class, null, "2017-03-22T17:15:31.111+00:00"),"Unexpected value");
        final int halfHourInSeconds = 30 * 60;
        for (int offsetSeconds = ZoneOffset.MIN.getTotalSeconds(); offsetSeconds < ZoneOffset.MAX.getTotalSeconds(); offsetSeconds += halfHourInSeconds) {
            final ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(offsetSeconds);
            final OffsetDateTime[] offsetDateTimes = new OffsetDateTime[]{
                    OffsetDateTime.of(2010, 10, 4, 17, 10, 4, 0, zoneOffset),
                    OffsetDateTime.of(2017, 12, 31, 3, 12, 31, 0, zoneOffset),
                    OffsetDateTime.of(2016, 9, 1, 11, 9, 1, 0, zoneOffset),
                    OffsetDateTime.of(2000, 1, 1, 12, 1, 1, 0, zoneOffset),
                    OffsetDateTime.of(0, 1, 1, 23, 59, 59, 123456789, zoneOffset),
                    OffsetDateTime.of(1970, 2, 28, 00, 00, 00, 000000001, zoneOffset),
            };
            for (final OffsetDateTime exp : offsetDateTimes) {
                assertEquals(exp, converter.convert(OffsetDateTime.class, null, exp.toString()),"Unexpected value");
            }
        }
    }

    @Test
    public void convertInstant() {
        assertEquals(LocalDateTime.of(2017, 03, 22, 17, 15, 31, 0).toInstant(ZoneOffset.UTC), converter.convert(Instant.class, null, "2017-03-22T17:15:31Z"),"Unexpected value");
        assertEquals(LocalDateTime.of(2017, 03, 22, 17, 15, 31, 111000000).toInstant(ZoneOffset.UTC), converter.convert(Instant.class, null, "2017-03-22T17:15:31.111Z"),"Unexpected value");
        final int halfHourInSeconds = 30 * 60;
        for (int offsetSeconds = ZoneOffset.MIN.getTotalSeconds(); offsetSeconds < ZoneOffset.MAX.getTotalSeconds(); offsetSeconds += halfHourInSeconds) {
            final ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(offsetSeconds);
            final Instant[] instants = new Instant[]{
                    LocalDateTime.of(2010, 10, 4, 17, 10, 4, 0).toInstant(zoneOffset),
                    LocalDateTime.of(2017, 12, 31, 3, 12, 31, 0).toInstant(zoneOffset),
                    LocalDateTime.of(2016, 9, 1, 11, 9, 1, 0).toInstant(zoneOffset),
                    LocalDateTime.of(2000, 1, 1, 12, 1, 1, 0).toInstant(zoneOffset),
                    LocalDateTime.of(0, 1, 1, 23, 59, 59, 123456789).toInstant(zoneOffset),
                    LocalDateTime.of(1970, 2, 28, 00, 00, 00, 000000001).toInstant(zoneOffset),
            };
            for (final Instant exp : instants) {
                assertEquals(exp, converter.convert(Instant.class, null, exp.toString()),"Unexpected value");
            }
        }
    }

    @Test
    public void convertDate() {
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        assertEquals(Date.from(LocalDateTime.of(2017, 03, 22, 17, 15, 31, 0).atZone(ZoneId.systemDefault()).toInstant()),
                converter.convert(Date.class, null, "2017-03-22 17:15:31"), "Unexpected value");
        assertEquals(Date.from(LocalDateTime.of(2017, 03, 22, 17, 15, 31, 111000000).atZone(ZoneId.systemDefault()).toInstant()),
                converter.convert(Date.class, null, "2017-03-22 17:15:31.111"), "Unexpected value");
        final int halfHourInSeconds = 30 * 60;
        for (int offsetSeconds = ZoneOffset.MIN.getTotalSeconds(); offsetSeconds < ZoneOffset.MAX.getTotalSeconds(); offsetSeconds += halfHourInSeconds) {
            final ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(offsetSeconds);
            final Date[] dates = new Date[]{
                    Date.from(LocalDateTime.of(2010, 10, 4, 17, 10, 4, 0).toInstant(zoneOffset)),
                    Date.from(LocalDateTime.of(2017, 12, 31, 3, 12, 31, 0).toInstant(zoneOffset)),
                    Date.from(LocalDateTime.of(2016, 9, 1, 11, 9, 1, 0).toInstant(zoneOffset)),
                    Date.from(LocalDateTime.of(2000, 1, 1, 12, 1, 1, 0).toInstant(zoneOffset)),
                    Date.from(LocalDateTime.of(1, 1, 1, 23, 59, 59, 123000000).toInstant(zoneOffset)),//year 0 fails ?!?
                    Date.from(LocalDateTime.of(1970, 2, 28, 00, 00, 00, 000000001).toInstant(zoneOffset)),
            };
            for (final Date exp : dates) {
                assertEquals(exp, converter.convert(Date.class, null, format.format(exp)),"Unexpected value");
            }
        }
    }

    @Test
    public void convertSqlDate() {
        assertEquals(java.sql.Date.valueOf(LocalDate.of(2017, 03, 22)),
                converter.convert(java.sql.Date.class, null, "2017-03-22"), "Unexpected value");
        final java.sql.Date[] dates = new java.sql.Date[]{
                java.sql.Date.valueOf(LocalDate.of(2010, 10, 4)),
                java.sql.Date.valueOf(LocalDate.of(2017, 12, 31)),
                java.sql.Date.valueOf(LocalDate.of(2016, 9, 1)),
                java.sql.Date.valueOf(LocalDate.of(2000, 1, 1)),
                java.sql.Date.valueOf(LocalDate.of(1, 1, 1)),//year 0 fails ?!?
                java.sql.Date.valueOf(LocalDate.of(1970, 2, 28)),
        };
        for (final java.sql.Date exp : dates) {
            assertEquals(exp, converter.convert(java.sql.Date.class, null, exp.toString()),"Unexpected value");
        }
    }

    @Test
    public void convertSqlTime() {
        //NOTE: no millisecond support!
        assertEquals(Time.valueOf(LocalTime.of(17, 15, 31, 0)), converter.convert(Time.class, null, "17:15:31"),"Unexpected value");
        final Time[] times = new Time[]{
                Time.valueOf(LocalTime.of(17, 10, 4, 0)),
                Time.valueOf(LocalTime.of(3, 12, 31, 0)),
                Time.valueOf(LocalTime.of(11, 9, 1, 0)),
                Time.valueOf(LocalTime.of(12, 1, 1, 0)),
                Time.valueOf(LocalTime.of(23, 59, 59, 0)),
                Time.valueOf(LocalTime.of(00, 00, 00, 0)),
        };
        for (final Time exp : times) {
            assertEquals(exp, converter.convert(Time.class, null, exp.toString()),"Unexpected value");
        }
    }

    @Test
    public void convertSqlTimestamp() {
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2017, 03, 22, 17, 15, 31, 0)),
                converter.convert(Timestamp.class, null, "2017-03-22 17:15:31"));
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2017, 03, 22, 17, 15, 31, 111000000)),
                converter.convert(Timestamp.class, null, "2017-03-22 17:15:31.111"));
        final Timestamp[] timestamps = new Timestamp[]{
                Timestamp.valueOf(LocalDateTime.of(2010, 10, 4, 17, 10, 4, 0)),
                Timestamp.valueOf(LocalDateTime.of(2017, 12, 31, 3, 12, 31, 0)),
                Timestamp.valueOf(LocalDateTime.of(2016, 9, 1, 11, 9, 1, 0)),
                Timestamp.valueOf(LocalDateTime.of(2000, 1, 1, 12, 1, 1, 0)),
                Timestamp.valueOf(LocalDateTime.of(1, 1, 1, 23, 59, 59, 123456789)),//year 0 fails ?!?
                Timestamp.valueOf(LocalDateTime.of(1970, 2, 28, 00, 00, 00, 000000001)),
        };
        for (final Timestamp exp : timestamps) {
            assertEquals(exp, converter.convert(Timestamp.class, null, exp.toString()),"Unexpected value");
        }
    }

    @Test
    public void convertEnum() {
        for (final TestEnum testEnum : TestEnum.values()) {
            assertEquals(testEnum, converter.convert(TestEnum.class, null, testEnum.name()),"Unexpected value");
        }
    }

    @Test
    public void convertOptional() throws Exception {
        final class Optionals {
            Optional<String> emptyString = Optional.empty();
            Optional<String> nonEmptyString = Optional.of("trumpet");
            Optional<String> nonEmptyString2 = Optional.of("empty");
            Optional<Integer> emptyInteger = Optional.empty();
            Optional<Integer> nonEmptyInteger = Optional.of(42);
        }
        final Optionals optionals = new Optionals();

        //empty string
        assertEquals(optionals.emptyString, converter.convert(
                Optional.class, Optionals.class.getDeclaredField("emptyString").getGenericType(), "empty"),
                "Unexpected empty string value");
        assertEquals(optionals.emptyString, converter.convert(
                Optional.class, Optionals.class.getDeclaredField("emptyString").getGenericType(), ""),
                "Unexpected empty string value");

        //empty integer
        assertEquals(optionals.emptyInteger, converter.convert(
                Optional.class, Optionals.class.getDeclaredField("emptyInteger").getGenericType(), "empty"),
                "Unexpected empty integer value");
        assertEquals(optionals.emptyInteger, converter.convert(
                Optional.class, Optionals.class.getDeclaredField("emptyInteger").getGenericType(), ""),
                "Unexpected empty integer value");

        //non-empty string value
        assertEquals(optionals.nonEmptyString, converter.convert(
                Optional.class, Optionals.class.getDeclaredField("nonEmptyString").getGenericType(), "trumpet"),
                "Unexpected optional string value");
        assertEquals(optionals.nonEmptyString, converter.convert(
                Optional.class, Optionals.class.getDeclaredField("nonEmptyString").getGenericType(), "'trumpet'"),
                "Unexpected optional string value");
        assertEquals(optionals.nonEmptyString2, converter.convert(
                Optional.class, Optionals.class.getDeclaredField("nonEmptyString2").getGenericType(), "'empty'"),
                "Unexpected optional string value 2");

        //non-empty integer value
        assertEquals(optionals.nonEmptyInteger, converter.convert(
                Optional.class, Optionals.class.getDeclaredField("nonEmptyInteger").getGenericType(), "42"),
                "Unexpected optional integer value");
    }

    @Test
    public void convertClass() {
        final Class<?>[] classes = new Class<?>[] {
                getClass(),
                ValueConverter.class,
                Object.class,
                String.class,
                TestEnum.class,
                TestEnum.CONST_A.getClass()
        };
        for (final Class<?> exp : classes) {
            assertEquals(exp, converter.convert(Class.class, null, exp.getName()),"Unexpected value");
        }
    }

    @Test
    public void convertArray() throws Exception {
        assertArrayEquals( new int[] {1,2,3,4}, converter.convert(int[].class, null, "[1,2,3,4]"),"Unexpected int array");
        assertArrayEquals( new Integer[] {1,2,3,4}, converter.convert(Integer[].class, null, "[1,2,3,4]"),"Unexpected Integer array");
        assertArrayEquals( new int[][] {{1,2},{3},{4}}, converter.convert(int[][].class, null, "[[1;2],[3],[4]]"),"Unexpected array of arrays");

        final class GenericArrayHolder {
            @SuppressWarnings("unchecked")
            final List<String>[] stringListArray = (List<String>[])new List<?>[] {
                    Arrays.asList("one", "two"),
                    Arrays.asList("three"),
                    Arrays.asList("four")
            };
        }
        assertArrayEquals(
                new GenericArrayHolder().stringListArray,
                converter.convert(
                        List[].class,
                        GenericArrayHolder.class.getDeclaredField("stringListArray").getGenericType(),
                        "[[one;two],[three],[four]]"
                ), "Unexpected array of string lists");
    }

    @Test
    public void convertCollection() throws Exception {
        final class CollectionHolder {
            final Collection<Integer> collection = Arrays.asList(1, 2, 3, 4);
            final List<Integer> list= Arrays.asList(1, 2, 3, 4);
            final List<Integer> emptyList = Arrays.asList();
            final ArrayList<Integer> arrayList = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
            final Vector<Integer> vector = new Vector<>(Arrays.asList(1, 2, 3, 4));
            final Set<Integer> set = new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4));
            final SortedSet<Integer> sortedSet = new TreeSet<>(Arrays.asList(1, 2, 3, 4));
            final NavigableSet<Integer> navigableSet = new TreeSet<>(Arrays.asList(1, 2, 3, 4));
            final HashSet<Integer> hashSet = new HashSet<>(Arrays.asList(1, 2, 3, 4));
            final Queue<Integer> queue = new LinkedList<>(Arrays.asList(1, 2, 3, 4));
            final Deque<Integer> deque = new LinkedList<>(Arrays.asList(1, 2, 3, 4));
            final ArrayDeque<Integer> arrayDeque = new ArrayDeque<>(Arrays.asList(1, 2, 3, 4));
            final ConcurrentLinkedQueue<Integer> concurrentLinkedQueue = new ConcurrentLinkedQueue<>(Arrays.asList(1, 2, 3, 4));
            final ConcurrentLinkedDeque<Integer> concurrentLinkedDeque = new ConcurrentLinkedDeque<>(Arrays.asList(1, 2, 3, 4));
            final ConcurrentSkipListSet<Integer> concurrentSkipListSet = new ConcurrentSkipListSet<>(Arrays.asList(1, 2, 3, 4));
            final EnumSet<TestEnum> enumSet = EnumSet.of(TestEnum.CONST_A, TestEnum.CONST_B);
            final EnumSet<TestEnum> emptyEnumSet = EnumSet.noneOf(TestEnum.class);
            final List<Map<Integer, String>> listOfMaps = Arrays.asList(map(1, "one", 2, "two", 3, "three"), Collections.singletonMap(4, "four"), map(5, "five", 6, "six", 7, "seven"));
            final List<Map<Integer, String>> listOfEmptyMaps = Arrays.asList(Collections.emptyMap(), Collections.emptyMap());
            final TestCollection<Integer> testCollection = new TestCollection<>(Arrays.asList(1, 2, 3, 4));
        }
        final CollectionHolder exp = new CollectionHolder();
        assertEquals(exp.collection, converter.convert(
                Collection.class,
                CollectionHolder.class.getDeclaredField("collection").getGenericType(),
                "[1,2,3,4]"), "Unexpected collection");
        assertEquals(exp.list, converter.convert(
                List.class,
                CollectionHolder.class.getDeclaredField("list").getGenericType(),
                "[1,2,3,4]"), "Unexpected list");
        assertEquals(exp.emptyList, converter.convert(
                List.class,
                CollectionHolder.class.getDeclaredField("emptyList").getGenericType(),
                "[]"), "Unexpected emptyList");
        assertEquals(exp.arrayList, converter.convert(
                ArrayList.class,
                CollectionHolder.class.getDeclaredField("arrayList").getGenericType(),
                "[1,2,3,4]"), "Unexpected arrayList");
        assertEquals(exp.vector, converter.convert(
                Vector.class,
                CollectionHolder.class.getDeclaredField("vector").getGenericType(),
                "[1,2,3,4]"), "Unexpected vector");
        assertEquals(exp.set, converter.convert(
                Set.class,
                CollectionHolder.class.getDeclaredField("set").getGenericType(),
                "[1,2,3,4]"), "Unexpected set");
        assertEquals(exp.sortedSet, converter.convert(
                SortedSet.class,
                CollectionHolder.class.getDeclaredField("sortedSet").getGenericType(),
                "[1,2,3,4]"), "Unexpected sortedSet");
        assertEquals(exp.navigableSet, converter.convert(
                NavigableSet.class,
                CollectionHolder.class.getDeclaredField("navigableSet").getGenericType(),
                "[1,2,3,4]"), "Unexpected navigableSet");
        assertEquals(exp.hashSet, converter.convert(
                HashSet.class,
                CollectionHolder.class.getDeclaredField("hashSet").getGenericType(),
                "[1,2,3,4]"), "Unexpected hashSet");
        assertEquals(exp.queue, converter.convert(
                Queue.class,
                CollectionHolder.class.getDeclaredField("queue").getGenericType(),
                "[1,2,3,4]"), "Unexpected queue");
        assertEquals(exp.deque, converter.convert(
                Deque.class,
                CollectionHolder.class.getDeclaredField("deque").getGenericType(),
                "[1,2,3,4]"), "Unexpected deque");
        assertQueueEquals(exp.arrayDeque, converter.convert(
                ArrayDeque.class,
                CollectionHolder.class.getDeclaredField("arrayDeque").getGenericType(),
                "[1,2,3,4]"), "Unexpected arrayDeque");
        assertQueueEquals(exp.concurrentLinkedQueue, converter.convert(
                ConcurrentLinkedQueue.class,
                CollectionHolder.class.getDeclaredField("concurrentLinkedQueue").getGenericType(),
                "[1,2,3,4]"), "Unexpected concurrentLinkedQueue");
        assertQueueEquals(exp.concurrentLinkedDeque, converter.convert(
                ConcurrentLinkedDeque.class,
                CollectionHolder.class.getDeclaredField("concurrentLinkedDeque").getGenericType(),
                "[1,2,3,4]"), "Unexpected concurrentLinkedDeque");
        assertEquals(exp.concurrentSkipListSet, converter.convert(
                ConcurrentSkipListSet.class,
                CollectionHolder.class.getDeclaredField("concurrentSkipListSet").getGenericType(),
                "[1,2,3,4]"), "Unexpected concurrentSkipListSet");
        assertEquals(exp.enumSet, converter.convert(
                EnumSet.class,
                CollectionHolder.class.getDeclaredField("enumSet").getGenericType(),
                "[CONST_A, CONST_B]"), "Unexpected enumSet");
        assertEquals(exp.emptyEnumSet, converter.convert(
                EnumSet.class,
                CollectionHolder.class.getDeclaredField("emptyEnumSet").getGenericType(),
                "[]"), "Unexpected emptyEnumSet");
        assertEquals(exp.listOfMaps, converter.convert(
                List.class,
                CollectionHolder.class.getDeclaredField("listOfMaps").getGenericType(),
                "[{1:one;2:two;3:three},{4:four},{5:five;6:six;7:seven}]"), "Unexpected listOfMaps");
        assertEquals(exp.listOfEmptyMaps, converter.convert(
                List.class,
                CollectionHolder.class.getDeclaredField("listOfEmptyMaps").getGenericType(),
                "[{}, { } ]"), "Unexpected listOfEmptyMaps");

        //expect not supported exception
        try {
            converter.convert(
                    TestCollection.class,
                    CollectionHolder.class.getDeclaredField("testCollection").getGenericType(),
                    "[1,2,3,4]");
            fail("Expected exception due to unsupported collection type for " + exp.testCollection);
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains(TestCollection.class.getName()),
                    "Expected that message contains unsupported collection class name");
        }
    }

    @Test
    public void convertMap() throws Exception {
        final class MapHolder {
            final Map<Integer, String> map = map(1, "one", 2, "two", 3, "three");
            final Map<Integer, String> emptyMap = Collections.emptyMap();
            final SortedMap<Integer, String> sortedMap = new TreeMap<>(map(1, "one", 2, "two", 3, "three"));
            final NavigableMap<Integer, String> navigableMap = new TreeMap<>(map(1, "one", 2, "two", 3, "three"));
            final HashMap<Integer, String> hashMap = new HashMap<>(map(1, "one", 2, "two", 3, "three"));
            final Hashtable<Integer, String> hashtable = new Hashtable<>(map(1, "one", 2, "two", 3, "three"));
            final LinkedHashMap<Integer, String> linkedHashMap = new LinkedHashMap<>(map(1, "one", 2, "two", 3, "three"));
            final ConcurrentMap<Integer, String> concurrentMap = new ConcurrentHashMap<>(map(1, "one", 2, "two", 3, "three"));
            final ConcurrentNavigableMap<Integer, String> concurrentNavigableMap = new ConcurrentSkipListMap<>(map(1, "one", 2, "two", 3, "three"));
            final EnumMap<TestEnum, String> enumMap = new EnumMap<>(map(TestEnum.CONST_A, "a", TestEnum.CONST_B, "b", TestEnum.CONST_C, "c"));
            final EnumMap<TestEnum, String> emptyEnumMap = new EnumMap<>(TestEnum.class);
            final Map<Integer, List<String>> listMap = map(1, Arrays.asList("one", "two"), 3, Arrays.asList("three"), 4, Arrays.asList("four"));
            final Properties properties = new Properties();{
                properties.putAll(map("propA", "a", "propB", "b", "propC", "c"));
            }
            final TestMap<Integer, String> testMap = new TestMap<>(map(1, "one", 2, "two", 3, "three"));
        }
        final MapHolder exp = new MapHolder ();
        assertEquals(exp.emptyMap, converter.convert(
                Map.class,
                MapHolder.class.getDeclaredField("emptyMap").getGenericType(),
                "{ }"), "Unexpected emptyMap");
        assertEquals(exp.map, converter.convert(
                Map.class,
                MapHolder.class.getDeclaredField("map").getGenericType(),
                "{1:one,2:two,3:three}"), "Unexpected map");
        assertEquals(exp.sortedMap, converter.convert(
                SortedMap.class,
                MapHolder.class.getDeclaredField("sortedMap").getGenericType(),
                "{1:one,2:two,3:three}"), "Unexpected sortedMap");
        assertEquals(exp.navigableMap, converter.convert(
                NavigableMap.class,
                MapHolder.class.getDeclaredField("navigableMap").getGenericType(),
                "{1:one,2:two,3:three}"), "Unexpected navigableMap");
        assertEquals(exp.hashMap, converter.convert(
                HashMap.class,
                MapHolder.class.getDeclaredField("hashMap").getGenericType(),
                "{1:one,2:two,3:three}"), "Unexpected hashMap");
        assertEquals(exp.hashtable, converter.convert(
                Hashtable.class,
                MapHolder.class.getDeclaredField("hashMap").getGenericType(),
                "{1:one,2:two,3:three}"), "Unexpected hashtable");
        assertEquals(exp.linkedHashMap, converter.convert(
                LinkedHashMap.class,
                MapHolder.class.getDeclaredField("linkedHashMap").getGenericType(),
                "{1:one,2:two,3:three}"), "Unexpected linkedHashMap");
        assertEquals(exp.concurrentMap, converter.convert(
                ConcurrentMap.class,
                MapHolder.class.getDeclaredField("concurrentMap").getGenericType(),
                "{1:one,2:two,3:three}"), "Unexpected concurrentMap");
        assertEquals(exp.concurrentNavigableMap, converter.convert(
                ConcurrentNavigableMap.class,
                MapHolder.class.getDeclaredField("concurrentNavigableMap").getGenericType(),
                "{1:one,2:two,3:three}"), "Unexpected concurrentNavigableMap");
        assertEquals(exp.emptyEnumMap, converter.convert(
                EnumMap.class,
                MapHolder.class.getDeclaredField("emptyEnumMap").getGenericType(),
                "{}"), "Unexpected emptyEnumMap");
        assertEquals(exp.enumMap, converter.convert(
                EnumMap.class,
                MapHolder.class.getDeclaredField("enumMap").getGenericType(),
                "{CONST_A:a,CONST_B:b,CONST_C:c}"), "Unexpected enumMap");
        assertEquals(exp.properties, converter.convert(
                Properties.class,
                MapHolder.class.getDeclaredField("properties").getGenericType(),
                "{propA:a,propB:b,propC:c}"), "Unexpected properties");
        assertEquals(exp.listMap, converter.convert(
                Map.class,
                MapHolder.class.getDeclaredField("listMap").getGenericType(),
                "{1:[one;two],3:[three],4:[four]}"), "Unexpected listMap");

        //expect not supported exception
        try {
            converter.convert(
                    TestMap.class,
                    MapHolder.class.getDeclaredField("testMap").getGenericType(),
                    "{1:one,2:two,3:three}");
            fail("Expected exception due to unsupported map type for " + exp.testMap);
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains(TestMap.class.getName()),
                    "Expected that message contains unsupported map class name");
        }
    }

    private static <K,V> Map<K,V> map(final K key1, final V val1, final K key2, final V val2, final K key3, final V val3) {
        final Map<K, V> map = new HashMap<K, V>();
        map.put(key1, val1);
        map.put(key2, val2);
        map.put(key3, val3);
        return map;
    }

    private static void assertQueueEquals(final Queue<?> exp, final Queue<?> act, final String msg) {
        assertEquals(exp.getClass(), act.getClass(), msg + "[type]");
        assertEquals(exp.size(), act.size(), msg + "[size]");
        final AtomicInteger index = new AtomicInteger();
        exp.forEach(e -> {
            assertEquals(e, act.poll(), msg + "[" + index.getAndIncrement() + "]");
        });
    }

    private static Number toNumber(final Class<?> primitiveType, final Number value) {
        if (byte.class.equals(primitiveType)) {
            return value.byteValue();
        }
        if (short.class.equals(primitiveType)) {
            return value.shortValue();
        }
        if (int.class.equals(primitiveType)) {
            return value.intValue();
        }
        if (long.class.equals(primitiveType)) {
            return value.longValue();
        }
        if (float.class.equals(primitiveType)) {
            return value.floatValue();
        }
        if (double.class.equals(primitiveType)) {
            return value.doubleValue();
        }
        throw new IllegalArgumentException("Invalid primitive numeric type: " + primitiveType.getName());
    }
}