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

import org.mockito.MockedSingleton
import org.mockito.MockedStatic
import org.mockito.ScopedMock

/**
 * Wraps a [MockedSingleton] and an optional [MockedStatic] to provide combined mocking of both
 * instance and static methods on Kotlin `object` declarations.
 *
 * When a Kotlin `object` has `@JvmStatic` methods, Kotlin compiles them as real JVM static methods.
 * Calls from Kotlin bytecode use `invokestatic`, bypassing the singleton instance mock. This class
 * transparently combines both mocking mechanisms so that `@JvmStatic` methods are also intercepted.
 */
class MockedObject<T>(
    private val singleton: MockedSingleton<T>,
    private val static: MockedStatic<T>?,
) : ScopedMock {

    /** Returns `true` if static method mocking is active for this object. */
    val isMockingStatic: Boolean
        get() = static != null

    override fun isClosed(): Boolean = singleton.isClosed

    override fun close() {
        static?.close()
        singleton.close()
    }

    override fun closeOnDemand() {
        static?.closeOnDemand()
        singleton.closeOnDemand()
    }
}
