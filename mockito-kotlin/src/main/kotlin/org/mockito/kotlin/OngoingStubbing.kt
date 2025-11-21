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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.mockito.Mockito
import org.mockito.internal.stubbing.answers.CallsRealMethods
import org.mockito.internal.stubbing.answers.Returns
import org.mockito.internal.stubbing.answers.ThrowsException
import org.mockito.internal.stubbing.answers.ThrowsExceptionForClassType
import org.mockito.kotlin.internal.CoroutineAwareAnswer
import org.mockito.kotlin.internal.KAnswer
import org.mockito.kotlin.internal.CoroutineAwareAnswer.Companion.wrapAsCoroutineAwareAnswer
import org.mockito.stubbing.Answer
import org.mockito.stubbing.OngoingStubbing
import kotlin.reflect.KClass

/**
 * Enables stubbing methods. Use it when you want the mock to return particular value when particular method is called.
 *
 * Alias for [org.mockito.Mockito.when].
 */
fun <T> whenever(methodCall: () -> T): OngoingStubbing<T> {
    return whenever(methodCall())
}

/**
 * Enables stubbing methods. Use it when you want the mock to return particular value when particular method is called.
 *
 * Alias for [org.mockito.Mockito.when].
 */
fun <T> whenever(methodCall: T): OngoingStubbing<T> {
    return Mockito.`when`<T>(methodCall)
}

/**
 * Enables stubbing suspending methods. Use it when you want the mock to return particular value when particular suspending method is called.
 *
 * Warning: Only one method call can be stubbed in the function.
 * other method calls are ignored!
 */
fun <T> wheneverBlocking(methodCall: suspend CoroutineScope.() -> T): OngoingStubbing<T> {
    return runBlocking { whenever(methodCall()) }
}

/**
 * Sets a return value to be returned when the method is called.
 *
 * Alias for [thenReturn].
 */
infix fun <T> OngoingStubbing<T>.doReturn(value: T): OngoingStubbing<T> {
    return doAnswerInternal(Returns(value))
}

/**
 * Sets a return value to be returned when the method is called.
 */
infix fun <T> OngoingStubbing<T>.thenReturn(value: T): OngoingStubbing<T> {
    return doAnswerInternal(Returns(value))
}

/**
 * Sets consecutive return values to be returned when the method is called.
 *
 * Alias for [OngoingStubbing.thenReturn].
 */
fun <T> OngoingStubbing<T>.doReturn(value: T, vararg otherValues: T): OngoingStubbing<T> {
    return doAnswerInternal(listOf(value, *otherValues).map { Returns(it) })
}

/**
 * Sets consecutive return values to be returned when the method is called.
 */
fun <T> OngoingStubbing<T>.doReturnConsecutively(vararg values: T): OngoingStubbing<T> {
    return doReturnConsecutively(listOf(*values))
}

/**
 * Sets consecutive return values to be returned when the method is called.
 */
infix fun <T> OngoingStubbing<T>.doReturnConsecutively(values: List<T>): OngoingStubbing<T> {
    return doAnswerInternal(values.map { Returns(it) })
}

/**
 * Sets Throwable objects to be thrown when the method is called.
 *
 * Alias for [OngoingStubbing.thenThrow].
 */
infix fun <T> OngoingStubbing<T>.doThrow(throwable: Throwable): OngoingStubbing<T> {
    return doAnswerInternal(ThrowsException(throwable))
}

/**
 * Sets Throwable objects to be thrown when the method is called.
 *
 * Alias for [OngoingStubbing.doThrow].
 */
fun <T> OngoingStubbing<T>.doThrow(throwable: Throwable, vararg otherThrowables: Throwable): OngoingStubbing<T> {
    return doAnswerInternal(listOf(throwable, *otherThrowables).map { ThrowsException(it) })
}

/**
 * Sets a Throwable type to be thrown when the method is called.
 */
infix fun <T> OngoingStubbing<T>.doThrow(clazz: KClass<out Throwable>): OngoingStubbing<T> {
    return doAnswerInternal(ThrowsExceptionForClassType(clazz.java))
}

/**
 * Sets Throwable classes to be thrown when the method is called.
 */
fun <T> OngoingStubbing<T>.doThrow(
    clazz: KClass<out Throwable>,
    vararg otherClasses: KClass<out Throwable>
): OngoingStubbing<T> {
    return doAnswerInternal(listOf(clazz, *otherClasses).map { ThrowsExceptionForClassType(it.java) })
}

/**
 * Calls the real method of the spy/mock when the method is called.
 */
fun <T> OngoingStubbing<T>.doCallRealMethod(): OngoingStubbing<T> {
    return callRealMethod()
}

/**
 * Calls the real method of the spy/mock when the method is called.
 */
fun <T> OngoingStubbing<T>.callRealMethod(): OngoingStubbing<T> {
    return doAnswerInternal(CallsRealMethods())
}

/**
 * Sets a generic Answer for the method.
 *
 * Alias for [OngoingStubbing.thenAnswer].
 */
infix fun <T> OngoingStubbing<T>.doAnswer(answer: Answer<*>): OngoingStubbing<T> {
    return doAnswerInternal(answer)
}

/**
 * Sets a generic Answer for the method using a lambda.
 */
infix fun <T> OngoingStubbing<T>.doAnswer(answer: (KInvocationOnMock) -> T?): OngoingStubbing<T> {
    return doAnswerInternal(KAnswer(answer))
}

/**
 * Sets a generic Answer for a suspend function using a suspend lambda.
 */
infix fun <T> OngoingStubbing<T>.doSuspendableAnswer(answer: suspend (KInvocationOnMock) -> T?): OngoingStubbing<T> {
    return thenAnswer(CoroutineAwareAnswer(answer))
}

private fun <T> OngoingStubbing<T>.doAnswerInternal(vararg answers: Answer<*>): OngoingStubbing<T> {
    return doAnswerInternal(listOf(*answers))
}

private fun <T> OngoingStubbing<T>.doAnswerInternal(answers: List<Answer<*>>): OngoingStubbing<T> {
    return answers.fold(this){ ongoingStubbing, answer ->
        ongoingStubbing.thenAnswer(answer.wrapAsCoroutineAwareAnswer())
    }
}
