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

import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.utils.StringUtils;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;

class StringTests {
    @Test
    public void testGetColorFromRGB() {
        // Test with valid RGB values
        Color color = StringUtils.getColorFrom(255, 0, 0); // Red
        assertEquals(new Color(255, 0, 0), color);

        // Test with edge case RGB values (boundaries)
        color = StringUtils.getColorFrom(0, 0, 0); // Black
        assertEquals(new Color(0, 0, 0), color);

        color = StringUtils.getColorFrom(255, 255, 255); // White
        assertEquals(new Color(255, 255, 255), color);
    }

    @Test
    public void testGetColorFromRGBA() {
        // Test with valid RGBA values
        Color color = StringUtils.getColorFrom(255, 0, 0, 128); // Red with 50% opacity
        assertEquals(new Color(255, 0, 0, 128), color);
    }

    @Test
    public void testGetColorFromInt() {
        // Test with a valid integer
        Color color = StringUtils.getColorFrom(0xFF0000); // Red
        assertEquals(new Color(255, 0, 0), color);

        color = StringUtils.getColorFrom(0x00FFFFFF, true); // Transparent White
        assertEquals(new Color(255, 255, 255, 0), color);
    }

    @Test
    public void testGetColorFromHex() {
        // Test with a valid hex color (without alpha)
        Color color = StringUtils.getColorFrom("#FF0000"); // Red
        assertEquals(new Color(255, 0, 0), color);

        // Test with a valid hex color (with alpha)
        color = StringUtils.getColorFrom("#80FF0000"); // Red with 50% opacity
        assertEquals(new Color(255, 0, 0, 128), color);

        // Test with an invalid hex should return white
        color = StringUtils.getColorFrom("invalid");
        assertEquals(Color.white, color);
    }

    @Test
    public void testFindColor() {
        // Test with valid start and end colors
        Pair<Color, Color> colors = StringUtils.findColor("#FF0000", "#00FF00"); // Red to Green
        assertEquals(new Color(255, 0, 0), colors.getFirst());
        assertEquals(new Color(0, 255, 0), colors.getSecond());

        // Test with invalid start color and valid end color
        colors = StringUtils.findColor("invalid", "#00FF00"); // Should default to white to Green
        assertEquals(Color.white, colors.getFirst());
        assertEquals(Color.white, colors.getSecond());

        // Test with valid start color and invalid end color, start color should be used for both
        colors = StringUtils.findColor("#FF0000", "invalid"); // Red to Red
        assertEquals(new Color(255, 0, 0), colors.getFirst());
        assertEquals(new Color(255, 0, 0), colors.getSecond());
    }

    @Test
    public void testOffsetColor() {
        Color original = new Color(100, 150, 200);
        float factor = 0.5f;

        Color result = StringUtils.offsetColor(original, factor);

        assertEquals(new Color(50, 75, 100), result);
    }

    @Test
    public void testOffsetColorWithFactorGreaterThanOne() {
        Color original = new Color(100, 100, 100);
        float factor = 1.5f;

        Color result = StringUtils.offsetColor(original, factor);

        assertEquals(new Color(150, 150, 150, 255), result);
    }

    @Test
    public void testOffsetColorWithZeroFactor() {
        Color original = new Color(100, 150, 200);
        float factor = 0f;

        Color result = StringUtils.offsetColor(original, factor);

        assertEquals(new Color(0, 0, 0, original.getAlpha()), result);
    }

    @Test
    public void testFindColorWithColorObjects() {
        Color startColor = Color.RED;
        Color endColor = Color.BLUE;

        Pair<Color, Color> result = StringUtils.findColor(startColor, endColor);

        assertEquals(startColor, result.getFirst());
        assertEquals(endColor, result.getSecond());
    }

    @Test
    public void testFindColorWithOneColorObject() {
        Color startColor = Color.RED;

        Color result = StringUtils.findColor(startColor);

        assertEquals(startColor, result);
    }

    @Test
    public void testFindColorWithStrings() {
        String startColorCode = "#FF0000"; // Red
        String endColorCode = "#0000FF"; // Blue

        Pair<Color, Color> result = StringUtils.findColor(startColorCode, endColorCode);

        assertEquals(Color.RED, result.getFirst());
        assertEquals(Color.BLUE, result.getSecond());
    }

    @Test
    public void testFindColorWithNullEndColor() {
        Color startColor = Color.RED;

        Pair<Color, Color> result = StringUtils.findColor(startColor, null);

        assertEquals(startColor, result.getFirst());
        assertEquals(startColor, result.getSecond());
    }

    @Test
    public void testIsValidColorWithHexRGB() {
        Pair<Boolean, Matcher> result = StringUtils.isValidColor("#FFAABB");
        assertTrue(result.getFirst());
    }

    @Test
    public void testIsValidColorWithHexARGB() {
        Pair<Boolean, Matcher> result = StringUtils.isValidColor("#80FFAABB");
        assertTrue(result.getFirst());
    }

    @Test
    public void testIsValidColorWith0xRGB() {
        Pair<Boolean, Matcher> result = StringUtils.isValidColor("0xFFAABB");
        assertTrue(result.getFirst());
    }

    @Test
    public void testIsValidColorWith0xARGB() {
        Pair<Boolean, Matcher> result = StringUtils.isValidColor("0x80FFAABB");
        assertTrue(result.getFirst());
    }

    @Test
    public void testIsValidColorWithInvalidCode() {
        Pair<Boolean, Matcher> result = StringUtils.isValidColor("#GGHHII");
        assertFalse(result.getFirst());
    }

    @Test
    public void testIsValidColorWithShortHex() {
        Pair<Boolean, Matcher> result = StringUtils.isValidColor("#FFF");
        assertFalse(result.getFirst(), "Should fail for short (3-digit) hex codes.");
    }

    @Test
    public void testIsValidColorWithLongInvalidHex() {
        Pair<Boolean, Matcher> result = StringUtils.isValidColor("#FFAABBCCEE");
        assertFalse(result.getFirst(), "Should fail for hex codes longer than 8 digits.");
    }

    @Test
    public void testIsValidColorWithEmptyString() {
        Pair<Boolean, Matcher> result = StringUtils.isValidColor("");
        assertFalse(result.getFirst(), "Should fail for empty strings.");
    }

    @Test
    public void testIsValidColorWithNull() {
        assertThrows(NullPointerException.class, () -> {
            StringUtils.isValidColor(null);
        }, "Should throw NullPointerException for null inputs.");
    }
}
