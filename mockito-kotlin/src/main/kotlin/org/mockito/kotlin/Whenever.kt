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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.mockito.Mockito
import org.mockito.kotlin.internal.assertStubbingForNonSuspendableFunctionCall
import org.mockito.kotlin.internal.assertStubbingForSuspendableFunctionCall
import org.mockito.stubbing.OngoingStubbing

/**
 * Enables stubbing methods. Use it when you want the mock to return particular value when particular method is called.
 *
 * Alias for [org.mockito.Mockito.when].
 */
fun <T> whenever(methodCall: () -> T): OngoingStubbing<T> {
    return whenever(methodCall())
}

/**
 * Enables stubbing methods. Use it when you want the mock to return particular value when particular method is called.
 *
 * Alias for [org.mockito.Mockito.when].
 */
fun <T> whenever(methodCall: T): OngoingStubbing<T> {
    val ongoingStubbing: OngoingStubbing<T> = Mockito.`when`(methodCall)
    assertStubbingForNonSuspendableFunctionCall(ongoingStubbing)
    return ongoingStubbing
}

/**
 * Enables stubbing suspending methods. Use it when you want the mock to return particular value when particular suspending method is called.
 *
 * Warning: Only one method call can be stubbed in the function.
 * other method calls are ignored!
 */
fun <T> wheneverBlocking(methodCall: suspend CoroutineScope.() -> T): CoroutinesOngoingStubbing<T> {
    return CoroutinesOngoingStubbing(
        runBlocking {
            val ongoingStubbing: OngoingStubbing<T> = Mockito.`when`(methodCall())!!
            assertStubbingForSuspendableFunctionCall(ongoingStubbing)
            ongoingStubbing
        }
    )
}

