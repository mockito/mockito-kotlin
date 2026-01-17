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

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.mock

internal fun <T> safeRunBlocking(block: suspend () -> T): T {
    return try {
        Class.forName("kotlinx.coroutines.BuildersKt")
        runBlocking { block() }
    } catch (_: ClassNotFoundException) {
        bareBasicsRunBlocking(block)
    }
}

/**
 * This function is a bare basics replacement for the proper function runBlocking from the
 * kotlinx-coroutines-core library.
 *
 * Mockito-kotlin is compiled with kotlinx-coroutines-core library on the compileOnly classpath, so
 * the library is not marked as a transitive dependency of Mockito-kotlin.
 *
 * Mockito-kotlin API, like [org.mockito.kotlin.whenever] accepts suspending lambda arguments to
 * unify the handling of synchronous and suspending lambdas. Currently, Kotlin compiler does not yet
 * allow to define overload functions, one for a synchronous lambda and one for a suspending lambda:
 * it leads to resolution ambiguity issues. But once a synchronous lambda is passed into the
 * Mockito-kotlin API and then flagged as potentially suspending, there is no easy way to unflag the
 * suspending nature.
 *
 * If the project that applies Mockito-kotlin is not including the kotlinx-coroutines-core
 * dependency, simply because the project does not include any suspending/coroutines functionality,
 * Mockito-kotlin needs an alternative mean to execute the lambda parameter flagged as suspending.
 *
 * Therefor this function assumes that the lambda parameter [block], although marked suspending,
 * should be considered a synchronous lambda in the absence of any coroutines infrastructure as
 * delivered by the kotlinx-coroutines-core library. It takes a bare basics approach to invoke the
 * lambda on the current thread without any safeguards for (potential) suspension points in the
 * lambda.
 *
 * In future, when support for overloads just differing in suspending nature of the lambda argument
 * is provided by the Kotlin compiler, this rather ugly fix should be dropped in favor of
 * introducing proper overloads in the Mockito-kotlin API to cater for both synchronous and
 * suspending lambdas.
 */
private fun <T> bareBasicsRunBlocking(block: suspend () -> T): T {
    val completion = SimpleContinuation<T>()
    block.startCoroutine(completion)
    return completion.result!!.let { result ->
        result.exceptionOrNull()?.let { throw it }
        result.getOrNull()!!
    }
}

private class SimpleContinuation<T> : Continuation<T> {
    var result: Result<T>? = null

    override val context: CoroutineContext
        get() = mock()

    override fun resumeWith(result: Result<T>) {
        this.result = result
    }
}
