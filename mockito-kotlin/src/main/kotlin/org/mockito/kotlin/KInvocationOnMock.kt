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

import org.mockito.invocation.InvocationOnMock

class KInvocationOnMock(
    val invocationOnMock: InvocationOnMock
) : InvocationOnMock by invocationOnMock {

    operator fun <T> component1(): T = invocationOnMock.getArgument(0)
    operator fun <T> component2(): T = invocationOnMock.getArgument(1)
    operator fun <T> component3(): T = invocationOnMock.getArgument(2)
    operator fun <T> component4(): T = invocationOnMock.getArgument(3)
    operator fun <T> component5(): T = invocationOnMock.getArgument(4)

    /**
     * The first argument.
     * @throws IndexOutOfBoundsException if the argument is not available.
     */
    inline fun <reified T> first(): T = invocationOnMock.getArgument(0)

    /**
     * The second argument.
     * @throws IndexOutOfBoundsException if the argument is not available.
     */
    inline fun <reified T> second(): T = invocationOnMock.getArgument(1)

    /**
     * The third argument.
     * @throws IndexOutOfBoundsException if the argument is not available.
     */
    inline fun <reified T> third(): T = invocationOnMock.getArgument(2)

    /**
     * The fourth argument.
     * @throws IndexOutOfBoundsException if the argument is not available.
     */
    inline fun <reified T> fourth(): T = invocationOnMock.getArgument(3)

    /**
     * The fifth argument.
     * @throws IndexOutOfBoundsException if the argument is not available.
     */
    inline fun <reified T> fifth(): T = invocationOnMock.getArgument(4)

    /**
     * The last argument.
     * @throws IndexOutOfBoundsException if the argument is not available.
     */
    inline fun <reified T> last(): T {
        val size  = invocationOnMock.arguments.size
        require(size >= 1) { "The invocation was expected to have at least 1 argument but got none." }
        return invocationOnMock.getArgument(size - 1)
    }

    /**
     * The single argument.
     * @throws IndexOutOfBoundsException if the argument is not available.
     */
    inline fun <reified T> single(): T {
        val size  = invocationOnMock.arguments.size
        require(size == 1) { "The invocation was expected to have exactly 1 argument but got $size." }
        return first()
    }

    /**
     * The all arguments.
     */
    fun all(): List<Any> = invocationOnMock.arguments.toList()
}
