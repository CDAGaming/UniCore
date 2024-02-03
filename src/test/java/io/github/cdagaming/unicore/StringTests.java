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

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
