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

import org.mockito.kotlin.internal.createInstance
import kotlinx.coroutines.runBlocking
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.exceptions.misusing.NotAMockException
import org.mockito.stubbing.OngoingStubbing
import org.mockito.stubbing.Stubber
import kotlin.reflect.KClass

/**
 * Stub a mock with given stubbing configuration.
 *
 * @param mock the mock to stub.
 * @param stubbing the stubbing configuration to apply to the mock.
 */
inline fun <T : Any> stubbing(
    mock: T,
    stubbing: KStubbing<T>.(T) -> Unit
) {
    KStubbing(mock).stubbing(mock)
}

/**
 * Stub a mock with given stubbing configuration.
 *
 * @receiver the mock to stub.
 * @param stubbing the stubbing configuration to apply to the mock.
 */
inline fun <T : Any> T.stub(stubbing: KStubbing<T>.(T) -> Unit): T {
    return apply { KStubbing(this).stubbing(this) }
}

class KStubbing<out T : Any>(val mock: T) {
    init {
        if (!mockingDetails(mock).isMock) throw NotAMockException("Stubbing target is not a mock!")
    }

    /**
     * Enables stubbing methods/function calls.
     * In case of Kotlin function calls, these can be either synchronous or suspendable function calls.
     *
     * Simply put: "**on a call to** the x function **then** return y".
     *
     * Examples:
     *
     * ```kotlin
     *      stubbing(mock) {
     *          on (mock.someFunction()) doReturn 10
     *      }
     * ```
     *
     * This function is an alias for [Mockito.when]. So, for more detailed documentation,
     * please refer to the Javadoc of that method in the [Mockito] class.
     * For stubbing Unit functions (or Java void methods) with throwables, see: [Mockito.doThrow].
     *
     * @param methodCall method call to be stubbed.
     * @return OngoingStubbing object used to stub fluently.
     *         ***Do not*** create a reference to this returned object.
     */
    inline fun <reified R> on(methodCall: R): OngoingStubbing<R> = whenever(methodCall)

    /**
     * Enables stubbing methods/function calls.
     * In case of Kotlin function calls, these can be either synchronous or suspendable function calls.
     *
     * Simply put: "**on a call to** the x function **then** return y".
     *
     * Examples:
     *
     * ```kotlin
     *      stubbing(mock) {
     *          on { mock.someFunction() } doReturn 10
     *      }
     * ```
     *
     * This function is an alias for [Mockito.when]. So, for more detailed documentation,
     * please refer to the Javadoc of that method in the [Mockito] class.
     * For stubbing Unit functions (or Java void methods) with throwables, see: [Mockito.doThrow].
     *
     * @param methodCall method call to be stubbed.
     * @return OngoingStubbing object used to stub fluently.
     *         ***Do not*** create a reference to this returned object.
     */
    fun <R> on(methodCall: suspend T.() -> R): OngoingStubbing<R> {
        return try {
            whenever { mock.methodCall() }
        } catch (e: NullPointerException) {
            throw MockitoKotlinException(
                "NullPointerException thrown when stubbing.\nThis may be due to two reasons:\n\t- The method you're trying to stub threw an NPE: look at the stack trace below;\n\t- You're trying to stub a generic method: try `onGeneric` instead.",
                e
            )
        }
    }

    /**
     * Enables stubbing methods/function calls with a generics return type [R].
     * In case of Kotlin function calls, these can be either synchronous or suspendable function calls.
     *
     * Simply put: "**on a call to** the x function **then** return y".
     *
     * Examples:
     *
     * ```kotlin
     *      interface GenericMethods<T> {
     *          fun genericMethod(): T
     *      }
     *
     *      mock<GenericMethods<Int>> {
     *          onGeneric({ genericMethod() }, Int::class) doReturn 10
     *      }
     * ```
     *
     * This function is an alias for [Mockito.when]. So, for more detailed documentation,
     * please refer to the Javadoc of that method in the [Mockito] class.
     * For stubbing Unit functions (or Java void methods) with throwables, see: [Mockito.doThrow].
     *
     * @param methodCall method call to be stubbed.
     * @param clazz the generics type.
     * @return OngoingStubbing object used to stub fluently.
     *         ***Do not*** create a reference to this returned object.
     */
    fun <R : Any> onGeneric(methodCall: suspend  T.() -> R?, clazz: KClass<R>): OngoingStubbing<R> {
        val r = try {
            runBlocking { mock.methodCall() }
        } catch (_: NullPointerException) {
            // An NPE may be thrown by the Kotlin type system when the MockMethodInterceptor returns a
            // null value for a non-nullable generic type.
            // We catch this NPE to return a valid instance.
            // The Mockito state has already been modified at this point to reflect
            // the wanted changes.
            createInstance(clazz)

        }
        return `when`<R?>(r)!!
    }

    /**
     * Enables stubbing methods/function calls with a generics return type [R].
     * In case of Kotlin function calls, these can be either synchronous or suspendable function calls.
     *
     * Simply put: "**on a call to** the x function **then** return y".
     *
     * Examples:
     *
     * ```kotlin
     *      interface GenericMethods<T> {
     *          fun genericMethod(): T
     *      }
     *
     *      mock<GenericMethods<Int>> {
     *          onGeneric { genericMethod() } doReturn 10
     *      }
     * ```
     *
     * This is a deprecated alias for [on]. Please use [on] instead.
     *
     * @param methodCall method call to be stubbed.
     * @return OngoingStubbing object used to stub fluently.
     *         ***Do not*** create a reference to this returned object.
     */
    @Deprecated("Use on { mock.methodCall() } instead")
    inline fun <reified R : Any> onGeneric(noinline methodCall: suspend T.() -> R?): OngoingStubbing<R> {
        return onGeneric(methodCall, R::class)
    }

    /**
     * Enables stubbing methods/function calls.
     * In case of Kotlin function calls, these can be either synchronous or suspendable function calls.
     *
     * Simply put: "**on a call to** the x function **then** return y".
     *
     * Examples:
     *
     * ```kotlin
     *      stubbing(mock) {
     *          onBlocking { mock.someFunction() } doReturn 10
     *      }
     * ```
     *
     * This is a deprecated alias for [on]. Please use [on] instead.
     *
     * @param methodCall (regular or suspendable) lambda, wrapping the function call to be stubbed.
     * @return OngoingStubbing object used to stub fluently.
     *         ***Do not*** create a reference to this returned object.
     */
    @Deprecated("Use on { methodCall } instead")
    fun <T : Any, R> KStubbing<T>.onBlocking(methodCall: suspend T.() -> R): OngoingStubbing<R> {
        return runBlocking { `when`<R>(mock.methodCall())!! }
    }

    /**
     * Stubs a method/function call in a reverse manner, as part of a mock being created.
     * You can reverse stub either synchronous as well as suspendable function calls.
     *
     * Reverse stubbing is especially useful when stubbing a void method (or Unit function) as
     * the regular approach of ongoing stubbing through [org.mockito.kotlin.whenever] leads to
     * problems in case of void methods (or Unit functions): the java compiler does not like void
     * methods inside brackets...
     *
     * Example:
     * ```kotlin
     *      mock<SynchronousFunctions> {
     *          doReturn("Test").on { stringResult() }
     *      }
     * ```
     * Warning: Only one method call can be stubbed in the function. Subsequent method calls are ignored!
     *
     * This function is an alias for [whenever]. Please use [whenever] instead.
     *
     * @param methodCall (regular or suspendable) lambda, wrapping the method/function call to be stubbed.
     */
    infix fun Stubber.on(methodCall: suspend T.() -> Unit) {
        this.whenever(mock) { methodCall() }
    }
}
