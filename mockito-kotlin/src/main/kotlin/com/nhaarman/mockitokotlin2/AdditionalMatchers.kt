/*
 * The MIT License
 *
 * Copyright (c) 2018 Niek Haarman
 * Copyright (c) 2007 Mockito contributors
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

package com.nhaarman.mockitokotlin2

import com.nhaarman.mockitokotlin2.internal.createInstance
import org.mockito.AdditionalMatchers

inline fun <reified T : Any> not(matcher: T): T {
    return AdditionalMatchers.not(matcher) ?: createInstance()
}

inline fun <reified T : Any> or(left: T, right: T): T {
    return AdditionalMatchers.or(left, right) ?: createInstance()
}

inline fun <reified T : Any> and(left: T, right: T): T {
    return AdditionalMatchers.and(left, right) ?: createInstance()
}

inline fun <reified T : Comparable<T>> geq(value: T): T {
    return AdditionalMatchers.geq(value) ?: createInstance()
}

inline fun <reified T : Comparable<T>> leq(value: T): T {
    return AdditionalMatchers.leq(value) ?: createInstance()
}

inline fun <reified T : Comparable<T>> gt(value: T): T {
    return AdditionalMatchers.gt(value) ?: createInstance()
}

inline fun <reified T : Comparable<T>> lt(value: T): T {
    return AdditionalMatchers.lt(value) ?: createInstance()
}

inline fun <reified T : Comparable<T>> cmpEq(value: T): T {
    return AdditionalMatchers.cmpEq(value) ?: createInstance()
}

fun find(regex: Regex): String {
    return AdditionalMatchers.find(regex.pattern) ?: createInstance()
}

fun eq(value: Float, delta: Float): Float {
    return AdditionalMatchers.eq(value, delta)
}

fun eq(value: Double, delta: Double): Double {
    return AdditionalMatchers.eq(value, delta)
}