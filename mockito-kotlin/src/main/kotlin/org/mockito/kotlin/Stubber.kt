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
import kotlinx.coroutines.runBlocking
import org.mockito.Mockito
import org.mockito.internal.stubbing.answers.*
import org.mockito.kotlin.internal.CoroutineAwareAnswer
import org.mockito.kotlin.internal.CoroutineAwareAnswer.Companion.wrapAsCoroutineAwareAnswer
import org.mockito.kotlin.internal.KAnswer
import org.mockito.stubbing.Answer
import org.mockito.stubbing.Stubber

/**
 * Sets a generic answer, specified with a lambda, to be applied in reverse stubbing.
 *
 * Example:
 * ```kotlin
 *      doAnswer { "result" }.whenever(mock).someMethod()
 * ```
 *
 * This function is an alias for Mockito's [Mockito.doAnswer].
 *
 * See examples in javadoc for [Mockito] class
 *
 * @param answer to answer to apply when the stubbed method/function is being called.
 * @return Stubber object used to stub fluently.
 */
fun <T> doAnswer(answer: (KInvocationOnMock) -> T): Stubber {
    return doAnswerInternal(KAnswer(answer))
}

/**
 * Sets a generic answer, specified with a suspendable lambda, to be applied in reverse stubbing.
 *
 * Example:
 * ```kotlin
 *      doSuspendableAnswer { "result" }.whenever(mock).someMethod()
 * ```
 *
 * This function is an alias for Mockito's [Mockito.doAnswer], but also taking extra steps to wire
 * the suspendable lambda answer properly into the Kotlin's coroutine context of the stubbed
 * suspendable function call.
 *
 * See examples in javadoc for [Mockito] class
 *
 * @param answer to answer to apply when the stubbed method/function is being called.
 * @return Stubber object used to stub fluently.
 */
fun <T> doSuspendableAnswer(answer: suspend (KInvocationOnMock) -> T): Stubber {
    return Mockito.doAnswer(CoroutineAwareAnswer(answer))
}

/**
 * Sets to call the real implementation of a method in a mock, to be applied in reverse stubbing.
 *
 * Example:
 * ```kotlin
 *      doCallRealMethod().whenever(mock).someMethod()
 * ```
 *
 * This function is an alias for Mockito's [Mockito.doCallRealMethod].
 *
 * @return Stubber object used to stub fluently.
 */
fun doCallRealMethod(): Stubber {
    return doAnswerInternal(CallsRealMethods())
}

/**
 * Sets to do nothing, to be applied in reverse stubbing. This comes handy is some rare cases, like:
 * - stubbing consecutive calls with different behavior
 * - when spying a real object, suppress the real implementation of the spied object
 *
 * Example:
 * ```kotlin
 *      doNothing().whenever(mock).someMethod()
 * ```
 *
 * This function is an alias for Mockito's [Mockito.doNothing].
 *
 * @return Stubber object used to stub fluently.
 */
fun doNothing(): Stubber {
    return doAnswerInternal(DoesNothing.doesNothing())
}

/**
 * Sets a value to be returned, to be applied in reverse stubbing.
 *
 * Example:
 * ```kotlin
 *      doReturn(10).whenever(mock).someMethod()
 * ```
 *
 * This function is an alias for Mockito's [Mockito.doReturn].
 *
 * See examples in javadoc for [Mockito] class
 *
 * @param value return value for the method/function invocation.
 * @return Stubber object used to stub fluently.
 */
fun doReturn(value: Any?): Stubber {
    return doAnswerInternal(Returns(value))
}

/**
 * Sets values to be returned on consecutive invocations, to be applied in reverse stubbing.
 *
 * Example:
 * ```kotlin
 *      doReturn(10, 20).whenever(mock).someMethod()
 * ```
 *
 * This function is an alias for Mockito's [Mockito.doReturn].
 *
 * See examples in javadoc for [Mockito] class
 *
 * @param value return value for the first method/function invocation.
 * @param values return values for the next method/function invocations.
 * @return Stubber object used to stub fluently.
 */
fun doReturn(value: Any?, vararg values: Any?): Stubber {
    return doAnswerInternal(listOf(value, *values).map { Returns(it) })
}

/**
 * Sets a throwable to be thrown, to be applied in reverse stubbing.
 *
 * Example:
 * ```kotlin
 *      doThrow(IllegalArgumentException()).whenever(mock).someMethod()
 * ```
 *
 * This function is an alias for Mockito's [Mockito.doThrow].
 *
 * See examples in javadoc for [Mockito] class
 *
 * @param throwable to be thrown on the method/function invocation.
 * @return Stubber object used to stub fluently.
 */
fun doThrow(throwable: Throwable): Stubber {
    return doAnswerInternal(ThrowsException(throwable))
}

/**
 * Sets throwables to be thrown on consecutive calls, to be applied in reverse stubbing.
 *
 * Example:
 * ```kotlin
 *      doThrow(RuntimeException(), IOException()).whenever(mock).someMethod()
 * ```
 *
 * This function is an alias for Mockito's [Mockito.doThrow].
 *
 * You can specify [throwables] to be thrown for consecutive invocations. In that case the last
 * throwable determines the behavior of further consecutive invocations.
 *
 * See examples in javadoc for [Mockito] class
 *
 * @param throwable to be thrown on the first method/function invocation.
 * @param throwables to be thrown on the next method/function invocations.
 * @return Stubber object used to stub fluently.
 */
fun doThrow(throwable: Throwable, vararg throwables: Throwable): Stubber {
    return doAnswerInternal(listOf(throwable, *throwables).map { ThrowsException(it) })
}

/**
 * Sets a throwable type to be thrown, to be applied in reverse stubbing.
 *
 * Example:
 * ```kotlin
 *      doThrow(IllegalArgumentException::class).whenever(mock).someMethod()
 * ```
 *
 * This function is an alias for Mockito's [Mockito.doThrow].
 *
 * See examples in javadoc for [Mockito] class
 *
 * @param throwableType to be thrown on the method/function invocation.
 * @return Stubber object used to stub fluently.
 */
fun doThrow(throwableType: KClass<out Throwable>): Stubber {
    return doAnswerInternal(ThrowsExceptionForClassType(throwableType.java))
}

/**
 * Sets throwable types to be thrown on consecutive calls, to be applied in reverse stubbing.
 *
 * Example:
 * ```kotlin
 *      doThrow(IllegalArgumentException::class, NullPointerException::class)
 *          .whenever(mock)
 *          .someMethod()
 * ```
 *
 * This function is an alias for Mockito's [Mockito.doThrow].
 *
 * You can specify [throwableTypes] to be thrown for consecutive invocations. In that case the last
 * throwable type determines the behavior of further consecutive invocations.
 *
 * See examples in javadoc for [Mockito] class
 *
 * @param throwableType to be thrown on the first method/function invocation.
 * @param throwableTypes to be thrown on the next method/function invocations.
 * @return Stubber object used to stub fluently.
 */
fun doThrow(
    throwableType: KClass<out Throwable>,
    vararg throwableTypes: KClass<out Throwable>,
): Stubber {
    return doAnswerInternal(
        listOf(throwableType, *throwableTypes).map { ThrowsExceptionForClassType(it.java) }
    )
}

/**
 * Sets the mock to apply the reverse stubbing on.
 *
 * Reverse stubbing is especially useful when stubbing a void method (or Unit function) to throw an
 * exception.
 *
 * Example:
 * ```kotlin
 *      mock<SynchronousFunctions> {
 *          doThrow(RuntimeException()).whenever { string("test") }
 *      }
 * ```
 *
 * This function is an alias for Mockito's [Mockito.`when`]. So, for more detailed documentation,
 * please refer to the Javadoc of that method in the [Mockito] class.
 *
 * @param mock the mock to stub the method/function call on.
 * @return the mock used to stub the method/function call fluently.
 */
fun <T> Stubber.whenever(mock: T) = `when`(mock)!!

/**
 * Sets the mock and the method call to be stubbed. With this version of whenever you can reverse
 * stub either synchronous or suspendable function calls.
 *
 * Reverse stubbing is especially useful when stubbing a void method (or Unit function) to throw an
 * exception.
 *
 * Warning: Only one method call can be stubbed in the function. Subsequent method calls are
 * ignored!
 *
 * Example:
 * ```kotlin
 *      doThrow(RuntimeException()).whenever(mock) { someMethod() }
 * ```
 *
 * This function is an alias for Mockito's [Mockito.`when`]. So, for more detailed documentation,
 * please refer to the Javadoc of that method in the [Mockito] class.
 *
 * @param mock the mock to stub the method/function call on.
 * @param methodCall (regular or suspendable) lambda, wrapping the method/function call to be
 *   stubbed.
 */
fun <T> Stubber.whenever(mock: T, methodCall: suspend T.() -> Unit) {
    whenever(mock).let { runBlocking { it.methodCall() } }
}

/**
 * Sets the mock and the method call to be stubbed. With this version of whenever you can reverse
 * stub either synchronous or suspendable function calls.
 *
 * Reverse stubbing is especially useful when stubbing a void method (or Unit function) to throw an
 * exception.
 *
 * Warning: Only one method call can be stubbed in the function. Subsequent method calls are
 * ignored!
 *
 * Example:
 * ```kotlin
 *      doThrow(RuntimeException()).wheneverBlocking(mock) { someMethod() }
 * ```
 *
 * This function is an alias for [whenever].
 *
 * @param mock the mock to stub the method/function call on.
 * @param methodCall (regular or suspendable) lambda, wrapping the method/function call to be
 *   stubbed.
 */
@Deprecated("Use whenever(mock) { methodCall() } instead")
fun <T> Stubber.wheneverBlocking(mock: T, methodCall: suspend T.() -> Unit) {
    whenever(mock, methodCall)
}

private fun doAnswerInternal(answer: Answer<*>): Stubber {
    return Mockito.doAnswer(answer.wrapAsCoroutineAwareAnswer())
}

private fun doAnswerInternal(answers: List<Answer<*>>): Stubber {
    return answers
        .map { it.wrapAsCoroutineAwareAnswer() }
        .let { wrappedAnswers ->
            val stubber = Mockito.doAnswer(wrappedAnswers.first())
            wrappedAnswers.drop(1).fold(stubber) { stubber, answer -> stubber.doAnswer(answer) }
        }
}
