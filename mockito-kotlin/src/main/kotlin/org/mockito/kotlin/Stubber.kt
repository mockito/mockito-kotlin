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
import org.mockito.internal.stubbing.answers.CallsRealMethods
import org.mockito.internal.stubbing.answers.DoesNothing
import org.mockito.internal.stubbing.answers.Returns
import org.mockito.internal.stubbing.answers.ThrowsException
import org.mockito.internal.stubbing.answers.ThrowsExceptionForClassType
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.internal.CoroutineAwareAnswer
import org.mockito.kotlin.internal.CoroutineAwareAnswer.Companion.wrapAsCoroutineAwareAnswer
import org.mockito.stubbing.Answer
import org.mockito.stubbing.Stubber
import kotlin.reflect.KClass

fun <T> doAnswer(answer: (InvocationOnMock) -> T): Stubber {
    return doAnswerInternal(Answer<T> { invocation -> answer(invocation) })
}

fun <T> doSuspendableAnswer(answer: suspend (KInvocationOnMock) -> T?): Stubber {
    return doAnswerInternal(CoroutineAwareAnswer(answer))
}

fun doCallRealMethod(): Stubber {
    return doAnswerInternal(CallsRealMethods())
}

fun doNothing(): Stubber {
    return doAnswerInternal(DoesNothing.doesNothing())
}

fun doReturn(value: Any?): Stubber {
    return doAnswerInternal(Returns(value))
}

fun doReturn(value: Any?, vararg otherValues: Any?): Stubber {
    return doAnswerInternal(
        listOf(value, *otherValues).map { Returns(it) }
    )
}

fun doThrow(clazz: KClass<out Throwable>): Stubber {
    return doAnswerInternal(ThrowsExceptionForClassType(clazz.java))
}

fun doThrow(vararg throwables: Throwable): Stubber {
    return doAnswerInternal(listOf(*throwables).map { ThrowsException(it) })
}

fun <T> Stubber.whenever(mock: T): T = `when`(mock)

/**
 * Reverse stubber for suspending functions.
 *
 * Warning: Only one method call can be stubbed in the function.
 * Subsequent method calls are ignored!
 */
fun <T> Stubber.onBlocking(mock: T, f: suspend T.() -> Unit) =
    wheneverBlocking(mock, f)

/**
 * Reverse stubber for suspending functions.
 *
 * Warning: Only one method call can be stubbed in the function.
 * Subsequent method calls are ignored!
 */
fun <T> Stubber.wheneverBlocking(mock: T, f: suspend T.() -> Unit) {
    val m = whenever(mock)
    runBlocking { m.f() }
}

private fun doAnswerInternal(vararg answers: Answer<*>): Stubber {
    return doAnswerInternal(listOf(*answers))
}

private fun doAnswerInternal(answers: List<Answer<*>>): Stubber {
    return answers
        .map { it.wrapAsCoroutineAwareAnswer() }
        .let { wrappedAnswers ->
            val stubber = Mockito.doAnswer(wrappedAnswers.first())
            wrappedAnswers
                .drop(1)
                .fold(stubber) { stubber, answer ->
                    stubber.doAnswer(answer)
                }
        }
}
