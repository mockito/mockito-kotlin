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

import kotlinx.coroutines.delay
import org.mockito.internal.invocation.InterceptedInvocation
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.KInvocationOnMock
import org.mockito.stubbing.Answer
import java.lang.reflect.Method
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.kotlinFunction

internal class CoroutineAwareAnswer<T> private constructor(private val delegate: Answer<T>) : Answer<T> {
    constructor(block: suspend (KInvocationOnMock) -> T?) :
          this(SuspendableAnswer<T>(block))

    companion object {
        internal fun <T> Answer<T>.wrapAsCoroutineAwareAnswer(): CoroutineAwareAnswer<T> {
            return this as? CoroutineAwareAnswer<T> ?: CoroutineAwareAnswer(this)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun answer(invocation: InvocationOnMock): T {
        val wrapped = if (invocation.isInvocationOnSuspendFunction ?: false) {
            delegate.wrapAsSuspendableAnswer()
        } else {
            delegate
        }
        return wrapped.answer(invocation) as T
    }

    private val InvocationOnMock.isInvocationOnSuspendFunction: Boolean?
        get() {
            return this.method.kotlinFunction?.isSuspend
        }

    private fun Answer<*>.wrapAsSuspendableAnswer(): Answer<out Any?> =
        (this as? SuspendableAnswer<*>) ?:
        SuspendableAnswer(
            { invocation ->
                suspendToEnforceProperValueBoxing()
                val result = this.answer(invocation)
                result.boxAsKotlinType(invocation)
            }
        )

    private fun Any.boxAsKotlinType(invocation: KInvocationOnMock): Any {
        val returnType: KType = invocation.method.kotlinFunction?.returnType ?: return this
        val returnCls: KClass<*> = returnType.jvmErasure

        if (this::class == returnCls) {
            return this
        }

        return if (returnCls.isValue) {
            returnCls.boxAsValueType(this)
        } else {
            this
        }
    }

    private fun KClass<*>.boxAsValueType(value: Any): Any {
        val boxImpl: Method =
            java
                .declaredMethods
                .single { it.name == "box-impl" && it.parameterCount == 1 }

        return boxImpl.invoke(null, value)
    }

    private suspend fun suspendToEnforceProperValueBoxing() {
        // delaying for 1 ms, forces a suspension to happen.
        // This (somehow) ensures that value class instances will be properly boxed when the
        // answer is yielded by the mock
        delay(1)
    }

    /**
     * This class properly wraps a suspendable lambda into a Mockito [Answer].
     */
    @Suppress("UNCHECKED_CAST")
    private class SuspendableAnswer<T>(
        private val body: suspend (KInvocationOnMock) -> T?
    ) : Answer<T> {
        override fun answer(invocation: InvocationOnMock?): T {
            //all suspend functions/lambdas has Continuation as the last argument.
            //InvocationOnMock does not see last argument
            val rawInvocation = invocation as InterceptedInvocation
            val continuation = rawInvocation.rawArguments.last() as Continuation<T?>

            // https://youtrack.jetbrains.com/issue/KT-33766#focus=Comments-27-3707299.0-0
            return body.startCoroutineUninterceptedOrReturn(KInvocationOnMock(invocation), continuation) as T
        }
    }
}
