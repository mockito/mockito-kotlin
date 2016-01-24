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

import org.mockito.Answers
import org.mockito.internal.creation.MockSettingsImpl
import org.mockito.internal.util.MockUtil
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.defaultType
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaType

/**
 * A collection of functions that tries to create an instance of
 * classes to avoid NPE's when using Mockito with Kotlin.
 */

inline fun <reified T> createArrayInstance() = arrayOf<T>()

inline fun <reified T : Any> createInstance() = createInstance(T::class)

fun <T : Any> createInstance(kClass: KClass<T>): T {
    return when {
        kClass.hasObjectInstance() -> kClass.objectInstance!!
        kClass.isMockable() -> kClass.java.uncheckedMock()
        kClass.isPrimitive() -> kClass.toDefaultPrimitiveValue()
        kClass.isEnum() -> kClass.java.enumConstants.first()
        kClass.isArray() -> kClass.toArrayInstance()
        else -> kClass.constructors.sortedBy { it.parameters.size }.first().newInstance()
    }
}

@Suppress("SENSELESS_COMPARISON")
private fun KClass<*>.hasObjectInstance() = objectInstance != null

private fun KClass<*>.isMockable() = !Modifier.isFinal(java.modifiers)
private fun KClass<*>.isEnum() = java.isEnum
private fun KClass<*>.isArray() = java.isArray
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

@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_UNIT_OR_ANY")
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

@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_UNIT_OR_ANY")
private fun <T : Any> KClass<T>.toArrayInstance(): T {
    return when (simpleName) {
        "ByteArray" -> byteArrayOf()
        "ShortArray" -> shortArrayOf()
        "IntArray" -> intArrayOf()
        "LongArray" -> longArrayOf()
        "DoubleArray" -> doubleArrayOf()
        "FloatArray" -> floatArrayOf()
        else -> throw UnsupportedOperationException("Cannot create a generic array for $simpleName. Use createArrayInstance() instead.")
    } as T
}

private fun <T : Any> KFunction<T>.newInstance(): T {
    isAccessible = true
    return callBy(parameters.toMap {
        it to it.type.createNullableInstance<T>()
    })
}

@Suppress("UNCHECKED_CAST")
private fun <T : Any> KType.createNullableInstance(): T? {
    if (isMarkedNullable) {
        return null
    }

    val javaType: Type = javaType
    return when (javaType) {
        is ParameterizedType -> (javaType.rawType as Class<T>).uncheckedMock()
        is Class<*> -> createInstance((javaType as Class<T>).kotlin)
        else -> null
    }
}

/**
 * Creates a mock instance of given class, without modifying or checking any internal Mockito state.
 */
@Suppress("UNCHECKED_CAST")
private fun <T> Class<T>.uncheckedMock(): T {
    val impl = MockSettingsImpl<T>().defaultAnswer(Answers.RETURNS_DEFAULTS) as MockSettingsImpl<*>
    val creationSettings = impl.confirm(this)
    return MockUtil().createMock(creationSettings) as T
}
