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

import org.mockito.InOrder
import org.mockito.verification.VerificationMode

interface KInOrder: InOrder {
    /**
     * Verifies certain suspending behavior <b>happened once</b> in order.
     *
     * Warning: Only one method call can be verified in the function.
     * Subsequent method calls are ignored!
     */
    fun <T> verifyBlocking(mock: T, f: suspend T.() -> Unit)

    /**
     * Verifies certain suspending behavior happened at least once / exact number of times / never in order.
     *
     * Warning: Only one method call can be verified in the function.
     * Subsequent method calls are ignored!
     */
    fun <T> verifyBlocking(mock: T, mode: VerificationMode, f: suspend T.() -> Unit)
}
