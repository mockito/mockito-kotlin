/*
 * Copyright 2016 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nhaarman.mockito_kotlin

import org.mockito.Answers
import org.mockito.Mockito
import org.mockito.internal.creation.MockSettingsImpl
import org.mockito.internal.util.MockUtil
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.defaultType
import kotlin.reflect.jvm.javaType

inline fun <reified T : Any> anyArray(): Array<T> = Mockito.any(Array<T>::class.java) ?: arrayOf()
inline fun <reified T : Any> any() = any(T::class)
fun <T : Any> any(clzz: KClass<T>) = Mockito.any(clzz.java) ?: createInstance(clzz)

internal fun <T : Any> createInstance(kClass: KClass<T>): T {
    return createInstance<T>(kClass.defaultType) as T
}

@Suppress("UNCHECKED_CAST")
private fun <T : Any> createInstance(kType: KType): T? {
    if (kType.isMarkedNullable) {
        return null
    }

    val javaType: Type = kType.javaType
    if (javaType is ParameterizedType) {
        val rawType = javaType.rawType
        return createInstance(rawType as Class<T>)
    } else if (javaType is Class<*> && javaType.isPrimitive) {
        return defaultPrimitive(javaType as Class<T>)
    } else if (javaType is Class<*>) {
        return createInstance(javaType as Class<T>)
    } else {
        return null
    }
}

@Suppress("UNCHECKED_CAST")
private fun <T : Any> createInstance(jClass: Class<T>): T {
    if (!Modifier.isFinal(jClass.modifiers)) {
        return uncheckedMock(jClass)
    }

    if (jClass.isPrimitive || jClass == String::class.java) {
        return defaultPrimitive(jClass)
    }

    if (jClass.isArray) {
        return jClass.toArrayInstance()
    }

    val constructor = jClass.constructors
            .sortedBy { it.parameterTypes.size }
            .first()

    val params = constructor.parameterTypes.map { createInstance(it) }

    val result: Any = when (params.size) {
        0 -> constructor.newInstance()
        1 -> constructor.newInstance(params[0])
        2 -> constructor.newInstance(params[0], params[1])
        3 -> constructor.newInstance(params[0], params[1], params[2])
        4 -> constructor.newInstance(params[0], params[1], params[2], params[3])
        5 -> constructor.newInstance(params[0], params[1], params[2], params[3], params[4])
        6 -> constructor.newInstance(params[0], params[1], params[2], params[3], params[4], params[5])
        7 -> constructor.newInstance(params[0], params[1], params[2], params[3], params[4], params[5], params[6])
        8 -> constructor.newInstance(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7])
        9 -> constructor.newInstance(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8])
        10 -> constructor.newInstance(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9])
        else -> throw UnsupportedOperationException("Cannot create a new instance for ${jClass.canonicalName} with ${params.size} constructor parameters.")
    }

    return result as T
}

@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_UNIT_OR_ANY")
private fun <T : Any> defaultPrimitive(clzz: Class<T>): T {
    return when (clzz.canonicalName) {
        "byte" -> 0.toByte()
        "short" -> 0.toShort()
        "int" -> 0
        "double" -> 0.0
        "float" -> 0f
        "long" -> 0
        "java.lang.String" -> ""
        else -> throw UnsupportedOperationException("Cannot create default primitive for ${clzz.canonicalName}.")
    } as T
}

/**
 * Creates a mock instance of given class, without modifying or checking any internal Mockito state.
 */
@Suppress("UNCHECKED_CAST")
private fun <T> uncheckedMock(clzz: Class<T>): T {
    val impl = MockSettingsImpl<T>().defaultAnswer(Answers.RETURNS_DEFAULTS) as MockSettingsImpl<*>
    val creationSettings = impl.confirm(clzz)
    return MockUtil().createMock(creationSettings) as T
}

@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_UNIT_OR_ANY")
private fun <T : Any> Class<T>.toArrayInstance(): T {
    return when (simpleName) {
        "byte[]" -> byteArrayOf()
        "short[]" -> shortArrayOf()
        "int[]" -> intArrayOf()
        "long[]" -> longArrayOf()
        "double[]" -> doubleArrayOf()
        "float[]" -> floatArrayOf()
        else -> throw UnsupportedOperationException("Cannot create a generic array for $simpleName. Use anyArray() instead.")
    } as T
}
