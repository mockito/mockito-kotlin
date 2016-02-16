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

import org.mockito.Mockito
import org.mockito.verification.VerificationMode
import kotlin.reflect.KClass

inline fun <reified T : Any> mock() = Mockito.mock(T::class.java)
inline fun <reified T : Any> mock(defaultAnswer: Answer<Any>) = Mockito.mock(T::class.java, defaultAnswer)
fun <T : Any> spy(value: T) = Mockito.spy(value)

fun <T> whenever(methodCall: T) = Mockito.`when`(methodCall)
fun <T> verify(mock: T) = Mockito.verify(mock)
fun <T> verify(mock: T, mode: VerificationMode) = Mockito.verify(mock, mode)
fun <T> verifyNoMoreInteractions(mock: T) = Mockito.verifyNoMoreInteractions(mock)
fun <T> reset(mock: T) = Mockito.reset(mock)

fun inOrder(vararg value: Any) = Mockito.inOrder(*value)
fun never() = Mockito.never()
fun times(numInvocations: Int) = Mockito.times(numInvocations)
fun atLeast(numInvocations: Int) = Mockito.atLeast(numInvocations)
fun atLeastOnce() = Mockito.atLeastOnce()

fun doReturn(value: Any) = Mockito.doReturn(value)
fun doThrow(throwable: Throwable) = Mockito.doThrow(throwable)
fun <T> doAnswer(answer: Answer<T>) = Mockito.doAnswer(answer)
fun doCallRealMethod() = Mockito.doCallRealMethod()
fun doNothing() = Mockito.doNothing()

fun <T> Stubber.whenever(mock: T) = `when`(mock)

inline fun <reified T : Any> eq(value: T) = Mockito.eq(value) ?: createInstance<T>()
inline fun <reified T : Any> anyArray(): Array<T> = Mockito.any(Array<T>::class.java) ?: arrayOf()
inline fun <reified T : Any> any() = Mockito.any(T::class.java) ?: createInstance<T>()
inline fun <reified T : Any> isNull(): T? = Mockito.isNull(T::class.java)

inline fun <reified T : Any> argThat(noinline predicate: T.() -> Boolean) = argThat(T::class, predicate)

@Suppress("UNCHECKED_CAST")
fun <T : Any> argThat(kClass: KClass<T>, predicate: T.() -> Boolean)
        = Mockito.argThat<T> { it -> (it as T).predicate() } ?: createInstance(kClass)
