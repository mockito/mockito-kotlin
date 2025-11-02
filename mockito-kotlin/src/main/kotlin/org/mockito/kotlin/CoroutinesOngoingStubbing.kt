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

import kotlinx.coroutines.delay
import org.mockito.internal.stubbing.answers.Returns
import org.mockito.kotlin.internal.SuspendableAnswer
import org.mockito.kotlin.internal.lastInvocationMethodIsSuspend
import org.mockito.stubbing.Answer
import org.mockito.stubbing.OngoingStubbing
import kotlin.reflect.KClass

class CoroutinesOngoingStubbing<T>(val mockitoOngoingStubbing: OngoingStubbing<T>) {
    /**
     * Sets a return value to be returned when the method is called.
     *
     * Alias for [thenReturn].
     */
    infix fun doReturn(t: T): CoroutinesOngoingStubbing<T> {
        return thenReturn(t)
    }

    /**
     * Sets a return value to be returned when the method is called.
     */
    @Suppress("UNCHECKED_CAST")
    infix fun thenReturn(t: T): CoroutinesOngoingStubbing<T> {
        return thenAnswer(Returns(t))
    }

    /**
     * Sets an answer for the suspendable function using a suspendable lambda.
     *
     * Alias for [thenAnswer].
     */
    infix fun doAnswer(answer: suspend (KInvocationOnMock) -> T?): CoroutinesOngoingStubbing<T> {
        return thenAnswer(answer)
    }

    /**
     * Sets an answer for the suspendable function using a suspendable lambda.
     *
     * Alias for [thenAnswer].
     *
     * This deprecated method was added for backwards compatibility.
     */
    @Deprecated(
        " use doAnswer() or thenAnswer() instead.",
        level = DeprecationLevel.WARNING
    )
    infix fun doSuspendableAnswer(answer: suspend (KInvocationOnMock) -> T?): CoroutinesOngoingStubbing<T> {
        return thenAnswer(answer)
    }

    /**
     * Sets an answer for the suspendable function using a suspendable lambda.
     */
    infix fun thenAnswer(answer: suspend (KInvocationOnMock) -> T?): CoroutinesOngoingStubbing<T> {
        return thenAnswer(SuspendableAnswer(answer))
    }

    /**
     * Sets an answer for the suspendable function by wrapping a non-suspendable Mockito Answer.
     *
     * Alias for [thenAnswer].
     */
    infix fun doAnswer(answer: Answer<*>): CoroutinesOngoingStubbing<T> {
        return thenAnswer(answer)
    }

    /**
     * Sets an answer for the suspendable function by wrapping a non-suspendable Mockito Answer.
     */
    infix fun thenAnswer(answer: Answer<*>): CoroutinesOngoingStubbing<T> {
        return if (mockitoOngoingStubbing.lastInvocationMethodIsSuspend ?: false) {
            answer.wrapAsSuspendableAnswer()
        } else {
            answer // to support stubbing a sync method call while using wheneverBlocking()/onBlocking()
        }.let {
            CoroutinesOngoingStubbing(mockitoOngoingStubbing.thenAnswer(it))
        }
    }

    private fun Answer<*>.wrapAsSuspendableAnswer(): Answer<out Any?> =
        (this as? SuspendableAnswer<*>) ?: SuspendableAnswer(
            { invocation ->
                suspendToEnforceProperValueBoxing()
                this.answer(invocation)
            }
        )

    private suspend fun suspendToEnforceProperValueBoxing() {
        // delaying for 1 ms, forces a suspension to happen.
        // This (somehow) ensures that value class instances will be properly boxed when the
        // answer is yielded by the mock
        delay(1)
    }

    /**
     * Sets consecutive return values to be returned when the method is called.
     *
     * Alias for [OngoingStubbing.thenReturn].
     */
    fun doReturn(t: T, vararg ts: T): CoroutinesOngoingStubbing<T> {
        return thenReturn(listOf(t, *ts))
    }

    /**
     * Sets consecutive return values to be returned when the method is called.
     */
    fun doReturnConsecutively(vararg ts: T): CoroutinesOngoingStubbing<T> {
        return doReturnConsecutively(listOf(*ts))
    }

    /**
     * Sets consecutive return values to be returned when the method is called.
     */
    infix fun doReturnConsecutively(ts: List<T>): CoroutinesOngoingStubbing<T> {
        return thenReturn(ts)
    }

    /**
     * Sets Throwable objects to be thrown when the method is called.
     *
     * Alias for [OngoingStubbing.thenThrow].
     */
    infix fun doThrow(t: Throwable): CoroutinesOngoingStubbing<T> {
        return CoroutinesOngoingStubbing(
            mockitoOngoingStubbing.thenThrow(t)
        )
    }

    /**
     * Sets Throwable objects to be thrown when the method is called.
     *
     * Alias for [OngoingStubbing.doThrow].
     */
    fun doThrow(
        t: Throwable,
        vararg ts: Throwable
    ): CoroutinesOngoingStubbing<T> {
        return CoroutinesOngoingStubbing(
            mockitoOngoingStubbing.thenThrow(t, *ts)
        )
    }

    /**
     * Sets a Throwable type to be thrown when the method is called.
     */
    infix fun doThrow(t: KClass<out Throwable>): CoroutinesOngoingStubbing<T> {
        return CoroutinesOngoingStubbing(
            mockitoOngoingStubbing.thenThrow(t.java)
        )
    }

    /**
     * Sets Throwable classes to be thrown when the method is called.
     */
    fun doThrow(
        t: KClass<out Throwable>,
        vararg ts: KClass<out Throwable>
    ): CoroutinesOngoingStubbing<T> {
        return CoroutinesOngoingStubbing(
            mockitoOngoingStubbing.thenThrow(
                t.java,
                *ts.map { it.java }.toTypedArray()
            )
        )
    }

    private fun thenReturn(values: List<T>): CoroutinesOngoingStubbing<T> {
        return thenAnswer(
            values.map { value ->
                SuspendableAnswer(
                    {
                        suspendToEnforceProperValueBoxing()
                        value
                    }
                )
            }
        )
    }

    private fun thenAnswer(answers: List<SuspendableAnswer<T>>): CoroutinesOngoingStubbing<T> {
        return CoroutinesOngoingStubbing(
            answers
                .fold(mockitoOngoingStubbing) { stubbing, answer -> stubbing.thenAnswer(answer) }
        )
    }
}
