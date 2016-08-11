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

inline fun <reified T : Any> any() = Mockito.any(T::class.java) ?: createInstance<T>()
inline fun <reified T : Any> anyArray(): Array<T> = Mockito.any(Array<T>::class.java) ?: arrayOf()
inline fun <reified T : Any> anyCollection(): Collection<T> = Mockito.anyCollectionOf(T::class.java)
inline fun <reified T : Any> anyList(): List<T> = Mockito.anyListOf(T::class.java)
inline fun <reified T : Any> anySet(): Set<T> = Mockito.anySetOf(T::class.java)
inline fun <reified K : Any, reified V : Any> anyMap(): Map<K, V> = Mockito.anyMapOf(K::class.java, V::class.java)
inline fun <reified T : Any> anyVararg() = Mockito.anyVararg<T>() ?: createInstance<T>()

inline fun <reified T : Any> argThat(noinline predicate: T.() -> Boolean) = Mockito.argThat<T> { it -> (it as T).predicate() } ?: createInstance(T::class)

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
inline fun <reified T : Any> isNotNull(): T? = Mockito.isNotNull(T::class.java)
inline fun <reified T : Any> isNull(): T? = Mockito.isNull(T::class.java)

inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)!!
inline fun <reified T : Any> mock(defaultAnswer: Answer<Any>): T = Mockito.mock(T::class.java, defaultAnswer)!!
inline fun <reified T : Any> mock(s: MockSettings): T = Mockito.mock(T::class.java, s)!!
inline fun <reified T : Any> mock(s: String): T = Mockito.mock(T::class.java, s)!!

fun mockingDetails(toInspect: Any): MockingDetails = Mockito.mockingDetails(toInspect)!!
fun never(): VerificationMode = Mockito.never()!!
inline fun <reified T : Any> notNull(): T? = Mockito.notNull(T::class.java)
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
