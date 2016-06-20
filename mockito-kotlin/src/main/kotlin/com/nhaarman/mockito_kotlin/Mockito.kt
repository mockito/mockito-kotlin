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

import org.mockito.MockSettings
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.mockito.verification.VerificationMode
import kotlin.reflect.KClass

fun after(millis: Long) = Mockito.after(millis)

inline fun <reified T : Any> any() = Mockito.any(T::class.java) ?: createInstance<T>()
inline fun <reified T : Any> anyArray(): Array<T> = Mockito.any(Array<T>::class.java) ?: arrayOf()
inline fun <reified T : Any> anyCollection(): Collection<T> = Mockito.anyCollectionOf(T::class.java)
inline fun <reified T : Any> anyList(): List<T> = Mockito.anyListOf(T::class.java)
inline fun <reified T : Any> anySet(): Set<T> = Mockito.anySetOf(T::class.java)
inline fun <reified K : Any, reified V : Any> anyMap(): Map<K, V> = Mockito.anyMapOf(K::class.java, V::class.java)
inline fun <reified T : Any> anyVararg() = Mockito.anyVararg<T>() ?: createInstance<T>()

inline fun <reified T : Any> argThat(noinline predicate: T.() -> Boolean) = Mockito.argThat<T> { it -> (it as T).predicate() } ?: createInstance<T>()

fun atLeast(numInvocations: Int) = Mockito.atLeast(numInvocations)
fun atLeastOnce() = Mockito.atLeastOnce()
fun atMost(maxNumberOfInvocations: Int) = Mockito.atMost(maxNumberOfInvocations)
fun calls(wantedNumberOfInvocations: Int) = Mockito.calls(wantedNumberOfInvocations)

fun <T> clearInvocations(vararg mocks: T) = Mockito.clearInvocations(*mocks)
fun description(description: String) = Mockito.description(description)

fun <T> doAnswer(answer: (InvocationOnMock) -> T?) = Mockito.doAnswer { answer(it) }

fun doCallRealMethod() = Mockito.doCallRealMethod()
fun doNothing() = Mockito.doNothing()
fun doReturn(value: Any) = Mockito.doReturn(value)
fun doReturn(toBeReturned: Any, vararg toBeReturnedNext: Any) = Mockito.doReturn(toBeReturned, *toBeReturnedNext)
fun doThrow(toBeThrown: KClass<out Throwable>) = Mockito.doThrow(toBeThrown.java)
fun doThrow(vararg toBeThrown: Throwable) = Mockito.doThrow(*toBeThrown)

inline fun <reified T : Any> eq(value: T) = Mockito.eq(value) ?: createInstance<T>()
fun ignoreStubs(vararg mocks: Any) = Mockito.ignoreStubs(*mocks)
fun inOrder(vararg mocks: Any) = Mockito.inOrder(*mocks)

inline fun <reified T : Any> isA() = Mockito.isA(T::class.java)
inline fun <reified T : Any> isNotNull() = Mockito.isNotNull(T::class.java)
inline fun <reified T : Any> isNull(): T? = Mockito.isNull(T::class.java)

inline fun <reified T : Any> mock() = Mockito.mock(T::class.java)
inline fun <reified T : Any> mock(defaultAnswer: Answer<Any>) = Mockito.mock(T::class.java, defaultAnswer)
inline fun <reified T : Any> mock(s: MockSettings) = Mockito.mock(T::class.java, s)
inline fun <reified T : Any> mock(s: String) = Mockito.mock(T::class.java, s)

fun mockingDetails(toInspect: Any) = Mockito.mockingDetails(toInspect)
fun never() = Mockito.never()
inline fun <reified T : Any> notNull() = Mockito.notNull(T::class.java)
fun only() = Mockito.only()
fun <T> refEq(value: T, vararg excludeFields: String) = Mockito.refEq(value, *excludeFields)

fun reset() = Mockito.reset<Any>()
fun <T> reset(vararg mocks: T) = Mockito.reset(*mocks)

fun <T> same(value: T) = Mockito.same(value)

inline fun <reified T : Any> spy() = Mockito.spy(T::class.java)
fun <T> spy(value: T) = Mockito.spy(value)

fun <T> stub(methodCall: T) = Mockito.stub(methodCall)
fun timeout(millis: Long) = Mockito.timeout(millis)
fun times(numInvocations: Int) = Mockito.times(numInvocations)
fun validateMockitoUsage() = Mockito.validateMockitoUsage()

fun <T> verify(mock: T) = Mockito.verify(mock)
fun <T> verify(mock: T, mode: VerificationMode) = Mockito.verify(mock, mode)
fun <T> verifyNoMoreInteractions(vararg mocks: T) = Mockito.verifyNoMoreInteractions(*mocks)
fun verifyZeroInteractions(vararg mocks: Any) = Mockito.verifyZeroInteractions(*mocks)

fun <T> whenever(methodCall: T) = Mockito.`when`(methodCall)
fun withSettings() = Mockito.withSettings()