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
import org.mockito.internal.creation.bytebuddy.MockMethodInterceptor
import org.mockito.internal.util.MockUtil
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.defaultType
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.jvmName

/**
 * A collection of functions that tries to create an instance of
 * classes to avoid NPE's when using Mockito with Kotlin.
 */

inline fun <reified T> createArrayInstance() = arrayOf<T>()

inline fun <reified T : Any> createInstance() = createInstance(T::class)

@Suppress("UNCHECKED_CAST")
fun <T : Any> createInstance(kClass: KClass<T>): T {
    return MockitoKotlin.instanceCreator(kClass)?.invoke() as T? ?:
            when {
                kClass.hasObjectInstance() -> kClass.objectInstance!!
                kClass.isMockable() -> kClass.java.uncheckedMock()
                kClass.isPrimitive() -> kClass.toDefaultPrimitiveValue()
                kClass.isEnum() -> kClass.java.enumConstants.first()
                kClass.isArray() -> kClass.toArrayInstance()
        kClass.isClassObject() -> kClass.toClassObject()
                else -> kClass.easiestConstructor().newInstance()
            }
}

/**
 * Tries to find the easiest constructor which it can instantiate.
 */
private fun <T : Any> KClass<T>.easiestConstructor(): KFunction<T> {
    return constructors.firstOrDefault(
            {
                it.parameters.filter {
                    it.type.toString().toLowerCase().contains("array")
                }.isEmpty()
            },
            {
                constructors.sortedBy { it.parameters.size }.first()
            }
    )
}

private fun <T> Collection<T>.firstOrDefault(predicate: (T) -> Boolean, default: () -> T): T {
    return firstOrNull(predicate) ?: default()
}

@Suppress("SENSELESS_COMPARISON")
private fun KClass<*>.hasObjectInstance() = objectInstance != null

private fun KClass<*>.isMockable() = !Modifier.isFinal(java.modifiers)
private fun KClass<*>.isEnum() = java.isEnum
private fun KClass<*>.isArray() = java.isArray
private fun KClass<*>.isClassObject() = jvmName.equals("java.lang.Class")
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

@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
private fun <T : Any> KClass<T>.toArrayInstance(): T {
    return when (simpleName) {
        "ByteArray" -> byteArrayOf()
        "ShortArray" -> shortArrayOf()
        "IntArray" -> intArrayOf()
        "LongArray" -> longArrayOf()
        "DoubleArray" -> doubleArrayOf()
        "FloatArray" -> floatArrayOf()
        else -> throw UnsupportedOperationException("Cannot create a generic array for $simpleName. Use createArrayInstance() or anyArray() instead.")
    } as T
}

@Suppress("UNCHECKED_CAST")
private fun <T : Any> KClass<T>.toClassObject(): T {
    return Class.forName("java.lang.Object") as T
}

private fun <T : Any> KFunction<T>.newInstance(): T {
    try {
        isAccessible = true
        return callBy(parameters.associate {
            it to it.type.createNullableInstance<T>()
        })
    } catch(e: InvocationTargetException) {
        throw MockitoKotlinException(
                """

        Could not create an instance of class ${this.returnType}, because of an error with the following message:

            "${e.cause?.message}"

        Try registering an instance creator yourself, using MockitoKotlin.registerInstanceCreator<${this.returnType}> {...}.""",
                e.cause
        )
    }
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
    val impl = MockSettingsImpl<T>().defaultAnswer(Answers.RETURNS_DEFAULTS) as MockSettingsImpl<T>
    val creationSettings = impl.confirm(this)
    return MockUtil().createMock(creationSettings).apply {
        (this as MockMethodInterceptor.MockAccess).mockitoInterceptor = null
    }
}
