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

package com.nhaarman.mockitokotlin2

import org.mockito.BDDMockito
import org.mockito.BDDMockito.BDDMyOngoingStubbing
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

/**
 * Alias for [BDDMockito.given].
 */
fun <T> given(methodCall: T): BDDMockito.BDDMyOngoingStubbing<T> {
    return BDDMockito.given(methodCall)
}

/**
 * Alias for [BDDMockito.given] with a lambda.
 */
fun <T> given(methodCall: () -> T): BDDMyOngoingStubbing<T> {
    return given(methodCall())
}

/**
 * Alias for [BDDMockito.then].
 */
fun <T> then(mock: T): BDDMockito.Then<T> {
    return BDDMockito.then(mock)
}

/**
 * Alias for [BDDMyOngoingStubbing.will]
 * */
infix fun <T> BDDMyOngoingStubbing<T>.will(value: Answer<T>): BDDMockito.BDDMyOngoingStubbing<T> {
    return will(value)
}

/**
 * Alias for [BBDMyOngoingStubbing.willAnswer], accepting a lambda.
 */
infix fun <T> BDDMyOngoingStubbing<T>.willAnswer(value: (InvocationOnMock) -> T?): BDDMockito.BDDMyOngoingStubbing<T> {
    return willAnswer { value(it) }
}

/**
 * Alias for [BBDMyOngoingStubbing.willReturn].
 */
infix fun <T> BDDMyOngoingStubbing<T>.willReturn(value: () -> T): BDDMockito.BDDMyOngoingStubbing<T> {
    return willReturn(value())
}

/**
 * Alias for [BBDMyOngoingStubbing.willThrow].
 */
infix fun <T> BDDMyOngoingStubbing<T>.willThrow(value: () -> Throwable): BDDMockito.BDDMyOngoingStubbing<T> {
    return willThrow(value())
}

