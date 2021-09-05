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

import org.mockito.internal.util.reflection.ReflectionMemberAccessor
import org.mockito.plugins.MemberAccessor
import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Method
import java.util.*

class KMemberAccessor(
    private val delegate: ReflectionMemberAccessor = ReflectionMemberAccessor()
) : MemberAccessor by delegate {

    override fun newInstance(constructor: Constructor<*>, vararg arguments: Any?): Any {
        val args = if (constructor.isSynthetic) {
            constructor.toSyntheticExecutableArguments(arguments)
        } else {
            arguments
        }

        return delegate.newInstance(constructor, *args)
    }

    override fun invoke(method: Method, target: Any, vararg arguments: Any?): Any? {
        val args = if (method.isSynthetic) {
            method.toSyntheticExecutableArguments(arguments)
        } else {
            arguments
        }

        return delegate.invoke(method, target, *args)
    }

    private fun Executable.toSyntheticExecutableArguments(
        arguments: Array<out Any?>
    ): Array<Any?> {
        val mask = BitSet()
        return (parameterTypes
            .take(parameterCount - 2)
            .mapIndexed { index, parameterType ->
                val argument = arguments.firstOrNull { arg ->
                    arg != null && parameterType.isInstance(arg)
                }

                mask.set(index, argument == null)

                argument
            } + mask.toInt() + null as Any?).toTypedArray()
    }

    private fun BitSet.toInt(): Int {
        var value = 0
        for (i in 0 until length()) {
            value += if (get(i)) {
                1 shl i
            } else{
                0
            }
        }
        return value
    }
}