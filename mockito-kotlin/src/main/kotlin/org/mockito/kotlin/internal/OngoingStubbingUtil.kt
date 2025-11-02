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

import org.mockito.internal.configuration.plugins.Plugins
import org.mockito.internal.stubbing.OngoingStubbingImpl
import org.mockito.stubbing.OngoingStubbing
import java.lang.reflect.Method
import kotlin.reflect.jvm.kotlinFunction


fun <T> assertStubbingForNonSuspendableFunctionCall(ongoingStubbing: OngoingStubbing<T>) {
    val method = ongoingStubbing.lastInvocationMethod ?: return
    val isSuspend = method.kotlinFunction?.isSuspend ?: false

    if (isSuspend) {
        Plugins
            .getMockitoLogger()
            .warn(
                "For stubbing suspendable function '${method.declaringClass.simpleName}" +
                      ".${method.name}' better use 'wheneverBlocking()' instead, to get full " +
                      "support for stubbing suspend functions."
            )
    }
}

fun <T> assertStubbingForSuspendableFunctionCall(ongoingStubbing: OngoingStubbing<T>) {
    val method = ongoingStubbing.lastInvocationMethod ?: return
    val isSuspend = method.kotlinFunction?.isSuspend ?: false

    if (!isSuspend) {
        warn(
            "For stubbing non-suspendable function '${method.declaringClass.simpleName}" +
                  ".${method.name}' better use 'whenever()' instead."
        )
    }
}

private fun warn(message: String) {
    Plugins.getMockitoLogger().warn(message)
}

val <T> OngoingStubbing<T>.lastInvocationMethodIsSuspend: Boolean?
    get() {
        val method = this.lastInvocationMethod
        if (method == null) {
            warn("Failed to determine last invocation on a mock.")
        }
        return method?.kotlinFunction?.isSuspend
    }

private val <T> OngoingStubbing<T>.lastInvocationMethod: Method?
    get(): Method? =
        (this as? OngoingStubbingImpl<T>)
            ?.registeredInvocations
            ?.lastOrNull()
            ?.method
