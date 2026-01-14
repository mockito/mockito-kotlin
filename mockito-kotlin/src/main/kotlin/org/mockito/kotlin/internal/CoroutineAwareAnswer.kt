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
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.kotlinFunction
import org.mockito.internal.invocation.InterceptedInvocation
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.KInvocationOnMock
import org.mockito.stubbing.Answer

internal class CoroutineAwareAnswer<T> private constructor(private val delegate: Answer<*>) :
    Answer<T> {
    constructor(block: suspend (KInvocationOnMock) -> T?) : this(SuspendableAnswer(block))

    companion object {
        internal fun <T> Answer<T>.wrapAsCoroutineAwareAnswer(): CoroutineAwareAnswer<T> {
            return this as? CoroutineAwareAnswer<T> ?: CoroutineAwareAnswer(this)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun answer(invocation: InvocationOnMock): T? {
        val invokedKotlinFunction = invocation.invokedKotlinFunction
        val wrappedAnswer =
            if (invokedKotlinFunction == null || !invokedKotlinFunction.isSuspend) {
                delegate
            } else {
                (delegate as? SuspendableAnswer)
                    ?: SuspendableAnswer { invocation ->
                        val result = delegate.answer(invocation)
                        val returnType = invokedKotlinFunction.returnType.jvmErasure
                        result.toKotlinType(returnType)
                    }
            }

        return wrappedAnswer.answer(invocation) as T
    }

    private val InvocationOnMock.invokedKotlinFunction: KFunction<*>?
        get() =
            try {
                this.method.kotlinFunction
            } catch (_: Throwable) {
                // Failed to determine kotlin function by reflection. This can happen, for
                // instance, when the invocation is on a mocked function object (when
                // the Java method is of a type from the kotlin.jvm.functions package)
                // or when reflection fails due to interoperability issues with Java
                // classes.
                null
            }

    @Suppress("UNCHECKED_CAST")
    private class SuspendableAnswer(private val block: suspend (KInvocationOnMock) -> Any?) :
        Answer<Any?> {
        override fun answer(invocation: InvocationOnMock): Any? {
            val unboxNonPrimitiveValueClasses: suspend (KInvocationOnMock) -> Any? =
                { invocationOnMock ->
                    block.invoke(invocationOnMock)?.let { result: Any ->
                        if (result::class.isValue) {
                            result.unboxValueClass().let { unboxed ->
                                if (unboxed.isPrimitiveValue()) result else unboxed
                            }
                        } else {
                            result
                        }
                    }
                }

            // all suspend functions/lambdas has Continuation as the last argument.
            // InvocationOnMock does not see last argument
            val receiver = KInvocationOnMock(invocation)
            val rawInvocation = invocation as InterceptedInvocation
            val continuation = rawInvocation.rawArguments.last() as Continuation<Any?>

            // https://youtrack.jetbrains.com/issue/KT-33766#focus=Comments-27-3707299.0-0
            return unboxNonPrimitiveValueClasses.startCoroutineUninterceptedOrReturn(
                receiver,
                continuation,
            )
        }

        private fun Any.isPrimitiveValue(): Boolean =
            this is Number || this is Boolean || this is Char
    }
}
