/*
 * The MIT License
 *
 * Copyright (c) 2024 Mockito contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.mockito.kotlin

import org.mockito.AdditionalMatchers
import org.mockito.kotlin.internal.createInstance
import kotlin.reflect.KClass

/** comparable argument greater than or equal the given value. */
inline fun <reified T : Comparable<T>> geq(value: T): T {
    return AdditionalMatchers.geq(value) ?: createInstance()
}

/** comparable argument greater than or equal to the given value. */
inline fun <reified T : Comparable<T>> leq(value: T): T {
    return AdditionalMatchers.leq(value) ?: createInstance()
}

/** comparable argument greater than the given value. */
inline fun <reified T : Comparable<T>> gt(value: T): T {
    return AdditionalMatchers.gt(value) ?: createInstance()
}

/** comparable argument less than the given value. */
inline fun <reified T : Comparable<T>> lt(value: T): T {
    return AdditionalMatchers.lt(value) ?: createInstance()
}

/** comparable argument equals to the given value according to their compareTo method. */
inline fun <reified T : Comparable<T>> cmpEq(value: T): T {
    return AdditionalMatchers.cmpEq(value) ?: createInstance()
}

/**
 * Any array argument that is equal to the given array, i.e. it has to have the same type, length,
 * and each element has to be equal.
 */
inline fun <reified T> aryEq(value: Array<T>): Array<T> {
  return AdditionalMatchers.aryEq(value) ?: createInstance()
}

/**
 * short array argument that is equal to the given array, i.e. it has to have the same length, and
 * each element has to be equal.
 */
fun aryEq(value: ShortArray): ShortArray {
  return AdditionalMatchers.aryEq(value) ?: createInstance()
}

/**
 * long array argument that is equal to the given array, i.e. it has to have the same length, and
 * each element has to be equal.
 */
fun aryEq(value: LongArray): LongArray {
  return AdditionalMatchers.aryEq(value) ?: createInstance()
}

/**
 * int array argument that is equal to the given array, i.e. it has to have the same length, and
 * each element has to be equal.
 */
fun aryEq(value: IntArray): IntArray {
  return AdditionalMatchers.aryEq(value) ?: createInstance()
}

/**
 * float array argument that is equal to the given array, i.e. it has to have the same length, and
 * each element has to be equal.
 */
fun aryEq(value: FloatArray): FloatArray {
  return AdditionalMatchers.aryEq(value) ?: createInstance()
}

/**
 * double array argument that is equal to the given array, i.e. it has to have the same length, and
 * each element has to be equal.
 */
fun aryEq(value: DoubleArray): DoubleArray {
  return AdditionalMatchers.aryEq(value) ?: createInstance()
}

/**
 * char array argument that is equal to the given array, i.e. it has to have the same length, and
 * each element has to be equal.
 */
fun aryEq(value: CharArray): CharArray {
  return AdditionalMatchers.aryEq(value) ?: createInstance()
}

/**
 * byte array argument that is equal to the given array, i.e. it has to have the same length, and
 * each element has to be equal.
 */
fun aryEq(value: ByteArray): ByteArray {
  return AdditionalMatchers.aryEq(value) ?: createInstance()
}

/**
 * boolean array argument that is equal to the given array, i.e. it has to have the same length, and
 * each element has to be equal.
 */
fun aryEq(value: BooleanArray): BooleanArray {
  return AdditionalMatchers.aryEq(value) ?: createInstance()
}

/** String argument that contains a substring that matches the given regular expression. */
fun find(regex: Regex): String {
    return AdditionalMatchers.find(regex.pattern) ?: ""
}

/** argument that matches both given argument matchers. */
inline fun <reified T : Any> and(left: T, right: T): T {
    return AdditionalMatchers.and(left, right) ?: createInstance()
}

/** argument that matches both given argument matchers. */
inline fun <reified T : Any> or(left: T, right: T): T {
    return AdditionalMatchers.or(left, right) ?: createInstance()
}

/** argument that does not match the given argument matcher. */
inline fun <reified T : Any> not(matcher: T): T {
    return AdditionalMatchers.not(matcher) ?: createInstance()
}

/**
 * float argument that has an absolute difference to the given value that is
 * less than the given delta details.
 */
fun eq(value: Double, delta: Double): Double {
    return AdditionalMatchers.eq(value, delta) ?: 0.0
}

/**
 * double argument that has an absolute difference to the given value that
 * is less than the given delta details.
 */
fun eq(value: Float, delta: Float): Float {
    return AdditionalMatchers.eq(value, delta) ?: 0.0f
}
