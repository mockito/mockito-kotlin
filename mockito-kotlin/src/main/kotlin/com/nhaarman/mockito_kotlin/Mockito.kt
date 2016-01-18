/*
 * Copyright 2016 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nhaarman.mockito_kotlin

import org.mockito.Mockito
import org.mockito.verification.VerificationMode

inline fun <reified T : Any> mock() = Mockito.mock(T::class.java)
fun <T : Any> spy(value: T) = Mockito.spy(value)

fun <T> whenever(methodCall: T) = Mockito.`when`(methodCall)
fun <T> verify(mock: T) = Mockito.verify(mock)
fun <T> verify(mock: T, mode: VerificationMode) = Mockito.verify(mock, mode)
fun <T> verifyNoMoreInteractions(mock: T) = Mockito.verifyNoMoreInteractions(mock)
fun <T> reset(mock: T) = Mockito.reset(mock)

fun inOrder(vararg value: Any) = Mockito.inOrder(*value)
fun never() = Mockito.never()

fun <T> eq(value: T) = Mockito.eq(value)

inline fun <reified T : Any> isNull(): T? = Mockito.isNull(T::class.java)
