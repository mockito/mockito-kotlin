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
import org.mockito.Mockito.`when`
import org.mockito.kotlin.internal.KAnswer
import org.mockito.kotlin.internal.SuspendableAnswer
import org.mockito.stubbing.Answer
import org.mockito.stubbing.OngoingStubbing
import kotlin.reflect.KClass

/**
 * Enables stubbing java methods or kotlin functions. Use it when you want the mock to return
 * a particular value when particular method/function is being called.
 * The kotlin function call to be stubbed can be either a synchronous or suspendable function.
 *
 * Simply put: "**Whenever** the x function is being called **then** return y".
 *
 * Examples:
 *
 * ```kotlin
 *      whenever(mock.someFunction()) doReturn 10
 *
 *      //you can use flexible argument matchers, e.g:
 *      whenever(mock.someFunction(anyString())) doReturn 10
 * ```
 *
 * This function acts as an alias for [Mockito.when], as `when` is a keyword in kotlin and as such
 * the Mockito method can only be called by wrapping the method name in backticks, e.g. `` `when` ``.
 * To reduce the noise of backticks in your code, you can use this the function [whenever] instead.
 *
 * For more detail documentation, please refer to the Javadoc in the [Mockito] class.
 *
 * For stubbing Unit functions (or Java void methods) with throwables, see: [Mockito.doThrow].
 *
 * @param methodCall method call to be stubbed.
 * @return OngoingStubbing object used to stub fluently.
 *         ***Do not*** create a reference to this returned object.
 */
inline fun <reified T> whenever(methodCall: T): OngoingStubbing<T> {
    return `when`<T>(methodCall)!!
}

/**
 * Enables stubbing java methods or kotlin functions. Use it when you want the mock to return
 * a particular value when particular method/function is being called.
 * The lambda with the method/function call to be stubbed can be either a synchronous (regular)
 * or suspend lambda.
 *
 * **Warning**: Only the first method/function call in the lambda will be stubbed, other methods/functions calls are ignored!
 *
 * Simply put: "**Whenever** the x function is being called **then** return y".
 *
 * Examples:
 *
 * ```kotlin
 *      whenever { mock.someFunction() } doReturn 10
 *
 *      //you can use flexible argument matchers, e.g:
 *      whenever { mock.someFunction(anyString()) } doReturn 10
 * ```
 *
 * This function acts as an alias for [Mockito.when], as `when` is a keyword in kotlin and as such
 * the Mockito method can only be called by wrapping the method name in backticks, e.g. `` `when` ``.
 * To reduce the noise of backticks in your code, you can use this the function [whenever] instead.
 * Next to that, this function will take care of handling with suspend lambda, to ease the stubbing
 * of a suspendable function call.
 *
 * For more detailed documentation, please refer to the Javadoc in the [Mockito] class.
 *
 * For stubbing Unit functions (or Java void methods), see: [Mockito.doThrow].
 *
 * @param methodCall (regular or suspendable) lambda, wrapping the method/function call to be stubbed.
 * @return OngoingStubbing object used to stub fluently.
 *         ***Do not*** create a reference to this returned object.
 */
fun <T> whenever(methodCall: suspend CoroutineScope.() -> T): OngoingStubbing<T> {
    return runBlocking { `when`<T>(methodCall())!! }
}

/**
 * Enables stubbing a (suspendable) kotlin functions. Use it when you want the mock to return
 * a particular value when particular function is being called.
 * The lambda with the function call to be stubbed can be either a synchronous (regular)
 * or suspend lambda.
 *
 * This is a deprecated alias for [whenever]. Please use [whenever] instead.
 *
 * For more detailed documentation, please refer to [whenever].
 *
 * @param methodCall (regular or suspendable) lambda, wrapping the function call to be stubbed.
 * @return OngoingStubbing object used to stub fluently.
 *         ***Do not*** create a reference to this returned object.
 */
@Deprecated("Use whenever { mock.methodCall() } instead")
fun <T> wheneverBlocking(methodCall: suspend CoroutineScope.() -> T): OngoingStubbing<T> {
    return whenever(methodCall)
}

/**
 * Sets a return value to be returned when the method/function is called. E.g:
 *
 * ```kotlin
 *      whenever { mock.someMethod() } doReturn 10
 * ```
 *
 * This function acts as an alias for Mockito's [OngoingStubbing.thenReturn], adding the infix
 * functionality and extended type inference to it.
 *
 * @param value return value for the method/function invocation.
 * @return OngoingStubbing object used to stub fluently.
 *         ***Do not*** create a reference to this returned object.
 */
inline infix fun <reified T> OngoingStubbing<T>.doReturn(value: T): OngoingStubbing<T> {
    return thenReturn(value)
}

/**
 * Sets a return values to be returned when the method/function is called consecutively. E.g:
 *
 * ```kotlin
 *      whenever { mock.someMethod() }.doReturn(10, 20)
 * ```
 * You can specify [values] to be returned on consecutive invocations.
 * In that case the last value determines the behavior of further consecutive invocations.
 *
 * This function acts as an alias for Mockito's [OngoingStubbing.thenReturn], adding extended
 * type inference to it.
 *
 * @param value return value for the first method/function invocation.
 * @param values return values for the next method/function invocations.
 * @return OngoingStubbing object used to stub fluently.
 *         ***Do not*** create a reference to this returned object.
 */
inline fun <reified T> OngoingStubbing<T>.doReturn(value: T, vararg values: T): OngoingStubbing<T> {
    return doReturnConsecutively(value, *values)
}

/**
 * Sets a return values to be returned when the method/function is called consecutively. E.g:
 *
 * ```kotlin
 *      whenever { mock.someMethod() }.doReturnConsecutively(10, 20)
 * ```
 * You can specify [values] to be returned on consecutive invocations.
 * In that case the last value determines the behavior of further consecutive invocations.
 *
 * This function acts as an alias for Mockito's [OngoingStubbing.thenReturn], adding extended
 * type inference to it.
 *
 * @param value return value for the first method/function invocation.
 * @param values return values for the next method/function invocations.
 * @return OngoingStubbing object used to stub fluently.
 *         ***Do not*** create a reference to this returned object.
 */
inline fun <reified T> OngoingStubbing<T>.doReturnConsecutively(value: T, vararg values: T): OngoingStubbing<T> {
    return doReturnConsecutively(listOf(value, *values))
}

/**
 * Sets a return values to be returned when the method/function is called consecutively. E.g:
 *
 * ```kotlin
 *      whenever { mock.someMethod() } doReturnConsecutively listOf(10, 20)
 * ```
 *
 * The last value in [values] determines the behavior of further consecutive invocations.
 *
 * This function acts as an alias for Mockito's [OngoingStubbing.thenReturn], adding the infix
 * functionality and extended type inference to it.
 *
 * @param values return values for the consecutive method/function invocations.
 * @return OngoingStubbing object used to stub fluently.
 *         ***Do not*** create a reference to this returned object.
 */
inline infix fun <reified T> OngoingStubbing<T>.doReturnConsecutively(values: List<T>): OngoingStubbing<T> {
    return thenReturn(values.first(), *values.drop(1).toTypedArray())
}

/**
 * Sets Throwable instance to be thrown when the method/function is called. E.g:
 *
 * ```kotlin
 *      whenever { mock.someFunction() } doThrow RuntimeException()
 * ```
 *
 * This function acts as an alias for Mockito's [OngoingStubbing.thenThrow], adding the
 * infix functionality to it.
 *
 * @param throwable to be thrown on method/function invocations.
 * @return OngoingStubbing object used to stub fluently.
 *         ***Do not*** create a reference to this returned object.
 */
infix fun <T> OngoingStubbing<T>.doThrow(throwable: Throwable): OngoingStubbing<T> {
    return thenThrow(throwable)
}

/**
 * Sets Throwable instance(s) to be thrown when the method/function is called consecutively. E.g:
 *
 * ```kotlin
 *      whenever { mock.someFunction() }.doThrow(RuntimeException(), IOException())
 * ```
 *
 * You can specify [throwables] to be thrown for consecutive invocations.
 * In that case the last throwable determines the behavior of further consecutive invocations.
 *
 * This function acts as an alias for Mockito's [OngoingStubbing.thenThrow].
 *
 * @param throwable to be thrown on the first method/function invocation.
 * @param throwables to be thrown on the next method/function invocations.
 * @return OngoingStubbing object used to stub fluently.
 *         ***Do not*** create a reference to this returned object.
 */
fun <T> OngoingStubbing<T>.doThrow(throwable: Throwable, vararg throwables: Throwable): OngoingStubbing<T> {
    return thenThrow(throwable, *throwables)
}

/**
 * Sets a Throwable type to be thrown when the method/function is called. E.g:
 *
 * ```kotlin
 *      whenever { mock.someFunction() } doThrow IllegalArgumentException::class
 * ```
 *
 * This function acts as an alias for Mockito's [OngoingStubbing.thenThrow], accepting the
 * throwable type as [KClass] and adding the infix functionality to it.
 *
 * @param throwableType to be thrown on the method/function invocation.
 * @return OngoingStubbing object used to stub fluently.
 *         ***Do not*** create a reference to this returned object.
 */
infix fun <T> OngoingStubbing<T>.doThrow(throwableType: KClass<out Throwable>): OngoingStubbing<T> {
    return thenThrow(throwableType.java)
}

/**
 * Sets a Throwable type to be thrown when the method is called consecutively. E.g:
 *
 * ```kotlin
 *      whenever { mock.someFunction() }.doThrow(IllegalArgumentException::class, NullPointerException::class)
 * ```
 *
 * You can specify [throwableTypes] to be thrown for consecutive invocations.
 * In that case the last throwable type determines the behavior of further consecutive invocations.
 *
 * This function acts as an alias for Mockito's [OngoingStubbing.thenThrow], accepting the
 * throwable types as [KClass].
 *
 * @param throwableType to be thrown on the first method/function invocation.
 * @param throwableTypes to be thrown on the next method/function invocations.
 * @return OngoingStubbing object used to stub fluently.
 *         ***Do not*** create a reference to this returned object.
 */
fun <T> OngoingStubbing<T>.doThrow(throwableType: KClass<out Throwable>, vararg throwableTypes: KClass<out Throwable>): OngoingStubbing<T> {
    return thenThrow(throwableType.java, *throwableTypes.map { it.java }.toTypedArray())
}

/**
 * Sets a generic answer for when the method/function is called. E.g:
 *
 * ```kotlin
 *      val answer = Answer { "result" }
 *      whenever { mock.someFunction() } doAnswer answer
 * ```
 *
 * This function acts as an alias for Mockito's [OngoingStubbing.thenAnswer], adding the infix
 * functionality to it.
 *
 * @param answer to be applied on the method/function invocation.
 * @return OngoingStubbing object used to stub fluently.
 *         ***Do not*** create a reference to this returned object.
 */
infix fun <T> OngoingStubbing<T>.doAnswer(answer: Answer<*>): OngoingStubbing<T> {
    return thenAnswer(answer)
}

/**
 * Sets an answer for when the method/function is called, specified by a lambda. E.g:
 *
 * ```kotlin
 *      whenever { mock.someFunction() } doAnswer { "result" }
 * ```
 *
 * This function acts as an alias for Mockito's [OngoingStubbing.thenAnswer], adding the infix
 * functionality to it.
 *
 * @param answer to be applied on the method/function invocation.
 * @return OngoingStubbing object used to stub fluently.
 *         ***Do not*** create a reference to this returned object.
 */
infix fun <T> OngoingStubbing<T>.doAnswer(answer: (KInvocationOnMock) -> T?): OngoingStubbing<T> {
    return thenAnswer(KAnswer(answer))
}

/**
 * Sets an answer for when the suspendable function is called, specified by  a suspendable lambda. E.g:
 *
 * ```kotlin
 *      whenever { mock.someFunction() } doAnswer {
 *          delay(1)
 *          "result"
 *      }
 * ```
 *
 * This function acts as an alias for Mockito's [OngoingStubbing.thenAnswer], adding the infix
 * functionality to it.
 * Also, this function will wrap the answer lambda in a SuspendableAnswer object, to wire the
 * suspendable lambda properly into the Kotlin's coroutine context on invocation of the stubbed
 * suspendable function.
 *
 * @param answer to be applied on the suspendable function invocation.
 * @return OngoingStubbing object used to stub fluently.
 *         ***Do not*** create a reference to this returned object.
 */
infix fun <T> OngoingStubbing<T>.doSuspendableAnswer(answer: suspend (KInvocationOnMock) -> T?): OngoingStubbing<T> {
    return thenAnswer(SuspendableAnswer(answer))
}
