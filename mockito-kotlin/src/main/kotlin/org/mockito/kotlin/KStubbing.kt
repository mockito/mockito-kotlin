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

import kotlinx.coroutines.runBlocking
import org.mockito.Mockito
import org.mockito.exceptions.misusing.NotAMockException
import org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress
import org.mockito.kotlin.internal.createInstance
import org.mockito.stubbing.OngoingStubbing
import org.mockito.stubbing.Stubber
import kotlin.reflect.KClass

/**
 * Apply stubbing on the given mock. The stubbed behavior of the mock can then be specified in a supplied lambda.
 */
inline fun <T : Any> stubbing(mock: T, stubbing: KStubbing<T>.(T) -> Unit) {
    KStubbing(mock).stubbing(mock)
}

/**
 * Apply stubbing on the given mock. The stubbed behavior of the mock can then be specified in a supplied lambda.
 */
inline fun <T : Any> T.stub(stubbing: KStubbing<T>.(T) -> Unit): T {
    return apply { KStubbing(this).stubbing(this) }
}

class KStubbing<out T : Any>(val mock: T) {
    init {
        if (!mockingDetails(mock).isMock) throw NotAMockException("Stubbing target is not a mock!")
    }

    /**
     * Enables stubbing methods. Use it when you want the mock to return particular value when particular method is called.
     *
     * Simply put: `When the x method is called then return y`
     */
    fun <R> on(methodCall: R): OngoingStubbing<R> = mockitoWhen(methodCall)

    /**
     * Enables stubbing methods. Use it when you want the mock to return particular value when particular method is called.
     *
     * Simply put: `When the x method is called then return y`
     */
    fun <R> on(methodCall: T.() -> R): OngoingStubbing<R> {
        return try {
            mockitoWhen(mock.methodCall())
        } catch (e: NullPointerException) {
            throw MockitoKotlinException(
                  "NullPointerException thrown when stubbing.\nThis may be due to two reasons:\n\t- The method you're trying to stub threw an NPE: look at the stack trace below;\n\t- You're trying to stub a generic method: try `onGeneric` instead.",
                  e
            )
        }
    }

    /**
     * Enables stubbing suspend functions. Use it when you want the mock to return particular value when particular suspend function is called.
     *
     * Simply put: `When the x suspend function is called then return y`
     */
    fun <R> onBlocking(suspendFunctionCall: suspend T.() -> R): OngoingStubbing<R> {
        return runBlocking { mockitoWhen(mock.suspendFunctionCall()) }
    }

    /**
     * Enables stubbing generic methods with return type [R]. Use it when you want the mock to return particular value when particular method is called.
     *
     * Simply put: `When the x method is called then return y of type R`
     */
    inline fun <reified R : Any> onGeneric(noinline methodCall: T.() -> R?): OngoingStubbing<R?> {
        return onGeneric(methodCall, R::class)
    }


    /**
     * Enables stubbing generic methods with return type [R]. Use it when you want the mock to return particular value when particular method is called.
     *
     * Simply put: `When the x method is called then return y of type R`
     */
    fun <R : Any> onGeneric(methodCall: T.() -> R?, c: KClass<R>): OngoingStubbing<R?> {
        val r = try {
            mock.methodCall()
        } catch (_: NullPointerException) {
            // An NPE may be thrown by the Kotlin type system when the MockMethodInterceptor returns a
            // null value for a non-nullable generic type.
            // We catch this NPE to return a valid instance.
            // The Mockito state has already been modified at this point to reflect
            // the wanted changes.
            createInstance(c)
        }
        return mockitoWhen(r)
    }

    /**
     * Completes stubbing a method, by addressing the method call to apply a given stubbed [org.mockito.stubbing.Answer] on.
     * Use it when you want the mock to return particular value when particular method is called.
     *
     * Simply put: `Return y when the x method is called`
     */
    fun Stubber.on(methodCall: T.() -> Unit) {
        this.`when`(mock).methodCall()
    }

    private fun <R> mockitoWhen(methodCall: R): OngoingStubbing<R> {
        val ongoingStubbing = Mockito.`when`(methodCall)

        if (ongoingStubbing.getMock<T>() != mock) {
            mockingProgress().reset()
            throw IllegalArgumentException("Stubbing of another mock is not allowed")
        }

        return ongoingStubbing
    }
}
