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
import org.mockito.ArgumentMatcher
import org.mockito.Mockito

/** Object argument that is equal to the given value. */
fun <T> eq(value: T): T {
    return Mockito.eq(value) ?: value
}

/**  Object argument that is the same as the given value. */
fun <T> same(value: T): T {
    return Mockito.same(value) ?: value
}

/** Matches any object, excluding nulls. */
inline fun <reified T : Any> any(): T {
    return Mockito.any(T::class.java) ?: createInstance()
}

/** Matches anything, including nulls. */
inline fun <reified T : Any> anyOrNull(): T {
    return Mockito.any<T>() ?: createInstance()
}

/** Matches any vararg object, including nulls. */
inline fun <reified T : Any> anyVararg(): T {
    return Mockito.any<T>() ?: createInstance()
}

/** Matches any array of type T. */
inline fun <reified T : Any?> anyArray(): Array<T> {
    return Mockito.any(Array<T>::class.java) ?: arrayOf()
}

/**
 * Creates a custom argument matcher.
 * `null` values will never evaluate to `true`.
 *
 * @param predicate An extension function on [T] that returns `true` when a [T] matches the predicate.
 */
inline fun <reified T : Any> argThat(noinline predicate: T.() -> Boolean): T {
    return Mockito.argThat { arg: T? -> arg?.predicate() ?: false } ?: createInstance(
          T::class
    )
}

/**
 * Registers a custom ArgumentMatcher. The original Mockito function registers the matcher and returns null,
 * here the required type is returned.
 *
 * @param matcher The ArgumentMatcher on [T] to be registered.
 */
inline fun <reified T : Any> argThat(matcher: ArgumentMatcher<T>): T {
    return Mockito.argThat(matcher) ?: createInstance()
}

/**
 * Alias for [argThat].
 *
 * Creates a custom argument matcher.
 * `null` values will never evaluate to `true`.
 *
 * @param predicate An extension function on [T] that returns `true` when a [T] matches the predicate.
 */
inline fun <reified T : Any> argForWhich(noinline predicate: T.() -> Boolean): T {
    return argThat(predicate)
}

/**
 * Creates a custom argument matcher.
 * `null` values will never evaluate to `true`.
 *
 * @param predicate A function that returns `true` when given [T] matches the predicate.
 */
inline fun <reified T : Any> argWhere(noinline predicate: (T) -> Boolean): T {
    return argThat(predicate)
}

/**
 * Argument that implements the given class.
 */
inline fun <reified T : Any> isA(): T {
    return Mockito.isA(T::class.java) ?: createInstance()
}

/**
 * `null` argument.
 */
fun <T : Any> isNull(): T? = Mockito.isNull()

/**
 * Not `null` argument.
 */
fun <T : Any> isNotNull(): T? {
    return Mockito.isNotNull()
}

/**
 * Not `null` argument.
 */
fun <T : Any> notNull(): T? {
    return Mockito.notNull()
}

/**
 * Object argument that is reflection-equal to the given value with support for excluding
 * selected fields from a class.
 */
fun <T> refEq(value: T, vararg excludeFields: String): T? {
    return Mockito.refEq(value, *excludeFields)
}

