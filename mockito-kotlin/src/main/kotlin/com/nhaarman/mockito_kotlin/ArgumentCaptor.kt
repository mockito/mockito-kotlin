/*
 * The MIT License
 *
 * Copyright (c) 2016 Niek Haarman
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

package com.nhaarman.mockito_kotlin

import org.mockito.ArgumentCaptor
import kotlin.reflect.KClass

inline fun <reified T : Any> argumentCaptor(): KArgumentCaptor<T> = KArgumentCaptor(ArgumentCaptor.forClass(T::class.java), T::class)

inline fun <reified T : Any> capture(captor: ArgumentCaptor<T>): T = captor.capture() ?: createInstance<T>()

@Deprecated("Use captor.capture() instead.", ReplaceWith("captor.capture()"), DeprecationLevel.ERROR)
inline fun <reified T : Any> capture(captor: KArgumentCaptor<T>): T = captor.capture()

class KArgumentCaptor<out T : Any>(private val captor: ArgumentCaptor<T>, private val tClass: KClass<T>) {

    val value: T
        get() = captor.value

    val allValues: List<T>
        get() = captor.allValues

    fun capture(): T = captor.capture() ?: createInstance(tClass)
}

/**
 * This method is deprecated because its behavior differs from the Java behavior.
 * Instead, use [argumentCaptor] in the traditional way, or use one of
 * [argThat], [argForWhich] or [check].
 */
@Deprecated("Use argumentCaptor() or argThat() instead.", ReplaceWith("check(consumer)"), DeprecationLevel.ERROR)
inline fun <reified T : Any> capture(noinline consumer: (T) -> Unit): T {
    var times = 0
    return argThat { if (++times == 1) consumer.invoke(this); true }
}
