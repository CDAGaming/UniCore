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

package io.github.cdagaming.unicore.utils;

import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.impl.Tuple;
import net.lenni0451.reflect.stream.RStream;
import net.lenni0451.reflect.stream.field.FieldStream;
import net.lenni0451.reflect.stream.field.FieldWrapper;
import net.lenni0451.reflect.stream.method.MethodStream;
import net.lenni0451.reflect.stream.method.MethodWrapper;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * String Utilities for interpreting Strings and Basic Data Types
 *
 * @author CDAGaming
 */
public class StringUtils {
    /**
     * The Character to be interpreted as the start to a Formatting Character
     */
    public static final char COLOR_CHAR = '§';
    /**
     * The character set representing a Tab in the form of four spaces
     */
    public static final String TAB_SPACE = "    ";
    /**
     * A conditional statement for determining if a String is null or empty
     */
    public static final Predicate<String> NULL_OR_EMPTY = StringUtils::isNullOrEmpty;
    /**
     * Regex Pattern for Possible New Line Characters
     */
    public static final Pattern NEW_LINE_PATTERN = Pattern.compile("(\\r\\n|\\r|\\n|\\\\n)");
    /**
     * Regex Pattern for Color and Formatting Codes
     */
    public static final Pattern STRIP_ALL_FORMATTING_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");
    /**
     * Regex Pattern for Color Codes
     */
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-F]");
    /**
     * Regex Pattern for Formatting Codes
     */
    public static final Pattern STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[K-O]");
    /**
     * The Default Charset to use for String Operations
     */
    public static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
    /**
     * Regex Pattern for Base64 Detection
     */
    private static final Pattern BASE64_PATTERN = Pattern.compile("data:(?<type>.+?);base64,(?<data>.+)");
    /**
     * Regex Pattern for Trimmed Uuid Detection
     */
    private static final Pattern TRIMMED_UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
    /**
     * Regex Pattern for Full Uuid Detection
     */
    private static final Pattern FULL_UUID_PATTERN = Pattern.compile("(\\w{8})-(\\w{4})-(\\w{4})-(\\w{4})-(\\w{12})");
    /**
     * Regex Pattern for Brackets containing Digits
     */
    private static final Pattern BRACKET_PATTERN = Pattern.compile("\\([^0-9]*\\d+[^0-9]*\\)");
    /**
     * Regex Pattern for Whitespace characters within a string
     */
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    /**
     * Regex Pattern for Curly Braces within a string
     */
    private static final Pattern CURLY_BRACES_PATTERN = Pattern.compile("[{}]");
    /**
     * Regex Pattern for Non-Alphanumeric characters within a string
     */
    private static final Pattern NON_ALPHANUMERIC_PATTERN = Pattern.compile("[^a-zA-Z0-9_-]");
    /**
     * Regex Pattern for valid Color Formats
     */
    private static final Pattern COLOR_PATTERN = Pattern.compile("^(?:0x([\\dA-Fa-f]{1,8})|#?([\\dA-Fa-f]{6}([\\dA-Fa-f]{2})?))$");
    /**
     * The list of the currently cached classToStream retrievals
     */
    private static final Map<Class<?>, RStream> CLASS_R_STREAM_MAP = newHashMap();
    /**
     * The list of the currently cached streamToFieldStream retrievals
     */
    private static final Map<RStream, FieldStream> FIELD_R_STREAM_MAP = newHashMap();
    /**
     * The list of the currently cached streamToMethodStream retrievals
     */
    private static final Map<RStream, MethodStream> METHOD_R_STREAM_MAP = newHashMap();

    /**
     * Attempts to Convert the specified data into a Valid interpretable Java Color
     *
     * @param red   the red component
     * @param green the green component
     * @param blue  the blue component
     * @param alpha the alpha component
     * @return A Valid Java Color, if successful
     */
    public static Color getColorFrom(final int red, final int green, final int blue, final int alpha) {
        return new Color(red, green, blue, alpha);
    }

    /**
     * Attempts to Convert the specified data into a Valid interpretable Java Color
     *
     * @param red   the red component
     * @param green the green component
     * @param blue  the blue component
     * @return A Valid Java Color, if successful
     */
    public static Color getColorFrom(final int red, final int green, final int blue) {
        return getColorFrom(red, green, blue, 255);
    }

    /**
     * Attempts to Convert the specified data into a Valid interpretable Java Color
     *
     * @param data     the raw interpretable data
     * @param hasAlpha Whether the alpha bits are valid
     * @return A Valid Java Color, if successful
     */
    public static Color getColorFrom(final long data, final boolean hasAlpha) {
        // Extract the alpha component. If not using alpha bits, default to 255.
        long alpha = 0xFF;
        if (hasAlpha) {
            alpha = (data >> 24) & 0xFF;
        }
        return getColorFrom(
                (int) ((data >> 16) & 0xFF),
                (int) ((data >> 8) & 0xFF),
                (int) (data & 0xFF),
                (int) alpha
        );
    }

    /**
     * Attempts to Convert the specified data into a Valid interpretable Java Color
     *
     * @param data the raw interpretable data
     * @return A Valid Java Color, if successful
     */
    public static Color getColorFrom(final long data) {
        return getColorFrom(data, false);
    }

    /**
     * Attempts to Convert the specified data into a Valid interpretable Java Color
     *
     * @param hexColor The inputted Hexadecimal Color String
     * @param fallback The fallback Color to return, if supplied value is invalid
     * @return A Valid Java Color, if successful
     */
    public static Color getColorFrom(final String hexColor, final Color fallback) {
        if (isNullOrEmpty(hexColor)) return fallback;
        final Pair<Boolean, Matcher> matchData = isValidColor(hexColor);
        if (!matchData.getFirst()) {
            return fallback;
        }
        final Matcher m = matchData.getSecond();
        String s = m.group(1);
        if (s == null) s = m.group(2);
        if (s == null) throw new IllegalStateException();
        long color = Long.parseLong(s, 16);
        return getColorFrom(color, s.length() == 8);
    }

    /**
     * Attempts to Convert the specified data into a Valid interpretable Java Color
     *
     * @param hexColor The inputted Hexadecimal Color String
     * @return A Valid Java Color, if successful
     */
    public static Color getColorFrom(final String hexColor) {
        return getColorFrom(hexColor, Color.white);
    }

    /**
     * Offset the specified {@link Color} by the specified factor
     * <p>The Alpha channel is NOT effected by this change
     *
     * @param color  the {@link Color} to offset
     * @param factor the offset factor
     * @return the modified {@link Color} instance
     */
    public static Color offsetColor(final Color color, final float factor) {
        return new Color(
                Math.max((int) (color.getRed() * factor), 0),
                Math.max((int) (color.getGreen() * factor), 0),
                Math.max((int) (color.getBlue() * factor), 0),
                color.getAlpha()
        );
    }

    /**
     * Attempt to retrieve color info for the specified entries
     *
     * @param startColorObj The Starting Color Object
     * @param endColorObj   The Ending Color Object (Returns startColor if null or invalid)
     * @param fallbackColor The fallback color, if starting color is null or invalid
     * @return the processed output
     */
    public static Pair<Color, Color> findColor(final Object startColorObj, final Object endColorObj, final Color fallbackColor) {
        final Color startColor = getColorFrom(startColorObj, fallbackColor);
        final Color endColor = getColorFrom(endColorObj, null);
        return new Pair<>(startColor, getOrDefault(endColor, startColor));
    }

    /**
     * Attempt to retrieve color info for the specified entries
     *
     * @param startColorObj The Starting Color Object (Fallback: {@link Color#white} if invalid)
     * @param endColorObj   The Ending Color Object (Returns startColor if null or invalid)
     * @return the processed output
     */
    public static Pair<Color, Color> findColor(final Object startColorObj, final Object endColorObj) {
        return findColor(startColorObj, endColorObj, Color.white);
    }

    /**
     * Attempt to retrieve color info for the specified entries
     * <p>Returns {@link Color#white} if start color is null or invalid
     *
     * @param startColorObj The Starting Color Object
     * @return the processed output, or {@link Color#white} if null or invalid
     */
    public static Color findColor(final Object startColorObj) {
        return getColorFrom(startColorObj);
    }

    /**
     * Attempt to retrieve color info for the specified entries
     * <p>Returns {@link Color#white} if start color is null or invalid
     *
     * @param startColorObj The Starting Color Object
     * @param fallback      The fallback color, if null or invalid
     * @return the processed output, or the fallback color if null or invalid
     */
    public static Color getColorFrom(final Object startColorObj, final Color fallback) {
        if (startColorObj instanceof String) {
            return getColorFrom((String) startColorObj, fallback);
        } else if (startColorObj instanceof Number) {
            return getColorFrom(((Number) startColorObj).longValue());
        } else if (startColorObj instanceof Color) {
            return (Color) startColorObj;
        }
        return fallback;
    }

    /**
     * Attempt to retrieve color info for the specified entries
     * <p>Returns {@link Color#white} if start color is null or invalid
     *
     * @param startColorObj The Starting Color Object
     * @return the processed output, or {@link Color#white} if null or invalid
     */
    public static Color getColorFrom(final Object startColorObj) {
        return getColorFrom(startColorObj, Color.white);
    }

    /**
     * Determines whether an inputted String classifies as a valid Color Code
     *
     * @param entry The String to evaluate
     * @return {@link Boolean#TRUE} if Entry is classified as a valid Color Code, alongside extra data
     */
    public static Pair<Boolean, Matcher> isValidColor(final String entry) {
        final Matcher m = COLOR_PATTERN.matcher(entry);
        return new Pair<>(m.find(), m);
    }

    /**
     * Converts a String to that of the Specified Charset, in byte form
     *
     * @param original The original String to interpret
     * @param encoding The Charset to encode the bytes under
     * @return The processed byte array
     */
    public static byte[] getBytes(final String original, final String encoding) {
        try {
            if (!isNullOrEmpty(encoding)) {
                return original.getBytes(encoding);
            } else {
                return getBytes(original, DEFAULT_CHARSET.name());
            }
        } catch (Exception ex) {
            return getBytes(original, DEFAULT_CHARSET.name());
        }
    }

    /**
     * Converts a String to that of the Specified Charset, in byte form
     *
     * @param original The original String to interpret
     * @return The processed byte array
     */
    public static byte[] getBytes(final String original) {
        return getBytes(original, null);
    }

    /**
     * Retrieve the stacktrace from an {@link Throwable}
     *
     * @param ex The exception to interpret
     * @return The string representation of the {@link Throwable}
     */
    public static String getStackTrace(final Throwable ex) {
        if (ex == null) {
            return "";
        }
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Converts a String and it's bytes to that of the Specified Charset
     *
     * @param original The original String to interpret
     * @param encoding The Charset to encode the String under
     * @param decode   If we are Decoding an already encoded String
     * @return The converted UTF_8 String, if successful
     */
    @SuppressWarnings("StringOperationCanBeSimplified")
    public static String convertString(final String original, final String encoding, final boolean decode) {
        try {
            if (decode) {
                return new String(getBytes(original), encoding);
            } else {
                final byte[] bytes = getBytes(original, encoding);
                return new String(bytes, 0, bytes.length, DEFAULT_CHARSET);
            }
        } catch (Exception ex) {
            return original;
        }
    }

    /**
     * Attempt to convert the specified object into an array
     *
     * @param original The object to interpret
     * @return the converted array, if able (Returns null if errored)
     */
    public static Object[] getDynamicArray(final Object original) {
        if (!(original instanceof Object[])) {
            try {
                final int len = Array.getLength(original);
                final Object[] objects = new Object[len];
                for (int i = 0; i < len; i++)
                    objects[i] = Array.get(original, i);
                return objects;
            } catch (Throwable ex) {
                return null;
            }
        } else {
            return (Object[]) original;
        }
    }

    /**
     * Retrieve the primary value if it satisfies the condition; Otherwise, use the secondary value
     *
     * @param primary   The primary value to interpret
     * @param secondary The secondary value to interpret
     * @param condition The conditional statement to interpret
     * @param <T>       The type of the values
     * @return the resulting value
     */
    public static <T> T getOrDefault(final T primary, final T secondary, final Predicate<T> condition) {
        return condition.test(primary) ? primary : secondary;
    }

    /**
     * Retrieve the primary value if it is non-null; Otherwise, use the secondary value
     *
     * @param primary   The primary value to interpret
     * @param secondary The secondary value to interpret
     * @param <T>       The type of the values
     * @return the resulting value
     */
    public static <T> T getOrDefault(final T primary, final T secondary) {
        return getOrDefault(primary, secondary, Objects::nonNull);
    }

    /**
     * Retrieve the primary value if it is non-null; Otherwise, use the secondary value
     *
     * @param primary The primary value to interpret
     * @param <T>     The type of the value
     * @return the resulting value
     */
    public static <T> T getOrDefault(final T primary) {
        return getOrDefault(primary, null);
    }

    /**
     * Retrieve the primary value if non-empty; Otherwise, use the secondary value
     *
     * @param primary   The primary value to interpret
     * @param secondary The secondary value to interpret
     * @return the resulting value
     */
    public static String getOrDefault(final String primary, final String secondary) {
        return getOrDefault(primary, secondary, NULL_OR_EMPTY.negate());
    }

    /**
     * Retrieve the primary value if non-empty; Otherwise, use the secondary value
     *
     * @param primary The primary value to interpret
     * @return the resulting value
     */
    public static String getOrDefault(final String primary) {
        return getOrDefault(primary, "");
    }

    /**
     * Reverse the specified list recursively
     *
     * @param list The specified list to interpret
     * @param <T>  The list type
     */
    public static <T> void revlist(List<T> list) {
        // base condition when the list size is 0
        if (list == null || list.size() <= 1)
            return;

        T value = list.remove(0);

        // call the recursive function to reverse
        // the list after removing the first element
        revlist(list);

        // now after the rest of the list has been
        // reversed by the upper recursive call,
        // add the first value at the end
        list.add(value);
    }

    /**
     * Retrieve Matching Values from an input that matches the defined regex
     *
     * @param pattern The Regex Pattern to test against
     * @param input   The original String to get matches from
     * @return the list of found matches
     */
    public static List<String> getMatches(final Pattern pattern, final String input) {
        final List<String> matches = newArrayList();

        if (!isNullOrEmpty(input)) {
            final Matcher m = pattern.matcher(input);

            while (m.find()) {
                matches.add(m.group());
            }
        }

        return matches;
    }

    /**
     * Retrieve the matching value from an input that matches the defined regex
     *
     * @param pattern The Regex Pattern to test against
     * @param input   The original String to get matches from
     * @return the found match, or null if no matches were found
     */
    public static String getMatch(final Pattern pattern, final String input) {
        if (!isNullOrEmpty(input)) {
            final Matcher m = pattern.matcher(input);

            if (m.find()) {
                return m.group();
            }
        }

        return null;
    }

    /**
     * Reduces the Length of a String to the Specified Length
     *
     * @param source The String to evaluate
     * @param length The Maximum Length to reduce the String down towards, beginning at 0
     * @return The newly reduced/minified String
     */
    public static String minifyString(final String source, final int length) {
        if (!isNullOrEmpty(source)) {
            return MathUtils.isWithinValue(length, 0, source.length(), true, true) ? source.substring(0, length) : source;
        } else {
            return "";
        }
    }

    /**
     * Determines whether a String classifies as NULL or EMPTY
     *
     * @param entry           The String to evaluate
     * @param allowWhitespace Whether to allow whitespace strings
     * @return {@link Boolean#TRUE} if Entry is classified as NULL or EMPTY
     */
    public static boolean isNullOrEmpty(String entry, final boolean allowWhitespace) {
        if (entry != null) {
            entry = allowWhitespace ? entry : entry.trim();
        }
        return entry == null || entry.isEmpty() || entry.equalsIgnoreCase("null");
    }

    /**
     * Determines whether a String classifies as NULL or EMPTY
     *
     * @param entry The String to evaluate
     * @return {@link Boolean#TRUE} if Entry is classified as NULL or EMPTY
     */
    public static boolean isNullOrEmpty(final String entry) {
        return isNullOrEmpty(entry, false);
    }

    /**
     * Determines whether the Object's String Interpretation classifies as a valid Boolean
     *
     * @param entry The Object to evaluate
     * @return {@link Boolean#TRUE} if Entry is classified as a valid Boolean
     */
    public static boolean isValidBoolean(final Object entry) {
        return entry != null && isValidBoolean(entry.toString());
    }

    /**
     * Determines whether a String classifies as a valid Boolean
     *
     * @param entry The String to evaluate
     * @return {@link Boolean#TRUE} if Entry is classified as a valid Boolean
     */
    public static boolean isValidBoolean(final String entry) {
        return !isNullOrEmpty(entry) && (entry.equalsIgnoreCase("true") || entry.equalsIgnoreCase("false"));
    }

    /**
     * Determines whether an inputted String classifies as a valid Color Code
     *
     * @param entry The String to evaluate
     * @return {@link Boolean#TRUE} if Entry is classified as a valid Color Code
     */
    public static boolean isValidColorCode(final String entry) {
        return !isNullOrEmpty(entry) && (isValidColor(entry).getFirst() || getValidInteger(entry).getFirst());
    }

    /**
     * Determine whether an inputted Object classifies as a valid Integer
     *
     * @param entry The Object to evaluate
     * @return A Pair with the format of canParse:parsedIntegerIfTrue
     */
    public static Pair<Boolean, Integer> getValidInteger(final Object entry) {
        return entry != null ? getValidInteger(entry.toString()) : new Pair<>(false, 0);
    }

    /**
     * Determine whether an inputted String classifies as a valid Integer
     *
     * @param entry The String to evaluate
     * @return A Pair with the format of canParse:parsedIntegerIfTrue
     */
    public static Pair<Boolean, Integer> getValidInteger(final String entry) {
        final Pair<Boolean, Integer> finalSet = new Pair<>();

        if (!isNullOrEmpty(entry)) {
            try {
                finalSet.setSecond(Integer.parseInt(entry));
                finalSet.setFirst(true);
            } catch (Exception ex) {
                finalSet.setFirst(false);
                finalSet.setSecond(0);
            }
        } else {
            finalSet.setFirst(false);
            finalSet.setSecond(0);
        }

        return finalSet;
    }

    /**
     * Determine whether an inputted Object classifies as a valid Long
     *
     * @param entry The Object to evaluate
     * @return A Pair with the format of canParse:parsedLongIfTrue
     */
    public static Pair<Boolean, Long> getValidLong(final Object entry) {
        return entry != null ? getValidLong(entry.toString()) : new Pair<>(false, 0L);
    }

    /**
     * Determine whether an inputted String classifies as a valid Long
     *
     * @param entry The String to evaluate
     * @return A Pair with the format of canParse:parsedLongIfTrue
     */
    public static Pair<Boolean, Long> getValidLong(final String entry) {
        final Pair<Boolean, Long> finalSet = new Pair<>();

        if (!isNullOrEmpty(entry)) {
            try {
                finalSet.setSecond(Long.parseLong(entry));
                finalSet.setFirst(true);
            } catch (Exception ex) {
                finalSet.setFirst(false);
                finalSet.setSecond(0L);
            }
        } else {
            finalSet.setFirst(false);
            finalSet.setSecond(0L);
        }

        return finalSet;
    }

    /**
     * Determine whether an inputted Object classifies as a valid Boolean
     *
     * @param entry The Object to evaluate
     * @return A Pair with the format of canParse:parsedBoolIfTrue
     */
    public static Pair<Boolean, Boolean> getValidBoolean(final Object entry) {
        return entry != null ? getValidBoolean(entry.toString()) : new Pair<>(false, false);
    }

    /**
     * Determine whether an inputted String classifies as a valid Boolean
     *
     * @param entry The String to evaluate
     * @return A Pair with the format of canParse:parsedBoolIfTrue
     */
    public static Pair<Boolean, Boolean> getValidBoolean(final String entry) {
        final Pair<Boolean, Boolean> finalSet = new Pair<>();

        if (!isNullOrEmpty(entry)) {
            try {
                finalSet.setSecond(Boolean.parseBoolean(entry));
                finalSet.setFirst(true);
            } catch (Exception ex) {
                finalSet.setFirst(false);
                finalSet.setSecond(false);
            }
        } else {
            finalSet.setFirst(false);
            finalSet.setSecond(false);
        }

        return finalSet;
    }

    /**
     * Formats an IP Address based on Input
     *
     * @param input      The original String to evaluate
     * @param returnPort Whether to return the port or the IP without the Port
     * @return Either the IP or the port on their own, depending on conditions
     */
    public static String formatAddress(final String input, final boolean returnPort) {
        if (!isNullOrEmpty(input)) {
            final String[] formatted = input.split(":", 2);
            return !returnPort ? (elementExists(formatted, 0) ? formatted[0].trim() : "127.0.0.1") : (elementExists(formatted, 1) ? formatted[1].trim() : "25565");
        } else {
            return !returnPort ? "127.0.0.1" : "25565";
        }
    }

    /**
     * Converts a String into a Valid and Acceptable Camel-Case Format
     *
     * @param original The original String to evaluate
     * @return The converted and valid String, in camel-case Format
     */
    public static String formatToCamel(final String original) {
        if (isNullOrEmpty(original)) {
            return original;
        } else {
            final String[] words = original.split("[\\W_]+");
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                if (i == 0) {
                    word = word.isEmpty() ? word : word.toLowerCase();
                } else {
                    word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
                }
                builder.append(word);
            }
            return builder.toString();
        }
    }

    /**
     * Converts a String into a Valid and Acceptable Icon Format
     *
     * @param original        The original String to evaluate
     * @param whitespaceIndex The string to replace whitespace with
     * @return The converted and valid String, in an iconKey Format
     */
    public static String formatAsIcon(final String original, final String whitespaceIndex) {
        if (isNullOrEmpty(original)) {
            return original;
        }

        String formattedKey = original.trim();

        formattedKey = replaceMatches(WHITESPACE_PATTERN, formattedKey, whitespaceIndex);

        formattedKey = replaceMatches(NON_ALPHANUMERIC_PATTERN, formattedKey, "_").toLowerCase();
        return formattedKey;
    }

    /**
     * Converts a String into a Valid and Acceptable Icon Format
     *
     * @param original The original String to evaluate
     * @return The converted and valid String, in an iconKey Format
     */
    public static String formatAsIcon(final String original) {
        return formatAsIcon(original, "");
    }

    /**
     * Checks via Regex whether the specified String classifies as a Base64 Image
     *
     * @param original The original string
     * @return Base64 data in the format of isBase64:imageId:formattedImageString
     */
    public static Tuple<Boolean, String, String> isBase64(final String original) {
        String formattedKey = original, imageIdentifier = "";
        final Tuple<Boolean, String, String> finalData = new Tuple<>(false, imageIdentifier, formattedKey);

        if (!isNullOrEmpty(formattedKey)) {
            if (formattedKey.contains(",")) {
                final String[] splitData = formattedKey.split(",", 2);
                imageIdentifier = splitData[0];
                formattedKey = splitData[1];
            }
            finalData.setFirst(BASE64_PATTERN.matcher(imageIdentifier + "," + formattedKey).find());
            finalData.setSecond(imageIdentifier);
            finalData.setThird(formattedKey);
        }
        return finalData;
    }

    /**
     * Checks via Regex whether the specified String classifies as a valid Uuid
     *
     * @param input The original string
     * @return Whether the specified String classifies as a valid Uuid
     */
    public static boolean isValidUuid(final String input) {
        return !isNullOrEmpty(input) &&
                (input.contains("-") ? FULL_UUID_PATTERN : TRIMMED_UUID_PATTERN).matcher(input).find();
    }

    /**
     * Converts a UUID into a String, presuming it is valid and not-null
     * <p>
     * Use {@link StringUtils#isValidUuid(String)} to ensure validity
     *
     * @param input   The original string
     * @param trimmed Whether to return the full or trimmed format of the UUID
     * @return the resulting UUID
     */
    public static String getFromUuid(final String input, final boolean trimmed) {
        if (!isValidUuid(input)) {
            return input;
        }
        if (trimmed) {
            return input.replace("-", "");
        } else {
            return (input.contains("-") ? FULL_UUID_PATTERN : TRIMMED_UUID_PATTERN).matcher(input).replaceFirst("$1-$2-$3-$4-$5");
        }
    }

    /**
     * Converts a UUID into a String, presuming it is valid and not-null
     * <p>
     * Use {@link StringUtils#isValidUuid(String)} to ensure validity
     *
     * @param input The original string
     * @return the resulting UUID
     */
    public static String getFromUuid(final String input) {
        return getFromUuid(input, false);
    }

    /**
     * Converts a UUID into a String, presuming it is valid and not-null
     *
     * @param input The original string
     * @return the resulting UUID
     */
    public static String getFromUuid(final UUID input) {
        return getFromUuid(input.toString());
    }

    /**
     * Converts a String into a UUID, presuming it is valid and not-null
     * <p>
     * Use {@link StringUtils#isValidUuid(String)} to ensure validity
     *
     * @param input The original string
     * @return the resulting UUID
     */
    public static UUID getAsUuid(final String input) {
        return UUID.fromString(getFromUuid(input, false));
    }

    /**
     * Add entries from the specified list, to the original list, if not present already
     *
     * @param original The original list to interpret
     * @param newList  The new list to interpret
     * @param <T>      The list type
     * @return the resulting list
     */
    public static <T> List<T> addEntriesNotPresent(final List<T> original, List<T> newList) {
        for (T entry : newList) {
            if (!original.contains(entry)) {
                original.add(entry);
            }
        }
        return original;
    }

    /**
     * Add entries from the specified list, to the original list, if it passes the filter
     *
     * @param original The original list to interpret
     * @param filter   The filter, at which to interpret the newList through
     * @param newList  The new list to interpret
     * @param <T>      The list type
     * @return the resulting list
     */
    public static <T> List<T> addEntriesNotPresent(final List<T> original, final Predicate<? super T> filter, List<T> newList) {
        newList = newList.stream().filter(filter).collect(Collectors.toList());
        return addEntriesNotPresent(original, newList);
    }

    /**
     * Add entries from the specified list, to the original list, if not present already
     *
     * @param original The original list to interpret
     * @param newList  The new list to interpret
     * @param <T>      The list type
     * @return the resulting list
     */
    public static <T> List<T> addEntriesNotPresent(final List<T> original, Set<T> newList) {
        return addEntriesNotPresent(original, newArrayList(newList));
    }

    /**
     * Add entries from the specified list, to the original list, if it passes the filter
     *
     * @param original The original list to interpret
     * @param filter   The filter, at which to interpret the newList through
     * @param newList  The new list to interpret
     * @param <T>      The list type
     * @return the resulting list
     */
    public static <T> List<T> addEntriesNotPresent(final List<T> original, final Predicate<? super T> filter, Set<T> newList) {
        newList = newList.stream().filter(filter).collect(Collectors.toSet());
        return addEntriesNotPresent(original, newList);
    }

    /**
     * Add entries from the specified list, to the original list, if not present already
     *
     * @param original The original list to interpret
     * @param newList  The new list to interpret
     * @param <T>      The list type
     * @return the resulting list
     */
    public static <T> List<T> addEntriesNotPresent(List<T> original, T[] newList) {
        return addEntriesNotPresent(original, Arrays.asList(newList));
    }

    /**
     * Converts input into a Properly Readable String
     *
     * @param original The original String to format
     * @return The formatted and evaluated String
     */
    public static String formatWord(final String original) {
        return formatWord(original, false);
    }

    /**
     * Converts input into a Properly Readable String
     *
     * @param original The original String to format
     * @param avoid    Flag to ignore method if true
     * @return The formatted and evaluated String
     */
    public static String formatWord(final String original, final boolean avoid) {
        return formatWord(original, avoid, false);
    }

    /**
     * Converts input into a Properly Readable String
     *
     * @param original              The original String to format
     * @param avoid                 Flag to ignore method if true
     * @param skipSymbolReplacement Flag to Skip Symbol Replacement if true
     * @return The formatted and evaluated String
     */
    public static String formatWord(final String original, final boolean avoid, final boolean skipSymbolReplacement) {
        return formatWord(original, avoid, skipSymbolReplacement, -1);
    }

    /**
     * Converts input into a Properly Readable String
     *
     * @param original              The original String to format
     * @param avoid                 Flag to ignore method if true
     * @param skipSymbolReplacement Flag to Skip Symbol Replacement if true
     * @param caseCheckTimes        Times to replace Parts of the String during Capitalization (Use -1 for Infinite)
     * @return The formatted and evaluated String
     */
    public static String formatWord(final String original, final boolean avoid, final boolean skipSymbolReplacement, final int caseCheckTimes) {
        if (isNullOrEmpty(original) || avoid) {
            return original;
        }

        String formattedKey = original.trim();

        formattedKey = normalizeWhitespace(formattedKey);

        if (!skipSymbolReplacement) {
            formattedKey = formattedKey.replace("_", " ")
                    .replace("-", " ");
            formattedKey = stripMatches(BRACKET_PATTERN, formattedKey);
            formattedKey = stripMatches(STRIP_ALL_FORMATTING_PATTERN, formattedKey);
        }

        return removeRepeatWords(capitalizeWord(formattedKey, caseCheckTimes)).trim();
    }

    /**
     * Removes Duplicated Words within an inputted String
     *
     * @param original The original String
     * @return The evaluated String without duplicate words
     */
    public static String removeRepeatWords(final String original) {
        if (isNullOrEmpty(original)) {
            return original;
        } else {
            String lastWord = "";
            StringBuilder finalString = new StringBuilder();
            String[] wordList = original.split(" ");

            for (String word : wordList) {
                if (isNullOrEmpty(lastWord) || !word.equalsIgnoreCase(lastWord)) {
                    finalString.append(word).append(" ");
                    lastWord = word;
                }
            }

            return finalString.toString().trim();
        }
    }

    /**
     * Converts an Identifier into a properly formatted and interpretable Name
     * <p>
     * Note: Additional Logic in Place for Older MC Versions
     *
     * @param originalId The Identifier to format
     * @param formatToId Whether to format as an Icon Key
     * @return The formatted name/icon key
     */
    public static String formatIdentifier(final String originalId, final boolean formatToId) {
        return formatIdentifier(originalId, formatToId, false);
    }

    /**
     * Converts an Identifier into a properly formatted and interpretable Name
     * <p>
     * Note: Additional Logic in Place for Older MC Versions
     *
     * @param originalId The Identifier to format
     * @param formatToId Whether to format as an Icon Key
     * @param avoid      Flag to ignore formatting identifier, if formatToId is false
     * @return The formatted name/icon key
     */
    public static String formatIdentifier(final String originalId, final boolean formatToId, final boolean avoid) {
        if (isNullOrEmpty(originalId)) {
            return originalId;
        }

        String formattedKey = originalId;

        if (formattedKey.equals("WorldProvider")) {
            formattedKey = "overworld";
        } else {
            formattedKey = formattedKey.replace("WorldProvider", "")
                    .replace("BiomeGen", "")
                    .replace("MobSpawner", "");
            formattedKey = normalizeWhitespace(formattedKey);
            formattedKey = stripMatches(CURLY_BRACES_PATTERN, formattedKey);

            if (formattedKey.contains(":")) {
                formattedKey = formattedKey.split(":", 2)[1];
            }
        }

        switch (formattedKey.toLowerCase()) {
            case "surface":
                formattedKey = "overworld";
                break;
            case "hell":
            case "nether":
                formattedKey = "the_nether";
                break;
            case "end":
            case "sky":
                formattedKey = "the_end";
                break;
        }

        return formatToId ? formatAsIcon(formattedKey, "_") : formatWord(formattedKey, avoid);
    }

    /**
     * Whether the character is a valid color character
     *
     * @param colorChar The character to interpret
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean isFormatColor(char colorChar) {
        return colorChar >= '0' && colorChar <= '9' || colorChar >= 'a' && colorChar <= 'f' || colorChar >= 'A' && colorChar <= 'F';
    }

    /**
     * Whether the character is a valid special character
     *
     * @param formatChar The character to interpret
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean isFormatSpecial(char formatChar) {
        return formatChar >= 'k' && formatChar <= 'o' || formatChar >= 'K' && formatChar <= 'O' || formatChar == 'r' || formatChar == 'R';
    }

    /**
     * Returns the Color and Formatting Characters within a String
     *
     * @param text The original String to evaluate
     * @return The formatting and color codes found within the input
     */
    public static String getFormatFromString(final String text) {
        final int stringLength = text.length();
        StringBuilder s = new StringBuilder();
        int index = -1;

        while ((index = text.indexOf(COLOR_CHAR, index + 1)) != -1) {
            if (index < stringLength - 1) {
                final char currentCharacter = text.charAt(index + 1);
                if (isFormatColor(currentCharacter)) {
                    s = new StringBuilder("" + COLOR_CHAR + currentCharacter);
                } else if (isFormatSpecial(currentCharacter)) {
                    s.append(COLOR_CHAR).append(currentCharacter);
                }
            }
        }

        return s.toString();
    }

    /**
     * Capitalizes the words within a specified string
     *
     * @param str          The String to capitalize
     * @param timesToCheck The amount of times to replace within the String (Use -1 for Infinite)
     * @return The capitalized output string
     */
    public static String capitalizeWord(final String str, final int timesToCheck) {
        final StringBuilder s = new StringBuilder();

        // Declare a character of space
        // To identify that the next character is the starting
        // of a new word
        char charIndex = ' ';
        int timesLeft = timesToCheck;
        for (int index = 0; index < str.length(); index++) {
            // If previous character is space and current
            // character is not space then it shows that
            // current letter is the starting of the word
            // We only replace however, whilst the times
            // remaining is more than 0 or is -1 (Infinite)
            if (charIndex == ' ' && str.charAt(index) != ' ' && (timesLeft > 0 || timesLeft == -1)) {
                s.append(Character.toUpperCase(str.charAt(index)));
                if (timesLeft > 0) {
                    timesLeft--;
                }
            } else {
                s.append(str.charAt(index));
            }

            charIndex = str.charAt(index);
        }

        // Return the string with trimming
        return s.toString().trim();
    }

    /**
     * Capitalizes the words within a specified string
     *
     * @param str The String to capitalize
     * @return The capitalized output string
     */
    public static String capitalizeWord(final String str) {
        return capitalizeWord(str, -1);
    }

    /**
     * Converts a String into a List of Strings, split up by new lines
     *
     * @param original        The original String
     * @param allowWhitespace Whether to allow whitespace strings
     * @return The converted, newline-split list from the original String
     */
    public static List<String> splitTextByNewLine(final String original, final boolean allowWhitespace) {
        if (!isNullOrEmpty(original, allowWhitespace)) {
            return newArrayList(NEW_LINE_PATTERN.split(original));
        } else {
            return newArrayList();
        }
    }

    /**
     * Converts a String into a List of Strings, split up by new lines
     *
     * @param original The original String
     * @return The converted, newline-split list from the original String
     */
    public static List<String> splitTextByNewLine(final String original) {
        return splitTextByNewLine(original, false);
    }

    /**
     * Determines if the Specified index exists in the List with a non-null value
     *
     * @param data  The Array to check within
     * @param index The index to check
     * @param <T>   The identified list type
     * @return {@link Boolean#TRUE} if the index element exists in the list with a non-null value
     */
    public static <T> boolean elementExists(final T[] data, final int index) {
        return elementExists(Arrays.asList(data), index);
    }

    /**
     * Determines if the Specified index exists in the List with a non-null value
     *
     * @param data  The List to check within
     * @param index The index to check
     * @param <T>   The identified list type
     * @return {@link Boolean#TRUE} if the index element exists in the list with a non-null value
     */
    public static <T> boolean elementExists(final List<T> data, final int index) {
        boolean result;
        try {
            result = data.size() >= index && data.get(index) != null;
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }

    /**
     * <p>Copies the given array and adds the given element at the end of the new array.
     *
     * <p>The new array contains the same elements of the input
     * array plus the given element in the last position. The component type of
     * the new array is the same as that of the input array.
     *
     * <p>If the input array is {@code null}, a new one element array is returned
     * whose component type is the same as the element, unless the element itself is null,
     * in which case the return type is Object[]
     *
     * @param array   the array to "add" the element to, may be {@code null}
     * @param element the object to add, may be {@code null}
     * @param <T>     the component type of the array
     * @return A new array containing the existing elements plus the new element
     * The returned array type will be that of the input array (unless null),
     * in which case it will have the same type as the element.
     * If both are null, an IllegalArgumentException is thrown
     * @throws IllegalArgumentException if both arguments are null
     */
    public static <T> T[] addToArray(final T[] array, final T element) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        T[] result = Arrays.copyOf(array, array.length + 1);
        result[array.length] = element;
        return result;
    }

    /**
     * Creates a new HashSet containing the specified elements.
     *
     * @param elements the elements to include in the new HashSet
     * @param <T>      the type of elements in the list
     * @return a new HashSet containing the specified elements
     */
    @SafeVarargs
    public static <T> Set<T> newHashSet(final T... elements) {
        final Set<T> data = newHashSet();
        Collections.addAll(data, elements);
        return data;
    }

    /**
     * Creates a new HashSet containing the specified elements.
     *
     * @param <T> the type of elements in the list
     * @return a new HashSet containing the specified elements
     */
    public static <T> Set<T> newHashSet() {
        return new HashSet<>();
    }

    /**
     * Creates a new HashSet containing the specified elements.
     *
     * @param iterator the elements to include in the new HashSet
     * @param <T>      the type of elements in the list
     * @return a new HashSet containing the specified elements
     */
    public static <T> Set<T> newHashSet(final Iterator<T> iterator) {
        final Set<T> set = newHashSet();
        while (iterator.hasNext()) {
            set.add(iterator.next());
        }
        return set;
    }

    /**
     * Creates a new HashSet containing the specified elements.
     *
     * @param iterable the elements to include in the new HashSet
     * @param <T>      the type of elements in the list
     * @return a new HashSet containing the specified elements
     */
    public static <T> Set<T> newHashSet(final Iterable<T> iterable) {
        return newHashSet(iterable.iterator());
    }

    /**
     * Creates a new ArrayList containing the specified elements.
     *
     * @param elements the elements to include in the new ArrayList
     * @param <T>      the type of elements in the list
     * @return a new ArrayList containing the specified elements
     */
    @SafeVarargs
    public static <T> List<T> newArrayList(final T... elements) {
        final List<T> data = newArrayList();
        Collections.addAll(data, elements);
        return data;
    }

    /**
     * Creates a new ArrayList containing the specified elements.
     *
     * @param <T> the type of elements in the list
     * @return a new ArrayList containing the specified elements
     */
    public static <T> List<T> newArrayList() {
        return new ArrayList<>();
    }

    /**
     * Creates a new ArrayList containing the specified elements.
     *
     * @param iterator the elements to include in the new ArrayList
     * @param <T>      the type of elements in the list
     * @return a new ArrayList containing the specified elements
     */
    public static <T> List<T> newArrayList(final Iterator<T> iterator) {
        final List<T> list = newArrayList();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    /**
     * Creates a new ArrayList containing the specified elements.
     *
     * @param iterable the elements to include in the new ArrayList
     * @param <T>      the type of elements in the list
     * @return a new ArrayList containing the specified elements
     */
    public static <T> List<T> newArrayList(final Iterable<T> iterable) {
        return newArrayList(iterable.iterator());
    }

    /**
     * Creates a new instance of {@link LinkedHashMap} with the default initial capacity.
     *
     * @param <K> the type of keys maintained by the new map
     * @param <V> the type of mapped values
     * @return a new instance of {@link LinkedHashMap}
     */
    public static <K, V> Map<K, V> newLinkedHashMap() {
        return new LinkedHashMap<>();
    }

    /**
     * Creates a new instance of {@link LinkedHashMap} that contains the same key-value mappings as the input map.
     *
     * @param <K> the type of keys maintained by the new map
     * @param <V> the type of mapped values
     * @param map the input map whose mappings are to be copied to the new map
     * @return a new instance of {@link LinkedHashMap} that contains the same key-value mappings as the input map
     */
    public static <K, V> Map<K, V> newLinkedHashMap(final Map<? extends K, ? extends V> map) {
        return new LinkedHashMap<>(map);
    }

    /**
     * Creates a new instance of {@link ConcurrentHashMap} with the default initial capacity.
     *
     * @param <K> the type of keys maintained by the new map
     * @param <V> the type of mapped values
     * @return a new instance of {@link ConcurrentHashMap}
     */
    public static <K, V> Map<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap<>();
    }

    /**
     * Creates a new instance of {@link ConcurrentHashMap} that contains the same key-value mappings as the input map.
     *
     * @param <K> the type of keys maintained by the new map
     * @param <V> the type of mapped values
     * @param map the input map whose mappings are to be copied to the new map
     * @return a new instance of {@link ConcurrentHashMap} that contains the same key-value mappings as the input map
     */
    public static <K, V> Map<K, V> newConcurrentHashMap(final Map<? extends K, ? extends V> map) {
        return new ConcurrentHashMap<>(map);
    }

    /**
     * Creates a new instance of {@link HashMap} with the default initial capacity.
     *
     * @param <K> the type of keys maintained by the new map
     * @param <V> the type of mapped values
     * @return a new instance of {@link HashMap}
     */
    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<>();
    }

    /**
     * Creates a new instance of {@link HashMap} that contains the same key-value mappings as the input map.
     *
     * @param <K> the type of keys maintained by the new map
     * @param <V> the type of mapped values
     * @param map the input map whose mappings are to be copied to the new map
     * @return a new instance of {@link HashMap} that contains the same key-value mappings as the input map
     */
    public static <K, V> Map<K, V> newHashMap(final Map<? extends K, ? extends V> map) {
        return new HashMap<>(map);
    }

    /**
     * Creates a new instance of {@link TreeMap} that uses the natural ordering of its keys.
     *
     * @param <K> the type of keys maintained by the new map
     * @param <V> the type of mapped values
     * @return a new instance of {@link TreeMap}
     */
    public static <K extends Comparable<? super K>, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<>();
    }

    /**
     * Creates a new instance of {@link TreeMap} that uses the specified comparator to order its keys.
     *
     * @param <K>        the type of keys maintained by the new map
     * @param <V>        the type of mapped values
     * @param comparator the comparator to use for ordering the keys
     * @return a new instance of {@link TreeMap}
     */
    public static <K, V> TreeMap<K, V> newTreeMap(final Comparator<? super K> comparator) {
        return new TreeMap<>(comparator);
    }

    /**
     * Creates a new instance of {@link TreeMap} that contains the same key-value mappings as the input map.
     *
     * @param <K> the type of keys maintained by the new map
     * @param <V> the type of mapped values
     * @param map the input map whose mappings are to be copied to the new map
     * @return a new instance of {@link TreeMap} that contains the same key-value mappings as the input map
     */
    public static <K extends Comparable<? super K>, V> TreeMap<K, V> newTreeMap(final Map<? extends K, ? extends V> map) {
        return new TreeMap<>(map);
    }

    /**
     * Creates a new instance of {@link ConcurrentSkipListMap} that uses the natural ordering of its keys.
     *
     * @param <K> the type of keys maintained by the new map
     * @param <V> the type of mapped values
     * @return a new instance of {@link ConcurrentSkipListMap}
     */
    public static <K extends Comparable<? super K>, V> ConcurrentSkipListMap<K, V> newConcurrentMap() {
        return new ConcurrentSkipListMap<>();
    }

    /**
     * Creates a new instance of {@link ConcurrentSkipListMap} that uses the specified comparator to order its keys.
     *
     * @param <K>        the type of keys maintained by the new map
     * @param <V>        the type of mapped values
     * @param comparator the comparator to use for ordering the keys
     * @return a new instance of {@link ConcurrentSkipListMap}
     */
    public static <K, V> ConcurrentSkipListMap<K, V> newConcurrentMap(final Comparator<? super K> comparator) {
        return new ConcurrentSkipListMap<>(comparator);
    }

    /**
     * Creates a new instance of {@link ConcurrentSkipListMap} that contains the same key-value mappings as the input map.
     *
     * @param <K> the type of keys maintained by the new map
     * @param <V> the type of mapped values
     * @param map the input map whose mappings are to be copied to the new map
     * @return a new instance of {@link ConcurrentSkipListMap} that contains the same key-value mappings as the input map
     */
    public static <K extends Comparable<? super K>, V> ConcurrentSkipListMap<K, V> newConcurrentMap(final Map<? extends K, ? extends V> map) {
        return new ConcurrentSkipListMap<>(map);
    }

    /**
     * Retrieve a stream of the given class
     *
     * @param classToAccess The class to get the stream of
     * @return The stream instance of the given class
     */
    public static RStream getClassStream(final Class<?> classToAccess) {
        if (!CLASS_R_STREAM_MAP.containsKey(classToAccess)) {
            CLASS_R_STREAM_MAP.put(classToAccess, RStream.of(classToAccess));
        }
        return CLASS_R_STREAM_MAP.get(classToAccess);
    }

    /**
     * Retrieve a Stream of all fields within the specified class
     *
     * @param classToAccess The class object to interpret
     * @return the output stream
     */
    public static FieldStream getFields(final Class<?> classToAccess) {
        final RStream stream = getClassStream(classToAccess);
        if (!FIELD_R_STREAM_MAP.containsKey(stream)) {
            FIELD_R_STREAM_MAP.put(stream, stream.fields());
        }
        return FIELD_R_STREAM_MAP.get(stream);
    }

    /**
     * Retrieve a Stream of all methods within the specified class
     *
     * @param classToAccess The class object to interpret
     * @return the output stream
     */
    public static MethodStream getMethods(final Class<?> classToAccess) {
        final RStream stream = getClassStream(classToAccess);
        if (!METHOD_R_STREAM_MAP.containsKey(stream)) {
            METHOD_R_STREAM_MAP.put(stream, stream.methods());
        }
        return METHOD_R_STREAM_MAP.get(stream);
    }

    /**
     * Retrieve the list of fields present in the specified class
     *
     * @param classToAccess The class object to interpret
     * @return the output String
     */
    public static String getFieldList(final Class<?> classToAccess) {
        final StringBuilder sb = new StringBuilder();
        if (classToAccess != null) {
            sb.append(classToAccess).append(": [\n");
            getFields(classToAccess).forEach(e ->
                    sb.append(TAB_SPACE)
                            .append(e.type())
                            .append(" ")
                            .append(e.name())
                            .append("\n")
            );
            sb.append("]");
        }
        return sb.toString();
    }

    /**
     * Retrieve the list of methods present in the specified class
     *
     * @param classToAccess The class object to interpret
     * @return the output String
     */
    public static String getMethodList(final Class<?> classToAccess) {
        final StringBuilder sb = new StringBuilder();
        if (classToAccess != null) {
            sb.append(classToAccess).append(": [\n");
            getMethods(classToAccess).forEach(e ->
                    sb.append(TAB_SPACE)
                            .append(e.returnType())
                            .append(" ")
                            .append(e.name())
                            .append("(")
                            .append(Arrays.stream(e.parameterTypes())
                                    .map(Class::toString)
                                    .collect(Collectors.joining(", "))
                            )
                            .append(")\n")
            );
            sb.append("]");
        }
        return sb.toString();
    }

    /**
     * Retrieves whether the specified class contains the specified field
     *
     * @param classToAccess The class to access
     * @param fieldNames    A List of Field Names to search for
     * @return whether the specified class contains the specified field
     */
    public static Optional<FieldWrapper> getValidField(final Class<?> classToAccess, final String... fieldNames) {
        if (classToAccess == null || fieldNames == null || fieldNames.length == 0) return Optional.empty();
        return getFields(classToAccess).copy()
                .filter(fieldNames)
                .jstream()
                .findFirst();
    }

    /**
     * Retrieves whether the specified class contains the specified method
     *
     * @param classToAccess  The class to access
     * @param parameterTypes An array of Class objects representing the types of the method's parameters.
     * @param methodNames    A List of Method Names to search for
     * @return whether the specified class contains the specified method
     */
    public static Optional<MethodWrapper> getValidMethod(final Class<?> classToAccess, final Class<?>[] parameterTypes, final String... methodNames) {
        if (classToAccess == null || methodNames == null || methodNames.length == 0) return Optional.empty();
        return getMethods(classToAccess).copy()
                .filter(methodNames).filter(parameterTypes)
                .jstream()
                .findFirst();
    }

    /**
     * Retrieves the Specified Field(s) via Reflection
     *
     * @param classToAccess The class to access with the field(s)
     * @param instance      An Instance of the Class, if needed
     * @param fieldNames    A List of Field Names to search for
     * @return The Found Field Data, if any
     */
    public static Object getField(final Class<?> classToAccess, final Object instance, final String... fieldNames) {
        return getValidField(classToAccess, fieldNames).map(f -> (instance == null ? f.get() : f.get(instance))).orElse(null);
    }

    /**
     * Adjusts the Specified Field(s) in the Target Class via Reflection
     *
     * @param classToAccess The class to access with the field(s)
     * @param instance      An Instance of the Class, if needed
     * @param value         The value to set for the field
     * @param fieldNames    A List of Field Names to search for
     */
    public static void updateField(final Class<?> classToAccess, final Object instance, final Object value, final String... fieldNames) {
        getValidField(classToAccess, fieldNames).ifPresent(fieldWrapper -> {
            if (instance == null) {
                fieldWrapper.set(value);
            } else {
                fieldWrapper.set(instance, value);
            }
        });
    }

    /**
     * Invokes the specified Method in the Target Class via Reflection
     *
     * @param classToAccess  The class to access with the method(s)
     * @param instance       An Instance of the Class, if needed
     * @param parameterTypes An array of Class objects representing the types of the method's parameters.
     * @param parameters     An array of objects representing the method's actual parameters.
     * @param methodNames    A List of Method Names to search for
     * @return the resulting method result
     */
    public static Object executeMethod(final Class<?> classToAccess, final Object instance, final Class<?>[] parameterTypes, final Object[] parameters, final String... methodNames) {
        return getValidMethod(classToAccess, parameterTypes, methodNames)
                .map(methodWrapper -> (instance == null ? methodWrapper.invokeArgs(parameters) : methodWrapper.invokeInstance(instance, parameters)))
                .orElse(null);
    }

    public static String replaceMatches(final Pattern pattern, final String input, final String replacement) {
        return isNullOrEmpty(input) ? input : pattern.matcher(input).replaceAll(replacement);
    }

    public static String stripMatches(final Pattern pattern, final String input) {
        return replaceMatches(pattern, input, "");
    }

    public static String normalizeWhitespace(final String input) {
        return replaceMatches(WHITESPACE_PATTERN, input, " ");
    }

    public static String stripWhitespace(final String input) {
        return stripMatches(WHITESPACE_PATTERN, input);
    }

    /**
     * Strips Color Codes from the inputted String
     *
     * @param input The original String to evaluate
     * @return The Stripped and evaluated String
     */
    public static String stripColors(final String input) {
        return stripMatches(STRIP_COLOR_PATTERN, input);
    }

    /**
     * Strips Formatting Codes from the inputted String
     *
     * @param input The original String to evaluate
     * @return The Stripped and evaluated String
     */
    public static String stripFormatting(final String input) {
        return stripMatches(STRIP_FORMATTING_PATTERN, input);
    }

    /**
     * Strips Color and Formatting Codes from the inputted String
     *
     * @param input The original String to evaluate
     * @return The Stripped and evaluated String
     */
    public static String stripAllFormatting(final String input) {
        return stripMatches(STRIP_ALL_FORMATTING_PATTERN, input);
    }

    /**
     * Normalize Line Separator Characters within the inputted String
     *
     * @param input The original String to evaluate
     * @return The Normalized and evaluated String
     */
    public static String normalizeLines(final String input) {
        return replaceMatches(NEW_LINE_PATTERN, input, "\n");
    }

    /**
     * Normalize the Line Separator and Extra Color Data within the inputted String
     *
     * @param input The original String to evaluate
     * @return The Normalized and evaluated String
     */
    public static String normalize(final String input) {
        return stripAllFormatting(normalizeLines(input));
    }
}
