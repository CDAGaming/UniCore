/*
 * MIT License
 *
 * Copyright (c) 2018 - 2026 CDAGaming (cstack2011@yahoo.com)
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

package io.github.cdagaming.unicore.impl;

import java.util.Objects;

/**
 * A Set of Three Objects contained within a Mapping
 *
 * @param <T> The first element of this {@link Tuple}
 * @param <U> The second element of this {@link Tuple}
 * @param <V> The third element of this {@link Tuple}
 */
public class Tuple<T, U, V> {

    /**
     * The first element of this {@link Tuple}
     */
    private T first;

    /**
     * The second element of this {@link Tuple}
     */
    private U second;

    /**
     * The third element of this {@link Tuple}
     */
    private V third;

    /**
     * Constructs a new {@link Tuple} with the given values.
     *
     * @param first  the first element
     * @param second the second element
     * @param third  the third element
     */
    public Tuple(T first, U second, V third) {
        put(first, second, third);
    }

    /**
     * Constructs a new {@link Tuple} with the given values.
     *
     * @param other the other {@link Tuple} to copy from
     */
    public Tuple(Tuple<T, U, V> other) {
        this(other.getFirst(), other.getSecond(), other.getThird());
    }

    /**
     * Constructs a new empty {@link Tuple}.
     */
    public Tuple() {
        // N/A
    }

    /**
     * Retrieve a copy of this {@link Tuple}
     *
     * @return a copy of this {@link Tuple}
     */
    public Tuple<T, U, V> copy() {
        return new Tuple<>(this);
    }

    /**
     * Retrieves the first element of this {@link Tuple}.
     *
     * @return the first element of this {@link Tuple}
     */
    public T getFirst() {
        return first;
    }

    /**
     * Sets the first element of this {@link Tuple} to the given value.
     *
     * @param first the first element to be applied
     * @return the resulting first element
     */
    public T setFirst(final T first) {
        this.first = first;
        return first;
    }

    /**
     * Sets the first element of this {@link Tuple} to the given value.
     *
     * @param first the first element to be applied
     * @return the current instance
     */
    public Tuple<T, U, V> putFirst(final T first) {
        setFirst(first);
        return this;
    }

    /**
     * Retrieves the second element of this {@link Tuple}.
     *
     * @return the second element of this {@link Tuple}
     */
    public U getSecond() {
        return second;
    }

    /**
     * Sets the second element of this {@link Tuple} to the given value.
     *
     * @param second the second element to be applied
     * @return the resulting second element
     */
    public U setSecond(final U second) {
        this.second = second;
        return second;
    }

    /**
     * Sets the second element of this {@link Tuple} to the given value.
     *
     * @param second the second element to be applied
     * @return the current instance
     */
    public Tuple<T, U, V> putSecond(final U second) {
        setSecond(second);
        return this;
    }

    /**
     * Retrieves the third element of this {@link Tuple}.
     *
     * @return the third element of this {@link Tuple}
     */
    public V getThird() {
        return third;
    }

    /**
     * Sets the third element of this {@link Tuple} to the given value.
     *
     * @param third the third element to be applied
     * @return the resulting third element
     */
    public V setThird(final V third) {
        this.third = third;
        return third;
    }

    /**
     * Sets the third element of this {@link Tuple} to the given value.
     *
     * @param third the third element to be applied
     * @return the current instance
     */
    public Tuple<T, U, V> putThird(final V third) {
        setThird(third);
        return this;
    }

    /**
     * Sets the elements of this {@link Tuple} to the given values.
     *
     * @param first  the first element to be applied
     * @param second the second element to be applied
     * @param third  the third element to be applied
     * @return the current instance
     */
    public Tuple<T, U, V> put(final T first, final U second, final V third) {
        setFirst(first);
        setSecond(second);
        setThird(third);
        return this;
    }

    /**
     * Determines if elements in two different Tuple objects are equivalent
     *
     * @param obj The Object to compare against
     * @return If the two Opposing objects are equivalent
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Tuple)) {
            return false;
        }

        Tuple<?, ?, ?> other = (Tuple<?, ?, ?>) obj;

        return Objects.equals(other.getFirst(), getFirst()) &&
                Objects.equals(other.getSecond(), getSecond()) &&
                Objects.equals(other.getThird(), getThird());
    }

    @Override
    public String toString() {
        return "Tuple[T=" + (this.getFirst() != null ? this.getFirst().toString() : "N/A") + "; U=" + (this.getSecond() != null ? this.getSecond().toString() : "N/A") + "; V=" + (this.getThird() != null ? this.getThird().toString() : "N/A") + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirst(), getSecond(), getThird());
    }
}
