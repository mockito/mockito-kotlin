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
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.internal.stubbing.answers.CallsRealMethods
import org.mockito.internal.stubbing.answers.Returns
import org.mockito.internal.stubbing.answers.ThrowsException
import org.mockito.internal.stubbing.answers.ThrowsExceptionForClassType
import org.mockito.kotlin.internal.CoroutineAwareAnswer
import org.mockito.kotlin.internal.CoroutineAwareAnswer.Companion.wrapAsCoroutineAwareAnswer
import org.mockito.kotlin.internal.KAnswer
import org.mockito.stubbing.Answer
import org.mockito.stubbing.OngoingStubbing

/**
 * Enables stubbing methods/function calls. In case of Kotlin function calls, these can be either
 * synchronous or suspendable function calls.
 *
 * Simply put: "**Whenever** the x function is being called **then** return y".
 *
 * Examples:
 * ```kotlin
 *      whenever(mock.someFunction()) doReturn 10
 *
 *      //you can use flexible argument matchers, e.g:
 *      whenever(mock.someFunction(any())) doReturn 10
 * ```
 *
 * This function is an alias for [Mockito.when]. So, for more detailed documentation, please refer
 * to the Javadoc of that method in the [Mockito] class. For stubbing Unit functions (or Java void
 * methods) with throwables, see: [Mockito.doThrow].
 *
 * @param methodCall method call to be stubbed.
 * @return OngoingStubbing object used to stub fluently. ***Do not*** create a reference to this
 *   returned object.
 */
fun <T> whenever(methodCall: T): OngoingStubbing<T> {
    return `when`<T>(methodCall)!!
}

/**
 * Enables stubbing methods/function calls. In case of Kotlin function calls, these can be either
 * synchronous or suspendable function calls.
 *
 * **Warning**: Only the first method/function call in the lambda will be stubbed, other
 * methods/functions calls are ignored!
 *
 * Simply put: "**Whenever** the x function is being called **then** return y".
 *
 * Examples:
 * ```kotlin
 *      whenever { mock.someFunction() } doReturn 10
 *
 *      //you can use flexible argument matchers, e.g:
 *      whenever { mock.someFunction(any()) } doReturn 10
 * ```
 *
 * This function is an alias for [Mockito.when]. So, for more detailed documentation, please refer
 * to the Javadoc of that method in the [Mockito] class. For stubbing Unit functions (or Java void
 * methods) with throwables, see: [Mockito.doThrow].
 *
 * @param methodCall (regular or suspendable) lambda, wrapping the method/function call to be
 *   stubbed.
 * @return OngoingStubbing object used to stub fluently. ***Do not*** create a reference to this
 *   returned object.
 */
fun <T> whenever(methodCall: suspend CoroutineScope.() -> T): OngoingStubbing<T> {
    return runBlocking { `when`<T>(methodCall())!! }
}

/**
 * Enables stubbing methods/function calls. In case of Kotlin function calls, these can be either
 * synchronous or suspendable function calls.
 *
 * This is a deprecated alias for [whenever]. Please use [whenever] instead.
 *
 * @param methodCall (regular or suspendable) lambda, wrapping the function call to be stubbed.
 * @return OngoingStubbing object used to stub fluently. ***Do not*** create a reference to this
 *   returned object.
 */
@Deprecated("Use whenever { mock.methodCall() } instead")
fun <T> wheneverBlocking(methodCall: suspend CoroutineScope.() -> T): OngoingStubbing<T> {
    return whenever(methodCall)
}

/**
 * Sets a value to be returned when the stubbed method/function is being called. E.g:
 * ```kotlin
 *      whenever { mock.someMethod() } doReturn 10
 * ```
 *
 * This function is an alias for Mockito's [OngoingStubbing.thenReturn].
 *
 * @param value return value for the method/function invocation.
 * @return OngoingStubbing object used to stub fluently. ***Do not*** create a reference to this
 *   returned object.
 */
infix fun <T> OngoingStubbing<T>.doReturn(value: T): OngoingStubbing<T> {
    return doAnswerInternal(Returns(value))
}

/**
 * Sets values to be returned when the stubbed method/function is being called consecutively. E.g:
 * ```kotlin
 *      whenever { mock.someMethod() }.doReturn(10, 20)
 * ```
 *
 * You can specify [values] to be returned on consecutive invocations. In that case the last value
 * determines the behavior of further consecutive invocations.
 *
 * This function is an alias for Mockito's [OngoingStubbing.thenReturn].
 *
 * @param value return value for the first method/function invocation.
 * @param values return values for the next method/function invocations.
 * @return OngoingStubbing object used to stub fluently. ***Do not*** create a reference to this
 *   returned object.
 */
fun <T> OngoingStubbing<T>.doReturn(value: T, vararg values: T): OngoingStubbing<T> {
    return doReturnConsecutively(value, *values)
}

/**
 * Sets values to be returned when the stubbed method/function is being called consecutively. E.g:
 * ```kotlin
 *      whenever { mock.someMethod() }.doReturnConsecutively(10, 20)
 * ```
 *
 * You can specify [values] to be returned on consecutive invocations. In that case the last value
 * determines the behavior of further consecutive invocations.
 *
 * This function is an alias for Mockito's [OngoingStubbing.thenReturn].
 *
 * @param value return value for the first method/function invocation.
 * @param values return values for the next method/function invocations.
 * @return OngoingStubbing object used to stub fluently. ***Do not*** create a reference to this
 *   returned object.
 */
fun <T> OngoingStubbing<T>.doReturnConsecutively(value: T, vararg values: T): OngoingStubbing<T> {
    return doReturnConsecutively(listOf(value, *values))
}

/**
 * Sets values to be returned when the stubbed method/function is being called consecutively. E.g:
 * ```kotlin
 *      whenever { mock.someMethod() } doReturnConsecutively listOf(10, 20)
 * ```
 *
 * The last value in [values] determines the behavior of further consecutive invocations.
 *
 * This function is an alias for Mockito's [OngoingStubbing.thenReturn].
 *
 * @param values return values for the consecutive method/function invocations.
 * @return OngoingStubbing object used to stub fluently. ***Do not*** create a reference to this
 *   returned object.
 */
infix fun <T> OngoingStubbing<T>.doReturnConsecutively(values: List<T>): OngoingStubbing<T> {
    return doAnswerInternal(values.map { Returns(it) })
}

/**
 * Sets a throwable to be thrown when the stubbed method/function is being called. E.g:
 * ```kotlin
 *      whenever { mock.someFunction() } doThrow RuntimeException()
 * ```
 *
 * This function is an alias for Mockito's [OngoingStubbing.thenThrow].
 *
 * @param throwable to be thrown on method/function invocations.
 * @return OngoingStubbing object used to stub fluently. ***Do not*** create a reference to this
 *   returned object.
 */
infix fun <T> OngoingStubbing<T>.doThrow(throwable: Throwable): OngoingStubbing<T> {
    return doAnswerInternal(ThrowsException(throwable))
}

/**
 * Sets throwables to be thrown when the stubbed method/function is being called consecutively. E.g:
 * ```kotlin
 *      whenever { mock.someFunction() }.doThrow(RuntimeException(), IOException())
 * ```
 *
 * You can specify [throwables] to be thrown for consecutive invocations. In that case the last
 * throwable determines the behavior of further consecutive invocations.
 *
 * This function is an alias for Mockito's [OngoingStubbing.thenThrow].
 *
 * @param throwable to be thrown on the first method/function invocation.
 * @param throwables to be thrown on the next method/function invocations.
 * @return OngoingStubbing object used to stub fluently. ***Do not*** create a reference to this
 *   returned object.
 */
fun <T> OngoingStubbing<T>.doThrow(
    throwable: Throwable,
    vararg throwables: Throwable,
): OngoingStubbing<T> {
    return doAnswerInternal(listOf(throwable, *throwables).map { ThrowsException(it) })
}

/**
 * Sets a throwable type to be thrown when the stubbed method/function is being called. E.g:
 * ```kotlin
 *      whenever { mock.someFunction() } doThrow IllegalArgumentException::class
 * ```
 *
 * This function is an alias for Mockito's [OngoingStubbing.thenThrow].
 *
 * @param throwableType to be thrown on the method/function invocation.
 * @return OngoingStubbing object used to stub fluently. ***Do not*** create a reference to this
 *   returned object.
 */
infix fun <T> OngoingStubbing<T>.doThrow(throwableType: KClass<out Throwable>): OngoingStubbing<T> {
    return doAnswerInternal(ThrowsExceptionForClassType(throwableType.java))
}

/**
 * Sets throwable types to be thrown when the stubbed method is called consecutively. E.g:
 * ```kotlin
 *      whenever { mock.someFunction() }.doThrow(IllegalArgumentException::class, NullPointerException::class)
 * ```
 *
 * You can specify [throwableTypes] to be thrown for consecutive invocations. In that case the last
 * throwable type determines the behavior of further consecutive invocations.
 *
 * This function is an alias for Mockito's [OngoingStubbing.thenThrow].
 *
 * @param throwableType to be thrown on the first method/function invocation.
 * @param throwableTypes to be thrown on the next method/function invocations.
 * @return OngoingStubbing object used to stub fluently. ***Do not*** create a reference to this
 *   returned object.
 */
fun <T> OngoingStubbing<T>.doThrow(
    throwableType: KClass<out Throwable>,
    vararg throwableTypes: KClass<out Throwable>,
): OngoingStubbing<T> {
    return doAnswerInternal(
        listOf(throwableType, *throwableTypes).map { ThrowsExceptionForClassType(it.java) }
    )
}

/**
 * Calls the real method of the spy/mock when the method is called. E.g:
 * ```kotlin
 *      whenever { mock.someFunction() }.doCallRealMethod()
 * ```
 *
 * This function is an alias for [callRealMethod].
 *
 * @return OngoingStubbing object used to stub fluently. ***Do not*** create a reference to this
 *   returned object.
 */
fun <T> OngoingStubbing<T>.doCallRealMethod(): OngoingStubbing<T> {
    return callRealMethod()
}

/**
 * Calls the real method of the spy/mock when the method is called. E.g:
 * ```kotlin
 *      whenever { mock.someFunction() }.callRealMethod()
 * ```
 *
 * This function is an alias for Mockito's [OngoingStubbing.thenCallRealMethod].
 *
 * @return OngoingStubbing object used to stub fluently. ***Do not*** create a reference to this
 *   returned object.
 */
fun <T> OngoingStubbing<T>.callRealMethod(): OngoingStubbing<T> {
    return doAnswerInternal(CallsRealMethods())
}

/**
 * Sets a generic answer to be applied when the stubbed method/function is being called. E.g:
 * ```kotlin
 *      val answer = Answer { "result" }
 *      whenever { mock.someFunction() } doAnswer answer
 * ```
 *
 * This function is an alias for Mockito's [OngoingStubbing.thenAnswer].
 *
 * @param answer to be applied on the method/function invocation.
 * @return OngoingStubbing object used to stub fluently. ***Do not*** create a reference to this
 *   returned object.
 */
infix fun <T> OngoingStubbing<T>.doAnswer(answer: Answer<*>): OngoingStubbing<T> {
    return doAnswerInternal(answer)
}

/**
 * Sets an answer to be applied when the stubbed method/function is being called, specified by a
 * lambda. E.g:
 * ```kotlin
 *      whenever { mock.someFunction() } doAnswer { "result" }
 * ```
 *
 * This function is an alias for Mockito's [OngoingStubbing.thenAnswer].
 *
 * @param answer to be applied on the method/function invocation.
 * @return OngoingStubbing object used to stub fluently. ***Do not*** create a reference to this
 *   returned object.
 */
infix fun <T> OngoingStubbing<T>.doAnswer(answer: (KInvocationOnMock) -> T?): OngoingStubbing<T> {
    return doAnswerInternal(KAnswer(answer))
}

/**
 * Sets an answer to be applied when the stubbed suspendable function is being called, specified by
 * a suspendable lambda. E.g:
 * ```kotlin
 *      whenever { mock.someFunction() } doSuspendableAnswer { "result" }
 * ```
 *
 * This function is an alias for Mockito's [OngoingStubbing.thenAnswer], but also taking extra steps
 * to wire the suspendable lambda answer properly into the Kotlin's coroutine context of the stubbed
 * suspendable function call.
 *
 * @param answer to be applied on the suspendable function invocation.
 * @return OngoingStubbing object used to stub fluently. ***Do not*** create a reference to this
 *   returned object.
 */
infix fun <T> OngoingStubbing<T>.doSuspendableAnswer(
    answer: suspend (KInvocationOnMock) -> T?
): OngoingStubbing<T> {
    return thenAnswer(CoroutineAwareAnswer(answer))
}

private fun <T> OngoingStubbing<T>.doAnswerInternal(answer: Answer<*>): OngoingStubbing<T> {
    return thenAnswer(answer.wrapAsCoroutineAwareAnswer())
}

private fun <T> OngoingStubbing<T>.doAnswerInternal(answers: List<Answer<*>>): OngoingStubbing<T> {
    return answers.fold(this) { ongoingStubbing, answer ->
        ongoingStubbing.doAnswerInternal(answer)
    }
}
