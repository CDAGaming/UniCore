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

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class StringTests {
    @Test
    public void testGetColorFromRGB() {
        // Test with valid RGB values
        Color color = StringUtils.getColorFrom(255, 0, 0); // Red
        assertEquals(Color.red, color);

        // Test with edge case RGB values (boundaries)
        color = StringUtils.getColorFrom(0, 0, 0); // Black
        assertEquals(Color.black, color);

        color = StringUtils.getColorFrom(255, 255, 255); // White
        assertEquals(Color.white, color);
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
        assertEquals(Color.red, color);

        color = StringUtils.getColorFrom(0x00FFFFFF, true); // Transparent White
        assertEquals(new Color(255, 255, 255, 0), color);
    }

    @Test
    public void testGetColorFromHex() {
        // Test with a valid hex color (without alpha)
        Color color = StringUtils.getColorFrom("#FF0000"); // Red
        assertEquals(Color.red, color);

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
        assertEquals(Color.red, colors.getFirst());
        assertEquals(Color.green, colors.getSecond());

        // Test with invalid start color and valid end color
        colors = StringUtils.findColor("invalid", "#00FF00"); // Should default to white to Green
        assertEquals(Color.white, colors.getFirst());
        assertEquals(Color.green, colors.getSecond());

        // Test with valid start color and invalid end color, start color should be used for both
        colors = StringUtils.findColor("#FF0000", "invalid"); // Red to Red
        assertEquals(Color.red, colors.getFirst());
        assertEquals(Color.red, colors.getSecond());
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
        Color startColor = Color.red;
        Color endColor = Color.blue;

        Pair<Color, Color> result = StringUtils.findColor(startColor, endColor);

        assertEquals(startColor, result.getFirst());
        assertEquals(endColor, result.getSecond());
    }

    @Test
    public void testFindColorWithOneColorObject() {
        Color startColor = Color.red;

        Color result = StringUtils.findColor(startColor);

        assertEquals(startColor, result);
    }

    @Test
    public void testFindColorWithStrings() {
        String startColorCode = "#FF0000"; // Red
        String endColorCode = "#0000FF"; // Blue

        Pair<Color, Color> result = StringUtils.findColor(startColorCode, endColorCode);

        assertEquals(Color.red, result.getFirst());
        assertEquals(Color.blue, result.getSecond());
    }

    @Test
    public void testFindColorWithNullEndColor() {
        Color startColor = Color.red;

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
        assertThrows(NullPointerException.class, () ->
                        StringUtils.isValidColor(null),
                "Should throw NullPointerException for null inputs."
        );
    }

    @Test
    void testGetBytesWithValidEncoding() {
        String original = "Hello World";
        byte[] result = StringUtils.getBytes(original, "UTF-8");
        assertArrayEquals(original.getBytes(StandardCharsets.UTF_8), result);
    }

    @Test
    void testGetBytesWithInvalidEncoding() {
        String original = "Hello World";
        byte[] result = StringUtils.getBytes(original, "INVALID_ENCODING");
        assertArrayEquals(original.getBytes(StringUtils.DEFAULT_CHARSET), result);
    }

    @Test
    void testGetBytesWithNullEncoding() {
        String original = "Hello World";
        byte[] result = StringUtils.getBytes(original);
        assertArrayEquals(original.getBytes(StringUtils.DEFAULT_CHARSET), result);
    }

    @Test
    void testGetStackTraceWithNonNullException() {
        Exception ex = new Exception("Test exception");
        String result = StringUtils.getStackTrace(ex);
        assertTrue(result.contains("Test exception"));
    }

    @Test
    void testGetStackTraceWithNullException() {
        String result = StringUtils.getStackTrace(null);
        assertEquals("", result);
    }

    @Test
    void testConvertStringWithoutDecoding() {
        String original = "Hello World";
        String result = StringUtils.convertString(original, "UTF-8", false);
        assertEquals(original, result);
    }

    @Test
    void testConvertStringWithDecoding() {
        String original = "Hello World";
        // Assuming the original string is encoded in UTF-8, and we are decoding it back
        String encoded = new String(StringUtils.getBytes(original, "UTF-8"), StandardCharsets.UTF_8);
        String result = StringUtils.convertString(encoded, "UTF-8", true);
        assertEquals(original, result);
    }

    @Test
    void testConvertStringWithInvalidEncoding() {
        String original = "Hello World";
        // Assuming the method returns the original string on failure
        String result = StringUtils.convertString(original, "INVALID_ENCODING", false);
        assertEquals(original, result);
    }

    @Test
    void testGetDynamicArrayWithArray() {
        Object[] original = new Object[]{"one", "two", "three"};
        Object[] result = StringUtils.getDynamicArray(original);
        assertArrayEquals(original, result);
    }

    @Test
    void testGetDynamicArrayWithNonArray() {
        String original = "Not an array";
        Object[] result = StringUtils.getDynamicArray(original);
        assertNull(result);
    }

    @Test
    void testGetDynamicArrayWithPrimitiveArray() {
        int[] original = new int[]{1, 2, 3};
        Object[] expected = new Object[]{1, 2, 3}; // Expect to convert primitive array to Object array
        Object[] result = StringUtils.getDynamicArray(original);
        assertArrayEquals(expected, result);
    }

    @Test
    void testGetDynamicArrayWithNull() {
        Object[] result = StringUtils.getDynamicArray(null);
        assertNull(result);
    }

    @Test
    void testRevlistWithNonEmptyList() {
        List<Integer> original = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        List<Integer> expected = new ArrayList<>(Arrays.asList(5, 4, 3, 2, 1));
        StringUtils.revlist(original);
        assertEquals(expected, original);
    }

    @Test
    void testRevlistWithEmptyList() {
        List<Object> original = new ArrayList<>();
        StringUtils.revlist(original);
        assertTrue(original.isEmpty());
    }

    @Test
    void testRevlistWithNull() {
        // Since the method does not throw an exception for null input, it should not cause an error.
        assertDoesNotThrow(() -> StringUtils.revlist(null));
    }

    @Test
    void testRevlistWithSingleElement() {
        List<String> original = new ArrayList<>(Collections.singletonList("single"));
        List<String> expected = new ArrayList<>(Collections.singletonList("single"));
        StringUtils.revlist(original);
        assertEquals(expected, original);
    }

    @Test
    void testGetMatchesWithValidRegexAndString() {
        String regexValue = "\\d+"; // Matches one or more digits
        String original = "abc123def456";
        List<String> result = StringUtils.getMatches(regexValue, original);
        assertArrayEquals(new String[]{"123", "456"}, result.toArray(new String[0]));
    }

    @Test
    void testGetMatchesWithNoMatch() {
        String regexValue = "xyz"; // No match expected
        String original = "abc123def456";
        List<String> result = StringUtils.getMatches(regexValue, original);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetMatchesWithEmptyInput() {
        String regexValue = "\\d+";
        List<String> result = StringUtils.getMatches(regexValue, "");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetMatchesWithFlags() {
        String regexValue = "ABC"; // Case-insensitive match due to flag
        String original = "abcABC";
        int flags = Pattern.CASE_INSENSITIVE;
        List<String> result = StringUtils.getMatches(regexValue, original, flags);
        assertArrayEquals(new String[]{"abc", "ABC"}, result.toArray(new String[0]));
    }

    @Test
    void testRemoveSpecificNumberOfMatches() {
        String regexValue = "is|st"; // Match "is" and "st"
        String original = "This is a test string";
        String result = StringUtils.removeMatches(regexValue, original, 0, 2);
        assertEquals("Th  a test string", result);
    }

    @Test
    void testRemoveAllMatches() {
        String regexValue = "\\bmatch\\b"; // Match "match"
        String original = "Remove these matches: match match match";
        String result = StringUtils.removeMatches(regexValue, original);
        assertEquals("Remove these matches:   ", result);
    }

    @Test
    void testRemoveMatchesWithNoMatchInData() {
        String regexValue = "\\d"; // Dummy Value
        String original = "No matches here";
        String result = StringUtils.removeMatches(regexValue, original);
        assertEquals("No matches here", result);
    }

    @Test
    void testRemoveMatchesWithRegexAndFlags() {
        String original = "RegexTest123";
        int flags = Pattern.CASE_INSENSITIVE;
        String result = StringUtils.removeMatches("\\d", original, flags, 2); // Removing two digits
        assertEquals("RegexTest3", result);
    }

    @Test
    void testRemoveMatchesWithRegexRemoveAll() {
        String original = "Remove123All456Digits789";
        String result = StringUtils.removeMatches("\\d+", original); // Remove all digits
        assertEquals("RemoveAllDigits", result);
    }

    @Test
    void testRemoveMatchesWithInvalidMaxMatches() {
        String regexValue = ""; // Match "is" and "st"
        String original = "This is a test string";
        String result = StringUtils.removeMatches(regexValue, original, 0, -2); // Invalid, so no matches should be removed
        assertEquals("This is a test string", result);
    }

    @Test
    void testRemoveMatchesWithNullMatchData() {
        String result = StringUtils.removeMatches(null, null);
        assertNull(result);
    }

    @Test
    void testMinifyString() {
        String result = StringUtils.minifyString("Hello world", 5);
        assertEquals("Hello", result);
    }

    @Test
    void testMinifyStringWithNegativeLength() {
        String result = StringUtils.minifyString("Hello world", -1);
        assertEquals("Hello world", result);
    }

    @Test
    void testMinifyStringWithLengthExceedingSourceLength() {
        String result = StringUtils.minifyString("Hi", 10);
        assertEquals("Hi", result);
    }

    @Test
    void testMinifyEmptyString() {
        String result = StringUtils.minifyString("", 5);
        assertEquals("", result);
    }

    @Test
    void testIsNullOrEmptyWithEmptyString() {
        assertTrue(StringUtils.isNullOrEmpty("", false));
    }

    @Test
    void testIsNullOrEmptyWithWhitespaceAndAllowWhitespace() {
        assertFalse(StringUtils.isNullOrEmpty("   ", true));
    }

    @Test
    void testIsNullOrEmptyWithWhitespaceAndDisallowWhitespace() {
        assertTrue(StringUtils.isNullOrEmpty("   ", false));
    }

    @Test
    void testIsNullOrEmptyWithNull() {
        assertTrue(StringUtils.isNullOrEmpty(null));
    }

    @Test
    void testIsValidBooleanWithStringTrue() {
        assertTrue(StringUtils.isValidBoolean("true"));
    }

    @Test
    void testIsValidBooleanWithStringFalse() {
        assertTrue(StringUtils.isValidBoolean("false"));
    }

    @Test
    void testIsValidBooleanWithInvalidString() {
        assertFalse(StringUtils.isValidBoolean("not a boolean"));
    }

    @Test
    void testGetValidIntegerWithValidNumber() {
        Pair<Boolean, Integer> result = StringUtils.getValidInteger("123");
        assertTrue(result.getFirst());
        assertEquals(123, result.getSecond().intValue());
    }

    @Test
    void testGetValidIntegerWithInvalidNumber() {
        Pair<Boolean, Integer> result = StringUtils.getValidInteger("abc");
        assertFalse(result.getFirst());
    }

    @Test
    void testGetValidLongWithValidNumber() {
        Pair<Boolean, Long> result = StringUtils.getValidLong("1234567890");
        assertTrue(result.getFirst());
        assertEquals(1234567890L, result.getSecond().longValue());
    }

    @Test
    void testGetValidLongWithInvalidNumber() {
        Pair<Boolean, Long> result = StringUtils.getValidLong("abc");
        assertFalse(result.getFirst());
    }

    @Test
    void testGetValidBooleanWithTrue() {
        Pair<Boolean, Boolean> result = StringUtils.getValidBoolean("true");
        assertTrue(result.getFirst());
        assertTrue(result.getSecond());
    }

    @Test
    void testGetValidBooleanWithFalse() {
        Pair<Boolean, Boolean> result = StringUtils.getValidBoolean("false");
        assertTrue(result.getFirst());
        assertFalse(result.getSecond());
    }
}
