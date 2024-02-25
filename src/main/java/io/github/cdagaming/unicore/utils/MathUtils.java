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

package io.github.cdagaming.unicore.utils;

/**
 * String Utilities for interpreting equations and Math-Related Data Types
 *
 * @author CDAGaming
 */
public class MathUtils {
    private static final float[] SIN_TABLE = new float[65536];

    static {
        for (int i = 0; i < SIN_TABLE.length; ++i) {
            SIN_TABLE[i] = (float) Math.sin((double) i * Math.PI * 2.0 / SIN_TABLE.length);
        }
    }

    /**
     * Determines whether the specified value is within the specified range
     *
     * @param value        The specified value to interpret
     * @param min          The minimum the value is allowed to be
     * @param max          The maximum the value is allowed to be
     * @param contains_min Whether the range should include the min value
     * @param contains_max Whether the range should include the max value
     * @param check_sanity Whether to sanity check the min and max values
     * @return whether the specified value is within range
     */
    public static boolean isWithinValue(final double value,
                                        double min, double max,
                                        final boolean contains_min, final boolean contains_max,
                                        final boolean check_sanity) {
        // Sanity checks
        if (check_sanity) {
            if (min > max) {
                final double temp = min;
                min = max;
                max = temp;
            }
        }
        // Checking value within range based on contains_min and contains_max
        if (contains_min && contains_max) {
            return value >= min && value <= max;
        } else if (contains_min) {
            return value >= min && value < max;
        } else if (contains_max) {
            return value > min && value <= max;
        } else {
            return value > min && value < max;
        }
    }

    /**
     * Determines whether the specified value is within the specified range
     *
     * @param value        The specified value to interpret
     * @param min          The minimum the value is allowed to be
     * @param max          The maximum the value is allowed to be
     * @param contains_min Whether the range should include the min value
     * @param contains_max Whether the range should include the max value
     * @return whether the specified value is within range
     */
    public static boolean isWithinValue(final double value,
                                        double min, double max,
                                        final boolean contains_min, final boolean contains_max) {
        return isWithinValue(value, min, max, contains_min, contains_max, true);
    }

    /**
     * Determines whether the specified value is within the specified range
     *
     * @param value The specified value to interpret
     * @param min   The minimum the value is allowed to be
     * @param max   The maximum the value is allowed to be
     * @return whether the specified value is within range
     */
    public static boolean isWithinValue(final double value,
                                        double min, double max) {
        return isWithinValue(value, min, max, false, false);
    }

    /**
     * Rounds a Double to the defined decimal place, if possible
     *
     * @param value  The original value to round
     * @param places The amount of places to round upon
     * @return The rounded Double value
     */
    public static double roundDouble(final double value, final int places) {
        if (places < 0) {
            return value;
        }

        final double x = Math.pow(10, places);
        return Math.round(value * x) / x;
    }

    /**
     * Returns the trigonometric sine of an angle.  Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the
     * result is NaN.
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.</ul>
     *
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     *
     * @param value an angle, in radians.
     * @return the sine of the argument.
     */
    public static float sin(final float value) {
        return SIN_TABLE[(int) (value * 10430.378F) & (SIN_TABLE.length - 1)];
    }

    /**
     * Returns the trigonometric cosine of an angle. Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the
     * result is NaN.
     * <li>If the argument is zero, then the result is {@code 1.0}.
     * </ul>
     *
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     *
     * @param value an angle, in radians.
     * @return the cosine of the argument.
     */
    public static float cos(final float value) {
        return SIN_TABLE[(int) (value * 10430.378F + 16384.0F) & (SIN_TABLE.length - 1)];
    }

    /**
     * Returns the smallest (closest to negative infinity)
     * {@code double} value that is greater than or equal to the
     * argument and is equal to a mathematical integer. Special cases:
     * <ul><li>If the argument value is already equal to a
     * mathematical integer, then the result is the same as the
     * argument.  <li>If the argument is NaN or an infinity or
     * positive zero or negative zero, then the result is the same as
     * the argument.  <li>If the argument value is less than zero but
     * greater than -1.0, then the result is negative zero.</ul> Note
     * that the value of {@code Math.ceil(x)} is exactly the
     * value of {@code -Math.floor(-x)}.
     *
     * @param value a value.
     * @return the smallest (closest to negative infinity)
     * floating-point value that is greater than or equal to
     * the argument and is equal to a mathematical integer.
     */
    public static int ceil(final float value) {
        int i = (int) value;
        return value > (float) i ? i + 1 : i;
    }

    /**
     * Returns the smallest (closest to negative infinity)
     * {@code double} value that is greater than or equal to the
     * argument and is equal to a mathematical integer. Special cases:
     * <ul><li>If the argument value is already equal to a
     * mathematical integer, then the result is the same as the
     * argument.  <li>If the argument is NaN or an infinity or
     * positive zero or negative zero, then the result is the same as
     * the argument.  <li>If the argument value is less than zero but
     * greater than -1.0, then the result is negative zero.</ul> Note
     * that the value of {@code Math.ceil(x)} is exactly the
     * value of {@code -Math.floor(-x)}.
     *
     * @param value a value.
     * @return the smallest (closest to negative infinity)
     * floating-point value that is greater than or equal to
     * the argument and is equal to a mathematical integer.
     */
    public static int ceil(final double value) {
        int i = (int) value;
        return value > (double) i ? i + 1 : i;
    }

    /**
     * Clamps the Specified Number between a minimum and maximum limit
     *
     * @param num The number to clamp upon
     * @param min The Minimum Limit for the number
     * @param max The Maximum Limit for the number
     * @return The adjusted and clamped number
     */
    public static int clamp(final int num, final int min, final int max) {
        return Math.min(Math.max(num, min), max);
    }

    /**
     * Clamps the Specified Number between a minimum and maximum limit
     *
     * @param num The number to clamp upon
     * @param min The Minimum Limit for the number
     * @param max The Maximum Limit for the number
     * @return The adjusted and clamped number
     */
    public static long clamp(final long num, final long min, final long max) {
        return Math.min(Math.max(num, min), max);
    }

    /**
     * Clamps the Specified Number between a minimum and maximum limit
     *
     * @param num The number to clamp upon
     * @param min The Minimum Limit for the number
     * @param max The Maximum Limit for the number
     * @return The adjusted and clamped number
     */
    public static float clamp(final float num, final float min, final float max) {
        return Math.min(Math.max(num, min), max);
    }

    /**
     * Clamps the Specified Number between a minimum and maximum limit
     *
     * @param num The number to clamp upon
     * @param min The Minimum Limit for the number
     * @param max The Maximum Limit for the number
     * @return The adjusted and clamped number
     */
    public static double clamp(final double num, final double min, final double max) {
        return Math.min(Math.max(num, min), max);
    }

    /**
     * Linearly Interpolate between the specified values
     *
     * @param percentage The percentage between the two points
     * @param min        The minimum number
     * @param max        The maximum number
     * @return The adjusted number
     */
    public static float lerp(final float percentage, final float min, final float max) {
        return min + percentage * (max - min);
    }

    /**
     * Linearly Interpolate between the specified values
     *
     * @param percentage The percentage between the two points
     * @param min        The minimum number
     * @param max        The maximum number
     * @return The adjusted number
     */
    public static double lerp(final double percentage, final double min, final double max) {
        return min + percentage * (max - min);
    }

    /**
     * Normalize and Clamp the specified value to a number between 0.0f and 1.0f
     *
     * @param num       The number to interpret
     * @param valueStep The rate at which the number is able to move at
     * @param min       The Minimum Limit for the number
     * @param max       The Maximum Limit for the number
     * @return The converted normalized value
     */
    public static float normalizeValue(final float num, final float valueStep, final float min, final float max) {
        return clamp((snapToStepClamp(num, valueStep, min, max) - min) / (max - min), 0.0F, 1.0F);
    }

    /**
     * Denormalize and Expand the specified value to a number between the minimum and maximum slider value
     *
     * @param num       The number to interpret
     * @param valueStep The rate at which the number is able to move at
     * @param min       The Minimum Limit for the number
     * @param max       The Maximum Limit for the number
     * @return The converted denormalized value
     */
    public static float denormalizeValue(final float num, final float valueStep, final float min, final float max) {
        return snapToStepClamp(
                min + (max - min) * clamp(num, 0.0F, 1.0F),
                valueStep, min, max
        );
    }

    /**
     * Snaps the Specified Value to the nearest Step Rate Value, then clamps said value within bounds
     *
     * @param num       The number to interpret
     * @param valueStep The rate at which the number is able to move at
     * @param min       The Minimum Limit for the number
     * @param max       The Maximum Limit for the number
     * @return The Snapped and Clamped proper Slider Value
     */
    public static float snapToStepClamp(final float num, final float valueStep, final float min, final float max) {
        float value = snapToStep(num, valueStep);
        return clamp(value, min, max);
    }

    /**
     * Rounds the Specified Value to the nearest value, using the Step Rate Value
     *
     * @param num       The non-rounded value to interpret
     * @param valueStep The rate at which the number is able to move at
     * @return The Step-Rounded Value at a valid step
     */
    public static float snapToStep(final float num, final float valueStep) {
        float value = num;
        if (valueStep > 0.0F) {
            value = valueStep * (float) Math.round(value / valueStep);
        }

        return value;
    }
}
