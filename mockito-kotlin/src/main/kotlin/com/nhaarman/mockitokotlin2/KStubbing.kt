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
import kotlinx.coroutines.runBlocking
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing
import kotlin.reflect.KClass


inline fun <T> stubbing(
    mock: T,
    stubbing: KStubbing<T>.(T) -> Unit
) {
    KStubbing(mock).stubbing(mock)
}

inline fun <T : Any> T.stub(stubbing: KStubbing<T>.(T) -> Unit): T {
    return apply { KStubbing(this).stubbing(this) }
}

class KStubbing<out T>(val mock: T) {

    fun <R> on(methodCall: R): OngoingStubbing<R> = Mockito.`when`(methodCall)

    fun <R : Any> onGeneric(methodCall: T.() -> R?, c: KClass<R>): OngoingStubbing<R> {
        val r = try {
            mock.methodCall()
        } catch (e: NullPointerException) {
            // An NPE may be thrown by the Kotlin type system when the MockMethodInterceptor returns a
            // null value for a non-nullable generic type.
            // We catch this NPE to return a valid instance.
            // The Mockito state has already been modified at this point to reflect
            // the wanted changes.
            createInstance(c)
        }
        return Mockito.`when`(r)
    }

    inline fun <reified R : Any> onGeneric(noinline methodCall: T.() -> R?): OngoingStubbing<R> {
        return onGeneric(methodCall, R::class)
    }

    fun <R> on(methodCall: T.() -> R): OngoingStubbing<R> {
        return try {
            Mockito.`when`(mock.methodCall())
        } catch (e: NullPointerException) {
            throw MockitoKotlinException(
                  "NullPointerException thrown when stubbing.\nThis may be due to two reasons:\n\t- The method you're trying to stub threw an NPE: look at the stack trace below;\n\t- You're trying to stub a generic method: try `onGeneric` instead.",
                  e
            )
        }
    }

    fun <T : Any, R> KStubbing<T>.onBlocking(
        m: suspend T.() -> R
    ): OngoingStubbing<R> {
        return runBlocking { Mockito.`when`(mock.m()) }
    }
}