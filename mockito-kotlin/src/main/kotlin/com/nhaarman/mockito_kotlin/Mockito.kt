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

import org.mockito.InOrder
import org.mockito.MockSettings
import org.mockito.MockingDetails
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.mockito.stubbing.OngoingStubbing
import org.mockito.stubbing.Stubber
import org.mockito.verification.VerificationMode
import org.mockito.verification.VerificationWithTimeout
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

inline fun <reified T : Any> argThat(noinline predicate: T.() -> Boolean) = Mockito.argThat<T> { it -> (it as T).predicate() } ?: createInstance(T::class)
inline fun <reified T : Any> argForWhich(noinline predicate: T.() -> Boolean) = argThat(predicate)
inline fun <reified T : Any> check(noinline predicate: (T) -> Unit) = Mockito.argThat<T> { it -> predicate(it); true } ?: createInstance(T::class)

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

inline fun <reified T : Any> eq(value: T): T = Mockito.eq(value) ?: createInstance<T>()
fun ignoreStubs(vararg mocks: Any): Array<out Any> = Mockito.ignoreStubs(*mocks)!!
fun inOrder(vararg mocks: Any): InOrder = Mockito.inOrder(*mocks)!!

inline fun <reified T : Any> isA(): T? = Mockito.isA(T::class.java)
fun <T : Any> isNotNull(): T? = Mockito.isNotNull()
fun <T : Any> isNull(): T? = Mockito.isNull()

inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)!!
inline fun <reified T : Any> mock(defaultAnswer: Answer<Any>): T = Mockito.mock(T::class.java, defaultAnswer)!!
inline fun <reified T : Any> mock(s: MockSettings): T = Mockito.mock(T::class.java, s)!!
inline fun <reified T : Any> mock(s: String): T = Mockito.mock(T::class.java, s)!!

inline fun <reified T : Any> mock(stubbing: KStubbing<T>.(T) -> Unit): T {
    return mock<T>().apply {
        KStubbing(this).stubbing(this)
    }
}

class KStubbing<out T>(private val mock: T) {
    fun <R> on(methodCall: R) = Mockito.`when`(methodCall)
    fun <R> on(methodCall: T.() -> R) = Mockito.`when`(mock.methodCall())
}

infix fun <T> OngoingStubbing<T>.doReturn(t: T): OngoingStubbing<T> = thenReturn(t)
fun <T> OngoingStubbing<T>.doReturn(t: T, vararg ts: T): OngoingStubbing<T> = thenReturn(t, *ts)
inline infix fun <reified T> OngoingStubbing<T>.doReturn(ts: List<T>): OngoingStubbing<T> = thenReturn(ts[0], *ts.drop(1).toTypedArray())

infix fun <T> OngoingStubbing<T>.doThrow(t: Throwable): OngoingStubbing<T> = thenThrow(t)
fun <T> OngoingStubbing<T>.doThrow(t: Throwable, vararg ts: Throwable): OngoingStubbing<T> = thenThrow(t, *ts)
infix fun <T> OngoingStubbing<T>.doThrow(t: KClass<out Throwable>): OngoingStubbing<T> = thenThrow(t.java)
fun <T> OngoingStubbing<T>.doThrow(t: KClass<out Throwable>, vararg ts: KClass<out Throwable>): OngoingStubbing<T> = thenThrow(t.java, *ts.map { it.java }.toTypedArray())

fun mockingDetails(toInspect: Any): MockingDetails = Mockito.mockingDetails(toInspect)!!
fun never(): VerificationMode = Mockito.never()!!
fun <T : Any> notNull(): T? = Mockito.notNull()
fun only(): VerificationMode = Mockito.only()!!
fun <T> refEq(value: T, vararg excludeFields: String): T? = Mockito.refEq(value, *excludeFields)

fun <T> reset(vararg mocks: T) = Mockito.reset(*mocks)

fun <T> same(value: T): T? = Mockito.same(value)

inline fun <reified T : Any> spy(): T = Mockito.spy(T::class.java)!!
fun <T> spy(value: T): T = Mockito.spy(value)!!

fun timeout(millis: Long): VerificationWithTimeout = Mockito.timeout(millis)!!
fun times(numInvocations: Int): VerificationMode = Mockito.times(numInvocations)!!
fun validateMockitoUsage() = Mockito.validateMockitoUsage()

fun <T> verify(mock: T): T = Mockito.verify(mock)!!
fun <T> verify(mock: T, mode: VerificationMode): T = Mockito.verify(mock, mode)!!
fun <T> verifyNoMoreInteractions(vararg mocks: T) = Mockito.verifyNoMoreInteractions(*mocks)
fun verifyZeroInteractions(vararg mocks: Any) = Mockito.verifyZeroInteractions(*mocks)

fun <T> whenever(methodCall: T): OngoingStubbing<T> = Mockito.`when`(methodCall)!!
fun withSettings(): MockSettings = Mockito.withSettings()!!

@Deprecated("Use any() instead.", ReplaceWith("any()"))
inline fun <reified T : Any> anyCollection(): Collection<T> = any()

@Deprecated("Use any() instead.", ReplaceWith("any()"))
inline fun <reified T : Any> anyList(): List<T> = any()

@Deprecated("Use any() instead.", ReplaceWith("any()"))
inline fun <reified T : Any> anySet(): Set<T> = any()

@Deprecated("Use any() instead.", ReplaceWith("any()"))
inline fun <reified K : Any, reified V : Any> anyMap(): Map<K, V> = any()
