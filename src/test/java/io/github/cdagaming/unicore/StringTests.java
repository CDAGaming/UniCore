/*
 * MIT License
 *
 * Copyright (c) 2018 - 2025 CDAGaming (cstack2011@yahoo.com)
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
import io.github.cdagaming.unicore.impl.Tuple;
import io.github.cdagaming.unicore.utils.StringUtils;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
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

        // Test with valid start and end integer colors
        colors = StringUtils.findColor(-8355712, -16777216);
        assertEquals(new Color(128, 128, 128), colors.getFirst());
        assertEquals(new Color(0, 0, 0), colors.getSecond());
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
        List<Integer> original = StringUtils.newArrayList(1, 2, 3, 4, 5);
        List<Integer> expected = StringUtils.newArrayList(5, 4, 3, 2, 1);
        StringUtils.revlist(original);
        assertEquals(expected, original);
    }

    @Test
    void testRevlistWithEmptyList() {
        List<Object> original = StringUtils.newArrayList();
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
        List<String> original = StringUtils.newArrayList("single");
        List<String> expected = StringUtils.newArrayList("single");
        StringUtils.revlist(original);
        assertEquals(expected, original);
    }

    @Test
    void testGetMatchesWithValidRegexAndString() {
        Pattern regexValue = Pattern.compile("\\d+"); // Matches one or more digits
        String original = "abc123def456";
        List<String> result = StringUtils.getMatches(regexValue, original);
        assertArrayEquals(new String[]{"123", "456"}, result.toArray(new String[0]));
    }

    @Test
    void testGetMatchesWithNoMatch() {
        Pattern regexValue = Pattern.compile("xyz"); // No match expected
        String original = "abc123def456";
        List<String> result = StringUtils.getMatches(regexValue, original);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetMatchesWithEmptyInput() {
        Pattern regexValue = Pattern.compile("\\d+");
        List<String> result = StringUtils.getMatches(regexValue, "");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetMatchesWithFlags() {
        String regexValue = "ABC"; // Case-insensitive match due to flag
        String original = "abcABC";
        int flags = Pattern.CASE_INSENSITIVE;
        List<String> result = StringUtils.getMatches(Pattern.compile(regexValue, flags), original);
        assertArrayEquals(new String[]{"abc", "ABC"}, result.toArray(new String[0]));
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

    @Test
    void testFormatAddressWithIPAndPort() {
        assertEquals("192.168.1.1", StringUtils.formatAddress("192.168.1.1:8080", false));
        assertEquals("8080", StringUtils.formatAddress("192.168.1.1:8080", true));
    }

    @Test
    void testFormatAddressWithOnlyIP() {
        assertEquals("192.168.1.1", StringUtils.formatAddress("192.168.1.1", false));
        assertEquals("25565", StringUtils.formatAddress("192.168.1.1", true));
    }

    @Test
    void testFormatAddressWithEmptyString() {
        assertEquals("127.0.0.1", StringUtils.formatAddress("", false));
        assertEquals("25565", StringUtils.formatAddress("", true));
    }

    @Test
    void testFormatToCamel() {
        assertEquals("helloWorld", StringUtils.formatToCamel("Hello world"));
        assertEquals("helloWorldAgain", StringUtils.formatToCamel("Hello World_Again"));
    }

    @Test
    void testFormatToCamelWithEmptyString() {
        assertEquals("", StringUtils.formatToCamel(""));
    }

    @Test
    void testFormatAsIconWithWhitespaceReplacement() {
        assertEquals("hello_world", StringUtils.formatAsIcon("Hello World", "_"));
        assertEquals("hello-world", StringUtils.formatAsIcon("Hello World", "-"));
    }

    @Test
    void testFormatAsIconWithoutWhitespaceReplacement() {
        assertEquals("helloworld", StringUtils.formatAsIcon("Hello World"));
    }

    @Test
    void testFormatAsIconWithSpecialCharacters() {
        assertEquals("icon_key_", StringUtils.formatAsIcon("Icon!Key@", "_"));
    }

    @Test
    void testIsBase64WithValidBase64Image() {
        String base64Image = "data:image/png;base64,iVBORw0KGgo=";
        Tuple<Boolean, String, String> result = StringUtils.isBase64(base64Image);
        assertTrue(result.getFirst());
        assertEquals("data:image/png;base64", result.getSecond());
        assertEquals("iVBORw0KGgo=", result.getThird());
    }

    @Test
    void testIsBase64WithEmptyString() {
        Tuple<Boolean, String, String> result = StringUtils.isBase64("");
        assertFalse(result.getFirst());
    }

    @Test
    void testIsValidUuidWithFullUuid() {
        assertTrue(StringUtils.isValidUuid("123e4567-e89b-12d3-a456-426614174000"));
    }

    @Test
    void testIsValidUuidWithTrimmedUuid() {
        assertTrue(StringUtils.isValidUuid("123e4567e89b12d3a456426614174000"));
    }

    @Test
    void testIsValidUuidWithInvalidUuid() {
        assertFalse(StringUtils.isValidUuid("INVALID_UUID"));
    }

    @Test
    void testIsValidUuidWithEmptyString() {
        assertFalse(StringUtils.isValidUuid(""));
    }
}
