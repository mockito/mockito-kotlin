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

import java.lang.reflect.Method
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
fun <T : Any?> Any?.toKotlinType(clazz: KClass<*>): T {
    if (this == null) return null as T

    return if (clazz.isValue && this::class != clazz) {
        this.boxAsValueClass(clazz) as T
    } else {
        this as T
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : Any?> Any?.boxAsValueClass(clazz: KClass<*>): T {
    require(clazz.isValue) { "${clazz.qualifiedName} is not a value class." }

    val boxImpl = clazz.boxImpl()
    return boxImpl.invoke(null, this) as T
}

fun Any.unboxValueClass(): Any {
    val clazz = this::class
    require(clazz.isValue) { "${clazz.qualifiedName} is not a value class." }

    val unboxImpl =
        clazz.java.declaredMethods.single { it.name == "unbox-impl" && it.parameterCount == 0 }

    return unboxImpl.invoke(this)
}

fun KClass<*>.valueClassInnerClass(): KClass<*> {
    require(isValue) { "$qualifiedName is not a value class." }

    return boxImpl().parameters[0].type.kotlin
}

private fun KClass<*>.boxImpl(): Method =
    java.declaredMethods.single { it.name == "box-impl" && it.parameterCount == 1 }
