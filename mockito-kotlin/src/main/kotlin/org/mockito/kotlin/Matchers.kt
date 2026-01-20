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

package org.mockito.kotlin

import kotlin.reflect.KClass
import org.mockito.AdditionalMatchers
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers
import org.mockito.kotlin.internal.boxAsValueClass
import org.mockito.kotlin.internal.createInstance
import org.mockito.kotlin.internal.toKotlinType
import org.mockito.kotlin.internal.unboxValueClass
import org.mockito.kotlin.internal.valueClassInnerClass

/** Matches an argument that is equal to the given value. */
inline fun <reified T : Any?> eq(value: T): T {
    if (value != null && T::class.isValue) return eqValueClass(value)

    return ArgumentMatchers.eq(value) ?: value
}

/** Matches an argument that is the same as the given value. */
fun <T> same(value: T): T {
    return ArgumentMatchers.same(value) ?: value
}

/** Matches any object, excluding nulls. */
inline fun <reified T : Any> any(): T {
    if (T::class.isValue) return anyValueClass()

    return ArgumentMatchers.any(T::class.java) ?: createInstance()
}

/** Matches anything, including nulls. */
inline fun <reified T : Any> anyOrNull(): T {
    return ArgumentMatchers.any<T>() ?: createInstance()
}

/** Matches any vararg argument, including nulls. */
inline fun <reified T : Any> anyVararg(): T {
    return anyVararg(T::class)
}

fun <T : Any> anyVararg(clazz: KClass<T>): T {
    return ArgumentMatchers.argThat(VarargMatcher(clazz.java)) ?: createInstance(clazz)
}

private class VarargMatcher<T>(private val clazz: Class<T>) : ArgumentMatcher<T> {
    override fun matches(t: T): Boolean = true

    // In Java >= 12 you can do clazz.arrayClass()
    override fun type(): Class<*> = java.lang.reflect.Array.newInstance(clazz, 0).javaClass
}

/** Matches any array of type T. */
inline fun <reified T : Any?> anyArray(): Array<T> {
    return ArgumentMatchers.any(Array<T>::class.java) ?: arrayOf()
}

/** Matches any Kotlin value class with the same boxed type by taking its boxed type. */
inline fun <reified T> anyValueClass(): T {
    val clazz = T::class
    return ArgumentMatchers.any(clazz.valueClassInnerClass().java).boxAsValueClass(clazz)
}

/** Matches an argument that is equal to the given Kotlin value class value. */
inline fun <reified T : Any> eqValueClass(value: T): T {
    require(value::class.isValue) { "${value::class.qualifiedName} is not a value class." }

    val unboxed = value.unboxValueClass()
    val matcher = AdditionalMatchers.or(ArgumentMatchers.eq(value), ArgumentMatchers.eq(unboxed))

    return (matcher ?: unboxed).toKotlinType(T::class)
}

/**
 * Matches an argument that is matching the given predicate. `null` values will never evaluate to
 * `true`.
 *
 * @param predicate An extension function on [T] that returns `true` when a [T] matches the
 *   predicate.
 */
inline fun <reified T : Any> argThat(noinline predicate: T.() -> Boolean): T {
    return ArgumentMatchers.argThat { arg: T? -> arg?.predicate() ?: false }
        ?: createInstance(T::class)
}

/**
 * Matches an argument that is matching the given [ArgumentMatcher].
 *
 * Registers a custom ArgumentMatcher. The original Mockito function registers the matcher and
 * returns null, here the required type is returned.
 *
 * @param matcher The ArgumentMatcher on [T] to be registered.
 */
inline fun <reified T : Any> argThat(matcher: ArgumentMatcher<T>): T {
    return ArgumentMatchers.argThat(matcher) ?: createInstance()
}

/**
 * Matches an argument that is matching the given [ArgumentMatcher].
 *
 * Alias for [argThat].
 *
 * Creates a custom argument matcher. `null` values will never evaluate to `true`.
 *
 * @param predicate An extension function on [T] that returns `true` when a [T] matches the
 *   predicate.
 */
inline fun <reified T : Any> argForWhich(noinline predicate: T.() -> Boolean): T {
    return argThat(predicate)
}

/**
 * Matches an argument that is matching the given predicate. `null` values will never evaluate to
 * `true`.
 *
 * @param predicate A function that returns `true` when given [T] matches the predicate.
 */
inline fun <reified T : Any> argWhere(noinline predicate: (T) -> Boolean): T {
    return argThat(predicate)
}

/** Matches an argument that is instance of the given class. */
inline fun <reified T : Any> isA(): T {
    return ArgumentMatchers.isA(T::class.java) ?: createInstance()
}

/** Matches an argument that is `null`. */
fun <T : Any> isNull(): T? = ArgumentMatchers.isNull()

/** Matches an argument that is not `null`. */
fun <T : Any> isNotNull(): T? {
    return ArgumentMatchers.isNotNull()
}

/** Matches an argument that is not `null`. */
fun <T : Any> notNull(): T? {
    return ArgumentMatchers.notNull()
}

/**
 * Matches an argument that is reflection-equal to the given value with support for excluding
 * selected fields from a class.
 */
inline fun <reified T : Any> refEq(value: T, vararg excludeFields: String): T {
    return ArgumentMatchers.refEq<T>(value, *excludeFields) ?: createInstance()
}
