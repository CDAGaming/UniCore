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

import io.github.cdagaming.unicore.utils.MathUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MathTests {
    @Test
    void testIsWithinValueWithInclusiveRange() {
        assertTrue(MathUtils.isWithinValue(5, 1, 10, true, true, false));
        assertFalse(MathUtils.isWithinValue(0, 1, 10, true, true, false));
    }

    @Test
    void testIsWithinValueWithExclusiveRange() {
        assertTrue(MathUtils.isWithinValue(5, 1, 10, false, false, false));
        assertFalse(MathUtils.isWithinValue(1, 1, 10)); // contains_min=false,contains_max=false,check_sanity=true
    }

    @Test
    void testIsWithinValueWithSanityCheck() {
        assertTrue(MathUtils.isWithinValue(5, 10, 1, true, true, true)); // Min and Max swapped
    }

    @Test
    void testRoundDouble() {
        assertEquals(3.142, MathUtils.roundDouble(3.14159, 3));
        assertEquals(3.14, MathUtils.roundDouble(3.14159, 2));
    }

    @Test
    void testRoundDoubleWithNegativePlaces() {
        assertEquals(3.14159, MathUtils.roundDouble(3.14159, -1));
    }

    @Test
    void testClampFloat() {
        assertEquals(5.0f, MathUtils.clamp(5.0f, 1.0f, 10.0f));
        assertEquals(1.0f, MathUtils.clamp(0.0f, 1.0f, 10.0f));
        assertEquals(10.0f, MathUtils.clamp(15.0f, 1.0f, 10.0f));
    }

    @Test
    void testClampDouble() {
        assertEquals(5.0, MathUtils.clamp(5.0, 1.0, 10.0));
        assertEquals(1.0, MathUtils.clamp(0.0, 1.0, 10.0));
        assertEquals(10.0, MathUtils.clamp(15.0, 1.0, 10.0));
    }

    @Test
    void testClampLong() {
        assertEquals(5L, MathUtils.clamp(5L, 1L, 10L));
        assertEquals(1L, MathUtils.clamp(0L, 1L, 10L));
        assertEquals(10L, MathUtils.clamp(15L, 1L, 10L));
    }

    @Test
    void testClampInt() {
        assertEquals(5, MathUtils.clamp(5, 1, 10));
        assertEquals(10, MathUtils.clamp(11, 1, 10));
    }

    @Test
    void testNormalizeValue() {
        assertEquals(0.5f, MathUtils.normalizeValue(5.5f, 0.1f, 5.0f, 6.0f));
    }

    @Test
    void testDenormalizeValue() {
        assertEquals(5.5f, MathUtils.denormalizeValue(0.5f, 0.1f, 5.0f, 6.0f));
    }

    @Test
    void testSnapToStepClamp() {
        assertEquals(5.0f, MathUtils.snapToStepClamp(5.3f, 1.0f, 1.0f, 10.0f));
        assertEquals(10.0f, MathUtils.snapToStepClamp(10.1f, 0.5f, 1.0f, 10.0f));
    }

    @Test
    void testSnapToStep() {
        assertEquals(5.0f, MathUtils.snapToStep(5.25f, 1.0f));
        assertEquals(5.5f, MathUtils.snapToStep(5.25f, 0.5f));
    }
}
