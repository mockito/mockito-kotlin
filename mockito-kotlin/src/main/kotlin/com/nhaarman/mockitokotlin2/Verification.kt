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

import com.nhaarman.mockitokotlin2.internal.createInstance
import kotlinx.coroutines.runBlocking
import org.mockito.InOrder
import org.mockito.Mockito
import org.mockito.verification.VerificationAfterDelay
import org.mockito.verification.VerificationMode
import org.mockito.verification.VerificationWithTimeout

/**
 * Verifies certain behavior <b>happened once</b>.
 *
 * Alias for [Mockito.verify].
 */
fun <T> verify(mock: T): T {
    return Mockito.verify(mock)!!
}

/**
 * Verifies certain suspending behavior <b>happened once</b>.
 *
 * Warning: Only one method call can be verified in the function.
 * Subsequent method calls are ignored!
 */
fun <T> verifyBlocking(mock: T, f: suspend T.() -> Unit) {
    val m = Mockito.verify(mock)
    runBlocking { m.f() }
}

/**
 * Verifies certain behavior happened at least once / exact number of times / never.
 *
 * Warning: Only one method call can be verified in the function.
 * Subsequent method calls are ignored!
 */
fun <T> verifyBlocking(mock: T, mode: VerificationMode, f: suspend T.() -> Unit) {
    val m = Mockito.verify(mock, mode)
    runBlocking { m.f() }
}
/**
 * Verifies certain behavior happened at least once / exact number of times / never.
 *
 * Alias for [Mockito.verify].
 */
fun <T> verify(mock: T, mode: VerificationMode): T {
    return Mockito.verify(mock, mode)!!
}

/**
 * Checks if any of given mocks has any unverified interaction.
 *
 * Alias for [Mockito.verifyNoMoreInteractions].
 */
fun <T> verifyNoMoreInteractions(vararg mocks: T) {
    Mockito.verifyNoMoreInteractions(*mocks)
}

/**
 * Verifies that no interactions happened on given mocks beyond the previously verified interactions.
 *
 * Alias for [Mockito.verifyZeroInteractions].
 */
fun verifyZeroInteractions(vararg mocks: Any) {
    Mockito.verifyZeroInteractions(*mocks)
}

/**
 * Allows verifying exact number of invocations.
 *
 * Alias for [Mockito.times].
 */
fun times(numInvocations: Int): VerificationMode {
    return Mockito.times(numInvocations)!!
}

/**
 * Allows at-least-x verification.
 *
 * Alias for [Mockito.atLeast].
 */
fun atLeast(numInvocations: Int): VerificationMode {
    return Mockito.atLeast(numInvocations)!!
}

/**
 * Allows at-least-once verification.
 *
 * Alias for [Mockito.atLeastOnce].
 */
fun atLeastOnce(): VerificationMode {
    return Mockito.atLeastOnce()!!
}

/**
 * Allows at-most-x verification.
 *
 * Alias for [Mockito.atMost].
 */
fun atMost(maxNumberOfInvocations: Int): VerificationMode {
    return Mockito.atMost(maxNumberOfInvocations)!!
}

/**
 * Allows non-greedy verification in order.
 *
 * Alias for [Mockito.calls].
 */
fun calls(wantedNumberOfInvocations: Int): VerificationMode {
    return Mockito.calls(wantedNumberOfInvocations)!!
}

/**
 * Alias for [times] with parameter `0`.
 */
fun never(): VerificationMode {
    return Mockito.never()!!
}

/**
 * Use this method in order to only clear invocations, when stubbing is non-trivial.
 *
 * Alias for [Mockito.clearInvocations].
 */
fun <T> clearInvocations(vararg mocks: T) {
    Mockito.clearInvocations(*mocks)
}

/**
 * Adds a description to be printed if verification fails.
 *
 * Alias for [Mockito.description].
 */
fun description(description: String): VerificationMode {
    return Mockito.description(description)
}

/**
 * Allows verifying over a given period. It causes a verify to wait for a specified period of time for a desired
 * interaction rather than failing immediately if has not already happened. May be useful for testing in concurrent
 * conditions.
 */
fun after(millis: Long): VerificationAfterDelay {
    return Mockito.after(millis)!!
}

/**
 * Allows verifying with timeout. It causes a verify to wait for a specified period of time for a desired
 * interaction rather than fails immediately if has not already happened. May be useful for testing in concurrent
 * conditions.
 */
fun timeout(millis: Long): VerificationWithTimeout {
    return Mockito.timeout(millis)!!
}

/**
 * Ignores stubbed methods of given mocks for the sake of verification.
 *
 * Alias for [Mockito.ignoreStubs].
 */
fun ignoreStubs(vararg mocks: Any): Array<out Any> {
    return Mockito.ignoreStubs(*mocks)!!
}

/**
 * Creates [InOrder] object that allows verifying mocks in order.
 *
 * Alias for [Mockito.inOrder].
 */
fun inOrder(vararg mocks: Any): InOrder {
    return Mockito.inOrder(*mocks)!!
}

/**
 * Creates [InOrder] object that allows verifying mocks in order.
 * Accepts a lambda to allow easy evaluation.
 *
 * Alias for [Mockito.inOrder].
 */
inline fun inOrder(
    vararg mocks: Any,
    evaluation: InOrder.() -> Unit
) {
    Mockito.inOrder(*mocks).evaluation()
}

/**
 * Allows [InOrder] verification for a single mocked instance:
 *
 * mock.inOrder {
 *    verify().foo()
 * }
 *
 */
inline fun <T> T.inOrder(block: InOrderOnType<T>.() -> Any) {
    block.invoke(InOrderOnType(this))
}

class InOrderOnType<T>(private val t: T) : InOrder by inOrder(t as Any) {

    fun verify(): T = verify(t)
}

/**
 * Allows checking if given method was the only one invoked.
 */
fun only(): VerificationMode {
    return Mockito.only()!!
}


/**
 * For usage with verification only.
 *
 * For example:
 *  verify(myObject).doSomething(check { assertThat(it, is("Test")) })
 *
 * @param predicate A function that performs actions to verify an argument [T].
 */
inline fun <reified T : Any> check(noinline predicate: (T) -> Unit): T {
    return Mockito.argThat { arg: T? ->
        if (arg == null) error(
              """The argument passed to the predicate was null.

If you are trying to verify an argument to be null, use `isNull()`.
If you are using `check` as part of a stubbing, use `argThat` or `argForWhich` instead.
""".trimIndent()
        )

        try {
            predicate(arg)
            true
        } catch (e: Error) {
            e.printStackTrace()
            false
        }
    } ?: createInstance(T::class)
}
