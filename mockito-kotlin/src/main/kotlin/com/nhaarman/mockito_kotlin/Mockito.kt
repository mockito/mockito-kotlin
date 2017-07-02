/*
 * The MIT License
 *
 * Copyright (c) 2016 Niek Haarman
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

package com.nhaarman.mockito_kotlin

import com.nhaarman.mockito_kotlin.createinstance.createInstance
import org.mockito.*
import org.mockito.invocation.InvocationOnMock
import org.mockito.listeners.InvocationListener
import org.mockito.mock.SerializableMode
import org.mockito.stubbing.Answer
import org.mockito.stubbing.OngoingStubbing
import org.mockito.stubbing.Stubber
import org.mockito.verification.VerificationMode
import org.mockito.verification.VerificationWithTimeout
import kotlin.DeprecationLevel.WARNING
import kotlin.reflect.KClass

fun after(millis: Long) = Mockito.after(millis)

/** Matches any object, excluding nulls. */
inline fun <reified T : Any> any() = Mockito.any(T::class.java) ?: createInstance<T>()

/** Matches anything, including nulls. */
inline fun <reified T : Any> anyOrNull(): T = Mockito.any<T>() ?: createInstance<T>()

/** Matches any vararg object, including nulls. */
inline fun <reified T : Any> anyVararg(): T = Mockito.any<T>() ?: createInstance<T>()

/** Matches any array of type T. */
inline fun <reified T : Any?> anyArray(): Array<T> = Mockito.any(Array<T>::class.java) ?: arrayOf()

/**
 * Creates a custom argument matcher.
 * `null` values will never evaluate to `true`.
 *
 * @param predicate An extension function on [T] that returns `true` when a [T] matches the predicate.
 */
inline fun <reified T : Any> argThat(noinline predicate: T.() -> Boolean) = Mockito.argThat<T?> { arg -> arg?.predicate() ?: false } ?: createInstance(T::class)

/**
 * Creates a custom argument matcher.
 * `null` values will never evaluate to `true`.
 *
 * @param predicate An extension function on [T] that returns `true` when a [T] matches the predicate.
 */
inline fun <reified T : Any> argForWhich(noinline predicate: T.() -> Boolean) = argThat(predicate)

/**
 * Creates a custom argument matcher.
 * `null` values will never evaluate to `true`.
 *
 * @param predicate A function that returns `true` when given [T] matches the predicate.
 */
inline fun <reified T : Any> argWhere(noinline predicate: (T) -> Boolean) = argThat(predicate)

/**
 * For usage with verification only.
 *
 * For example:
 *  verify(myObject).doSomething(check { assertThat(it, is("Test")) })
 *
 * @param predicate A function that performs actions to verify an argument [T].
 */
inline fun <reified T : Any> check(noinline predicate: (T) -> Unit) = Mockito.argThat<T?> { arg ->
    if (arg == null) error("""The argument passed to the predicate was null.

If you are trying to verify an argument to be null, use `isNull()`.
If you are using `check` as part of a stubbing, use `argThat` or `argForWhich` instead.
""".trimIndent())
    predicate(arg)
    true
} ?: createInstance(T::class)

fun atLeast(numInvocations: Int): VerificationMode = Mockito.atLeast(numInvocations)!!
fun atLeastOnce(): VerificationMode = Mockito.atLeastOnce()!!
fun atMost(maxNumberOfInvocations: Int): VerificationMode = Mockito.atMost(maxNumberOfInvocations)!!
fun calls(wantedNumberOfInvocations: Int): VerificationMode = Mockito.calls(wantedNumberOfInvocations)!!

fun <T> clearInvocations(vararg mocks: T) = Mockito.clearInvocations(*mocks)
fun description(description: String): VerificationMode = Mockito.description(description)

fun <T> doAnswer(answer: (InvocationOnMock) -> T?): Stubber = Mockito.doAnswer { answer(it) }!!

fun doCallRealMethod(): Stubber = Mockito.doCallRealMethod()!!
fun doNothing(): Stubber = Mockito.doNothing()!!
fun doReturn(value: Any?): Stubber = Mockito.doReturn(value)!!
fun doReturn(toBeReturned: Any?, vararg toBeReturnedNext: Any?): Stubber = Mockito.doReturn(toBeReturned, *toBeReturnedNext)!!
fun doThrow(toBeThrown: KClass<out Throwable>): Stubber = Mockito.doThrow(toBeThrown.java)!!
fun doThrow(vararg toBeThrown: Throwable): Stubber = Mockito.doThrow(*toBeThrown)!!

fun <T> eq(value: T): T = Mockito.eq(value) ?: value
fun ignoreStubs(vararg mocks: Any): Array<out Any> = Mockito.ignoreStubs(*mocks)!!
fun inOrder(vararg mocks: Any): InOrder = Mockito.inOrder(*mocks)!!
fun inOrder(vararg mocks: Any, evaluation: InOrder.() -> Unit) = Mockito.inOrder(*mocks).evaluation()

inline fun <reified T : Any> isA(): T = Mockito.isA(T::class.java) ?: createInstance<T>()
fun <T : Any> isNotNull(): T? = Mockito.isNotNull()
fun <T : Any> isNull(): T? = Mockito.isNull()

inline fun <reified T : Any> mock(
      extraInterfaces: Array<KClass<out Any>>? = null,
      name: String? = null,
      spiedInstance: Any? = null,
      defaultAnswer: Answer<Any>? = null,
      serializable: Boolean = false,
      serializableMode: SerializableMode? = null,
      verboseLogging: Boolean = false,
      invocationListeners: Array<InvocationListener>? = null,
      stubOnly: Boolean = false,
      @Incubating useConstructor: Boolean = false,
      @Incubating outerInstance: Any? = null
): T = Mockito.mock(T::class.java, withSettings(
      extraInterfaces = extraInterfaces,
      name = name,
      spiedInstance = spiedInstance,
      defaultAnswer = defaultAnswer,
      serializable = serializable,
      serializableMode = serializableMode,
      verboseLogging = verboseLogging,
      invocationListeners = invocationListeners,
      stubOnly = stubOnly,
      useConstructor = useConstructor,
      outerInstance = outerInstance
))!!

inline fun <reified T : Any> mock(
      extraInterfaces: Array<KClass<out Any>>? = null,
      name: String? = null,
      spiedInstance: Any? = null,
      defaultAnswer: Answer<Any>? = null,
      serializable: Boolean = false,
      serializableMode: SerializableMode? = null,
      verboseLogging: Boolean = false,
      invocationListeners: Array<InvocationListener>? = null,
      stubOnly: Boolean = false,
      @Incubating useConstructor: Boolean = false,
      @Incubating outerInstance: Any? = null,
      stubbing: KStubbing<T>.(T) -> Unit
): T = Mockito.mock(T::class.java, withSettings(
      extraInterfaces = extraInterfaces,
      name = name,
      spiedInstance = spiedInstance,
      defaultAnswer = defaultAnswer,
      serializable = serializable,
      serializableMode = serializableMode,
      verboseLogging = verboseLogging,
      invocationListeners = invocationListeners,
      stubOnly = stubOnly,
      useConstructor = useConstructor,
      outerInstance = outerInstance
)).apply {
    KStubbing(this).stubbing(this)
}!!

inline fun <T : Any> T.stub(stubbing: KStubbing<T>.(T) -> Unit) = this.apply { KStubbing(this).stubbing(this) }

@Deprecated("Use mock() with optional arguments instead.", ReplaceWith("mock<T>(defaultAnswer = a)"), level = WARNING)
inline fun <reified T : Any> mock(a: Answer<Any>): T = mock(defaultAnswer = a)

@Deprecated("Use mock() with optional arguments instead.", ReplaceWith("mock<T>(name = s)"), level = WARNING)
inline fun <reified T : Any> mock(s: String): T = mock(name = s)

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("Use mock() with optional arguments instead.", level = WARNING)
inline fun <reified T : Any> mock(s: MockSettings): T = Mockito.mock(T::class.java, s)!!

class KStubbing<out T>(private val mock: T) {
    fun <R> on(methodCall: R) = Mockito.`when`(methodCall)

    fun <R : Any> onGeneric(methodCall: T.() -> R, c: KClass<R>): OngoingStubbing<R> {
        val r = try {
            mock.methodCall()
        } catch(e: NullPointerException) {
            // An NPE may be thrown by the Kotlin type system when the MockMethodInterceptor returns a
            // null value for a non-nullable generic type.
            // We catch this NPE to return a valid instance.
            // The Mockito state has already been modified at this point to reflect
            // the wanted changes.
            createInstance(c)
        }
        return Mockito.`when`(r)
    }

    inline fun <reified R : Any> onGeneric(noinline methodCall: T.() -> R): OngoingStubbing<R> {
        return onGeneric(methodCall, R::class)
    }

    fun <R> on(methodCall: T.() -> R): OngoingStubbing<R> {
        return try {
            Mockito.`when`(mock.methodCall())
        } catch(e: NullPointerException) {
            throw MockitoKotlinException("NullPointerException thrown when stubbing. If you are trying to stub a generic method, try `onGeneric` instead.", e)
        }
    }
}

infix fun <T> OngoingStubbing<T>.doReturn(t: T): OngoingStubbing<T> = thenReturn(t)
fun <T> OngoingStubbing<T>.doReturn(t: T, vararg ts: T): OngoingStubbing<T> = thenReturn(t, *ts)
inline infix fun <reified T> OngoingStubbing<T>.doReturn(ts: List<T>): OngoingStubbing<T> = thenReturn(ts[0], *ts.drop(1).toTypedArray())

infix fun <T> OngoingStubbing<T>.doThrow(t: Throwable): OngoingStubbing<T> = thenThrow(t)
fun <T> OngoingStubbing<T>.doThrow(t: Throwable, vararg ts: Throwable): OngoingStubbing<T> = thenThrow(t, *ts)
infix fun <T> OngoingStubbing<T>.doThrow(t: KClass<out Throwable>): OngoingStubbing<T> = thenThrow(t.java)
fun <T> OngoingStubbing<T>.doThrow(t: KClass<out Throwable>, vararg ts: KClass<out Throwable>): OngoingStubbing<T> = thenThrow(t.java, *ts.map { it.java }.toTypedArray())

infix fun <T> OngoingStubbing<T>.doAnswer(answer: (InvocationOnMock) -> T?): OngoingStubbing<T> = thenAnswer(answer)

fun mockingDetails(toInspect: Any): MockingDetails = Mockito.mockingDetails(toInspect)!!
fun never(): VerificationMode = Mockito.never()!!
fun <T : Any> notNull(): T? = Mockito.notNull()
fun only(): VerificationMode = Mockito.only()!!
fun <T> refEq(value: T, vararg excludeFields: String): T? = Mockito.refEq(value, *excludeFields)

fun <T> reset(vararg mocks: T) = Mockito.reset(*mocks)

fun <T> same(value: T): T = Mockito.same(value) ?: value

inline fun <reified T : Any> spy(): T = Mockito.spy(T::class.java)!!
inline fun <reified T : Any> spy(stubbing: KStubbing<T>.(T) -> Unit ): T = Mockito.spy(T::class.java)
        .apply { KStubbing(this).stubbing(this) }!!
fun <T> spy(value: T): T = Mockito.spy(value)!!
inline fun <reified T> spy(value: T, stubbing: KStubbing<T>.(T) -> Unit): T = spy(value)
        .apply { KStubbing(this).stubbing(this) }!!

fun timeout(millis: Long): VerificationWithTimeout = Mockito.timeout(millis)!!
fun times(numInvocations: Int): VerificationMode = Mockito.times(numInvocations)!!
fun validateMockitoUsage() = Mockito.validateMockitoUsage()

fun <T> verify(mock: T): T = Mockito.verify(mock)!!
fun <T> verify(mock: T, mode: VerificationMode): T = Mockito.verify(mock, mode)!!
fun <T> verifyNoMoreInteractions(vararg mocks: T) = Mockito.verifyNoMoreInteractions(*mocks)
fun verifyZeroInteractions(vararg mocks: Any) = Mockito.verifyZeroInteractions(*mocks)

fun <T> whenever(methodCall: T): OngoingStubbing<T> = Mockito.`when`(methodCall)!!

fun withSettings(
      extraInterfaces: Array<KClass<out Any>>? = null,
      name: String? = null,
      spiedInstance: Any? = null,
      defaultAnswer: Answer<Any>? = null,
      serializable: Boolean = false,
      serializableMode: SerializableMode? = null,
      verboseLogging: Boolean = false,
      invocationListeners: Array<InvocationListener>? = null,
      stubOnly: Boolean = false,
      @Incubating useConstructor: Boolean = false,
      @Incubating outerInstance: Any? = null
): MockSettings = Mockito.withSettings().apply {
    extraInterfaces?.let { extraInterfaces(*it.map { it.java }.toTypedArray()) }
    name?.let { name(it) }
    spiedInstance?.let { spiedInstance(it) }
    defaultAnswer?.let { defaultAnswer(it) }
    if (serializable) serializable()
    serializableMode?.let { serializable(it) }
    if (verboseLogging) verboseLogging()
    invocationListeners?.let { invocationListeners(*it) }
    if (stubOnly) stubOnly()
    if (useConstructor) useConstructor()
    outerInstance?.let { outerInstance(it) }
}
