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

import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Stubber
import kotlin.reflect.KClass


fun <T> doAnswer(answer: (InvocationOnMock) -> T?): Stubber {
    return Mockito.doAnswer { answer(it) }!!
}

fun doCallRealMethod(): Stubber {
    return Mockito.doCallRealMethod()!!
}

fun doNothing(): Stubber {
    return Mockito.doNothing()!!
}

fun doReturn(value: Any?): Stubber {
    return Mockito.doReturn(value)!!
}

fun doReturn(toBeReturned: Any?, vararg toBeReturnedNext: Any?): Stubber {
    return Mockito.doReturn(
          toBeReturned,
          *toBeReturnedNext
    )!!
}

fun doThrow(toBeThrown: KClass<out Throwable>): Stubber {
    return Mockito.doThrow(toBeThrown.java)!!
}

fun doThrow(vararg toBeThrown: Throwable): Stubber {
    return Mockito.doThrow(*toBeThrown)!!
}

fun <T> Stubber.whenever(mock: T) = `when`(mock)