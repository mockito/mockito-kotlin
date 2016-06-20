/*
 * The MIT License
 *
 * Copyright (c) 2016 Niek Haarman
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

package com.nhaarman.mockito_kotlin

import kotlin.reflect.KClass
import kotlin.reflect.defaultType

/**
 * A collection of functions that tries to create an instance of
 * classes to avoid NPE's when using Mockito with Kotlin.
 */

inline fun <reified T> createArrayInstance() = arrayOf<T>()

inline fun <reified T : Any> createInstance() = createInstanceOrNull<T>(T::class)

fun <T: Any> createInstanceOrNull(kClass: KClass<T>): T {
    when {
        kClass.isPrimitive() -> return kClass.toDefaultPrimitiveValue()
        else -> return newObjectInstance()
    }
}

//must not be inlined or the null cast will fail
@Suppress("UNCHECKED_CAST")
private fun <T> newObjectInstance(): T {
    return null as T
}

private fun KClass<*>.isPrimitive() =
        java.isPrimitive || !defaultType.isMarkedNullable && simpleName in arrayOf(
                "Boolean",
                "Byte",
                "Short",
                "Int",
                "Double",
                "Float",
                "Long",
                "String"
        )

@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
private fun <T : Any> KClass<T>.toDefaultPrimitiveValue(): T {
    return when (simpleName) {
        "Boolean" -> true
        "Byte" -> 0.toByte()
        "Short" -> 0.toShort()
        "Int" -> 0
        "Double" -> 0.0
        "Float" -> 0f
        "Long" -> 0
        "String" -> ""
        else -> throw UnsupportedOperationException("Cannot create default primitive for $simpleName.")
    } as T
}
