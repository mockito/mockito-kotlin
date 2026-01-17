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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.mockito.BDDMockito
import org.mockito.BDDMockito.BDDMyOngoingStubbing
import org.mockito.BDDMockito.Then
import org.mockito.internal.stubbing.answers.Returns
import org.mockito.internal.stubbing.answers.ThrowsException
import org.mockito.internal.stubbing.answers.ThrowsExceptionForClassType
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.internal.CoroutineAwareAnswer
import org.mockito.kotlin.internal.CoroutineAwareAnswer.Companion.wrapAsCoroutineAwareAnswer
import org.mockito.kotlin.internal.safeRunBlocking
import org.mockito.stubbing.Answer

/** Alias for [BDDMockito.given]. */
fun <T> given(methodCall: T): BDDMyOngoingStubbing<T> {
    return BDDMockito.given(methodCall)
}

/** Alias for [BDDMockito.given] with a lambda. */
fun <T> given(methodCall: () -> T): BDDMyOngoingStubbing<T> {
    return given(methodCall())
}

/**
 * Alias for [BDDMockito.given] with a suspending lambda
 *
 * Warning: Only last method call can be stubbed in the function. other method calls are ignored!
 */
fun <T> givenBlocking(methodCall: suspend CoroutineScope.() -> T): BDDMyOngoingStubbing<T> {
    return runBlocking { BDDMockito.given(methodCall()) }
}

/** Alias for [BDDMockito.then]. */
fun <T> then(mock: T): Then<T> {
    return BDDMockito.then(mock)
}

/** Alias for [Then.should], with suspending lambda. */
fun <T, R> Then<T>.shouldBlocking(f: suspend T.() -> R): R {
    val m = should()
    return safeRunBlocking { m.f() }
}

/** Alias for [BDDMyOngoingStubbing.will] */
infix fun <T> BDDMyOngoingStubbing<T>.will(answer: Answer<T>): BDDMyOngoingStubbing<T> {
    return willAnswerInternal(answer)
}

/** Alias for [BDDMyOngoingStubbing.willAnswer], accepting a lambda. */
infix fun <T> BDDMyOngoingStubbing<T>.willAnswer(
    answer: (InvocationOnMock) -> T?
): BDDMyOngoingStubbing<T> {
    return willAnswerInternal { answer.invoke(it) }
}

/** Alias for [BDDMyOngoingStubbing.willAnswer], accepting a suspend lambda. */
infix fun <T> BDDMyOngoingStubbing<T>.willSuspendableAnswer(
    answer: suspend (InvocationOnMock) -> T?
): BDDMyOngoingStubbing<T> {
    return willAnswer(CoroutineAwareAnswer(answer))
}

/** Alias for [BDDMyOngoingStubbing.willReturn]. */
infix fun <T> BDDMyOngoingStubbing<T>.willReturn(value: () -> T): BDDMyOngoingStubbing<T> {
    return willAnswerInternal(Returns(value.invoke()))
}

/** Alias for [BDDMyOngoingStubbing.willThrow]. */
infix fun <T> BDDMyOngoingStubbing<T>.willThrow(
    throwable: () -> Throwable
): BDDMyOngoingStubbing<T> {
    return willAnswerInternal(ThrowsException(throwable.invoke()))
}

/**
 * Sets a Throwable type to be thrown when the method is called.
 *
 * Alias for [BDDMyOngoingStubbing.willThrow]
 */
infix fun <T> BDDMyOngoingStubbing<T>.willThrow(
    throwableType: KClass<out Throwable>
): BDDMyOngoingStubbing<T> {
    return willAnswerInternal(ThrowsExceptionForClassType(throwableType.java))
}

/**
 * Sets Throwable classes to be thrown when the method is called.
 *
 * Alias for [BDDMyOngoingStubbing.willThrow]
 */
fun <T> BDDMyOngoingStubbing<T>.willThrow(
    throwableType: KClass<out Throwable>,
    vararg throwableTypes: KClass<out Throwable>,
): BDDMyOngoingStubbing<T> {
    return willAnswerInternal(
        listOf(throwableType, *throwableTypes).map { ThrowsExceptionForClassType(it.java) }
    )
}

/**
 * Sets consecutive return values to be returned when the method is called. Same as
 * [BDDMyOngoingStubbing.willReturn], but accepts list instead of varargs.
 */
infix fun <T> BDDMyOngoingStubbing<T>.willReturnConsecutively(
    values: List<T>
): BDDMyOngoingStubbing<T> {
    return willAnswerInternal(values.map { Returns(it) })
}

private fun <T> BDDMyOngoingStubbing<T>.willAnswerInternal(
    answer: Answer<*>
): BDDMyOngoingStubbing<T> {
    return willAnswer(answer.wrapAsCoroutineAwareAnswer())
}

private fun <T> BDDMyOngoingStubbing<T>.willAnswerInternal(
    answers: List<Answer<*>>
): BDDMyOngoingStubbing<T> {
    return answers.fold(this) { bddMyOngoingStubbing, answer ->
        bddMyOngoingStubbing.willAnswerInternal(answer)
    }
}
