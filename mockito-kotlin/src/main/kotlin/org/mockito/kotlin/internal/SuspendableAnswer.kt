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

package org.mockito.kotlin.internal

import org.mockito.internal.invocation.InterceptedInvocation
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn

/**
 * This class properly wraps suspendable lambda into [Answer]
 */
@Suppress("UNCHECKED_CAST")
internal class SuspendableAnswer<T>(
    private val body: suspend (InvocationOnMock) -> T?
) : Answer<T> {
    override fun answer(invocation: InvocationOnMock?): T {
        //all suspend functions/lambdas has Continuation as the last argument.
        //InvocationOnMock does not see last argument
        val rawInvocation = invocation as InterceptedInvocation
        val continuation = rawInvocation.rawArguments.last() as Continuation<T?>

        // https://youtrack.jetbrains.com/issue/KT-33766#focus=Comments-27-3707299.0-0
        return body.startCoroutineUninterceptedOrReturn(invocation, continuation) as T
    }
}
