package com.nhaarman.mockito_kotlin

import org.mockito.BDDMockito

fun <T> given(methodCall: T): BDDMockito.BDDMyOngoingStubbing<T> = BDDMockito.given(methodCall)
fun <T> given(methodCall: () -> T) = given(methodCall())

fun <T> then(mock: T): BDDMockito.Then<T> = BDDMockito.then(mock)

infix fun <T> BDDMockito.BDDMyOngoingStubbing<T>.willAnswer(value: () -> T): BDDMockito.BDDMyOngoingStubbing<T> = willAnswer { value() }
infix fun <T> BDDMockito.BDDMyOngoingStubbing<T>.willReturn(value: () -> T): BDDMockito.BDDMyOngoingStubbing<T> = willReturn(value())
infix fun <T> BDDMockito.BDDMyOngoingStubbing<T>.willThrow(value: () -> Throwable): BDDMockito.BDDMyOngoingStubbing<T> = willThrow(value())

