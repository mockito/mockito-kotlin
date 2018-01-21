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

import org.mockito.Mockito


/**
 * Creates a spy of the real object.
 * The spy calls <b>real</b> methods unless they are stubbed.
 */
inline fun <reified T : Any> spy(): T {
    return Mockito.spy(T::class.java)!!
}

/**
 * Creates a spy of the real object, allowing for immediate stubbing.
 * The spy calls <b>real</b> methods unless they are stubbed.
 */
inline fun <reified T : Any> spy(stubbing: KStubbing<T>.(T) -> Unit): T {
    return Mockito.spy(T::class.java)
        .apply { KStubbing(this).stubbing(this) }!!
}

/**
 * Creates a spy of the real object. The spy calls <b>real</b> methods unless they are stubbed.
 */
fun <T> spy(value: T): T {
    return Mockito.spy(value)!!
}

/**
 * Creates a spy of the real object, allowing for immediate stubbing.
 * The spy calls <b>real</b> methods unless they are stubbed.
 */
inline fun <reified T> spy(value: T, stubbing: KStubbing<T>.(T) -> Unit): T {
    return spy(value)
        .apply { KStubbing(this).stubbing(this) }!!
}