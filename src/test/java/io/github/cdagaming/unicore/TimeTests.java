/*
 * MIT License
 *
 * Copyright (c) 2018 - 2024 CDAGaming (cstack2011@yahoo.com)
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

package io.github.cdagaming.unicore;

import io.github.cdagaming.unicore.utils.TimeUtils;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TimeTests {
    @Test
    void testToStringWithTimeZone() {
        Instant date = Instant.parse("2020-01-01T00:00:00Z");
        String formattedDate = TimeUtils.toString(date, "yyyy-MM-dd HH:mm:ss", "America/New_York");
        assertEquals("2019-12-31 19:00:00", formattedDate);
    }

    @Test
    void testToInstance() {
        String dateString = "2020-01-01 00:00:00";
        Instant instant = TimeUtils.toInstance(dateString, "yyyy-MM-dd HH:mm:ss", "UTC");
        assertNotNull(instant);
        assertEquals("2020-01-01T00:00:00Z", instant.toString());
    }

    @Test
    void testConvertTime() {
        String convertedDate = TimeUtils.convertTime("2020-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss", "UTC", "yyyy-MM-dd", "America/New_York");
        assertEquals("2019-12-31", convertedDate);
    }

    @Test
    void testConvertFormat() {
        String convertedDate = TimeUtils.convertFormat("2020-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy");
        assertEquals("01-01-2020", convertedDate);
    }

    @Test
    void testConvertZone() {
        String convertedDate = TimeUtils.convertZone("2020-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss", "UTC", "America/New_York");
        assertEquals("2019-12-31 19:00:00", convertedDate);
    }

    @Test
    void testEpochToString() {
        long epochTime = Instant.parse("2020-01-01T00:00:00Z").getEpochSecond();
        String formattedDate = TimeUtils.epochToString(epochTime, "yyyy-MM-dd HH:mm:ss", "UTC");
        assertEquals("2020-01-01 00:00:00", formattedDate);
    }

    @Test
    void testStringToEpoch() {
        long epoch = TimeUtils.stringToEpoch("2020-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss", "UTC");
        assertEquals(Instant.parse("2020-01-01T00:00:00Z").toEpochMilli(), epoch);
    }

    @Test
    void testGetDuration() {
        Instant start = Instant.now();
        Instant end = start.plus(Duration.ofHours(1));
        Duration duration = TimeUtils.getDuration(start, end);
        assertEquals(1, duration.toHours());
    }

    @Test
    void testAppendTime() {
        Instant start = Instant.parse("2020-01-01T00:00:00Z");
        Instant modified = (Instant) TimeUtils.appendTime(start, 1, ChronoUnit.DAYS);
        assertEquals("2020-01-02T00:00:00Z", modified.toString());
    }
}
