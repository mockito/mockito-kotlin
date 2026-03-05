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

import java.lang.reflect.Modifier
import kotlin.DeprecationLevel.ERROR
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.jvm.javaMethod
import org.mockito.MockSettings
import org.mockito.MockedConstruction
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.listeners.InvocationListener
import org.mockito.mock.SerializableMode
import org.mockito.quality.Strictness
import org.mockito.stubbing.Answer

/**
 * Creates a mock for [T].
 *
 * @param extraInterfaces Specifies extra interfaces the mock should implement.
 * @param name Specifies mock name. Naming mocks can be helpful for debugging - the name is used in
 *   all verification errors.
 * @param spiedInstance Specifies the instance to spy on. Makes sense only for spies/partial mocks.
 * @param defaultAnswer Specifies default answers to interactions.
 * @param serializable Configures the mock to be serializable.
 * @param serializableMode Configures the mock to be serializable with a specific serializable mode.
 * @param verboseLogging Enables real-time logging of method invocations on this mock.
 * @param invocationListeners Registers a listener for method invocations on this mock. The listener
 *   is notified every time a method on this mock is called.
 * @param stubOnly A stub-only mock does not record method invocations, thus saving memory but
 *   disallowing verification of invocations.
 * @param useConstructor Mockito attempts to use constructor when creating instance of the mock.
 * @param outerInstance Makes it possible to mock non-static inner classes in conjunction with
 *   [useConstructor].
 * @param lenient (DEPRECATED) Lenient mocks bypass "strict stubbing" validation.
 * @param strictness Specifies strictness level for the mock.
 */
inline fun <reified T : Any> mock(
    extraInterfaces: Array<out KClass<out Any>>? = null,
    name: String? = null,
    spiedInstance: Any? = null,
    defaultAnswer: Answer<Any>? = null,
    serializable: Boolean = false,
    serializableMode: SerializableMode? = null,
    verboseLogging: Boolean = false,
    invocationListeners: Array<InvocationListener>? = null,
    stubOnly: Boolean = false,
    useConstructor: UseConstructor? = null,
    outerInstance: Any? = null,
    lenient: Boolean = false,
    strictness: Strictness? = if (lenient) Strictness.LENIENT else null,
): T {
    return Mockito.mock(
        T::class.java,
        withSettings(
            extraInterfaces = extraInterfaces,
            name = name,
            spiedInstance = spiedInstance,
            defaultAnswer = defaultAnswer,
            serializable = serializable,
            serializableMode = serializableMode,
            verboseLogging = verboseLogging,
            invocationListeners = invocationListeners,
            stubOnly = stubOnly,
            useConstructor = useConstructor,
            outerInstance = outerInstance,
            strictness = strictness,
        ),
    )!!
}

/**
 * Creates a mock for [T], allowing for immediate stubbing.
 *
 * @param extraInterfaces Specifies extra interfaces the mock should implement.
 * @param name Specifies mock name. Naming mocks can be helpful for debugging - the name is used in
 *   all verification errors.
 * @param spiedInstance Specifies the instance to spy on. Makes sense only for spies/partial mocks.
 * @param defaultAnswer Specifies default answers to interactions.
 * @param serializable Configures the mock to be serializable.
 * @param serializableMode Configures the mock to be serializable with a specific serializable mode.
 * @param verboseLogging Enables real-time logging of method invocations on this mock.
 * @param invocationListeners Registers a listener for method invocations on this mock. The listener
 *   is notified every time a method on this mock is called.
 * @param stubOnly A stub-only mock does not record method invocations, thus saving memory but
 *   disallowing verification of invocations.
 * @param useConstructor Mockito attempts to use constructor when creating instance of the mock.
 * @param outerInstance Makes it possible to mock non-static inner classes in conjunction with
 *   [useConstructor].
 * @param lenient (DEPRECATED) Lenient mocks bypass "strict stubbing" validation.
 * @param strictness Specifies strictness level for the mock.
 */
inline fun <reified T : Any> mock(
    extraInterfaces: Array<out KClass<out Any>>? = null,
    name: String? = null,
    spiedInstance: Any? = null,
    defaultAnswer: Answer<Any>? = null,
    serializable: Boolean = false,
    serializableMode: SerializableMode? = null,
    verboseLogging: Boolean = false,
    invocationListeners: Array<InvocationListener>? = null,
    stubOnly: Boolean = false,
    useConstructor: UseConstructor? = null,
    outerInstance: Any? = null,
    lenient: Boolean = false,
    strictness: Strictness? = if (lenient) Strictness.LENIENT else null,
    stubbing: KStubbing<T>.(T) -> Unit,
): T {
    return Mockito.mock(
            T::class.java,
            withSettings(
                extraInterfaces = extraInterfaces,
                name = name,
                spiedInstance = spiedInstance,
                defaultAnswer = defaultAnswer,
                serializable = serializable,
                serializableMode = serializableMode,
                verboseLogging = verboseLogging,
                invocationListeners = invocationListeners,
                stubOnly = stubOnly,
                useConstructor = useConstructor,
                outerInstance = outerInstance,
                strictness = strictness,
            ),
        )
        .apply { KStubbing(this).stubbing(this) }!!
}

/**
 * Allows mock creation with additional mock settings. See [MockSettings].
 *
 * @param extraInterfaces Specifies extra interfaces the mock should implement.
 * @param name Specifies mock name. Naming mocks can be helpful for debugging - the name is used in
 *   all verification errors.
 * @param spiedInstance Specifies the instance to spy on. Makes sense only for spies/partial mocks.
 * @param defaultAnswer Specifies default answers to interactions.
 * @param serializable Configures the mock to be serializable.
 * @param serializableMode Configures the mock to be serializable with a specific serializable mode.
 * @param verboseLogging Enables real-time logging of method invocations on this mock.
 * @param invocationListeners Registers a listener for method invocations on this mock. The listener
 *   is notified every time a method on this mock is called.
 * @param stubOnly A stub-only mock does not record method invocations, thus saving memory but
 *   disallowing verification of invocations.
 * @param useConstructor Mockito attempts to use constructor when creating instance of the mock.
 * @param outerInstance Makes it possible to mock non-static inner classes in conjunction with
 *   [useConstructor].
 * @param lenient (DEPRECATED) Lenient mocks bypass "strict stubbing" validation.
 * @param strictness Specifies strictness level for the mock.
 */
fun withSettings(
    extraInterfaces: Array<out KClass<out Any>>? = null,
    name: String? = null,
    spiedInstance: Any? = null,
    defaultAnswer: Answer<Any>? = null,
    serializable: Boolean = false,
    serializableMode: SerializableMode? = null,
    verboseLogging: Boolean = false,
    invocationListeners: Array<InvocationListener>? = null,
    stubOnly: Boolean = false,
    useConstructor: UseConstructor? = null,
    outerInstance: Any? = null,
    lenient: Boolean = false,
    strictness: Strictness? = if (lenient) Strictness.LENIENT else null,
): MockSettings =
    Mockito.withSettings().apply {
        extraInterfaces?.let { extraInterfaces(*it.map { it.java }.toTypedArray()) }
        name?.let { name(it) }
        spiedInstance?.let { spiedInstance(it) }
        defaultAnswer?.let { defaultAnswer(it) }
        if (serializable) serializable()
        serializableMode?.let { serializable(it) }
        if (verboseLogging) verboseLogging()
        invocationListeners?.let { invocationListeners(*it) }
        if (stubOnly) stubOnly()
        useConstructor?.let { useConstructor(*it.args) }
        outerInstance?.let { outerInstance(it) }
        strictness?.let { strictness(it) }
    }

/**
 * Creates a thread-local mock for static methods on [T].
 *
 * @param defaultAnswer the default answer when invoking static methods.
 * @see Mockito.mockStatic
 */
inline fun <reified T> mockStatic(defaultAnswer: Answer<Any>? = null): MockedStatic<T> {
    return Mockito.mockStatic(T::class.java, withSettings(defaultAnswer = defaultAnswer))
}

/**
 * Creates a thread-local mock for constructions of [T].
 *
 * @see Mockito.mockConstruction
 */
inline fun <reified T> mockConstruction(): MockedConstruction<T> {
    return Mockito.mockConstruction(T::class.java)
}

/**
 * Creates a thread-local mock for an `object` or `companion object` singleton.
 *
 * NOTE: This returns a [MockedObject] which must be closed after test execution to prevent mocking
 * state from leaking to other test cases. You can do this with a `.use {}` block or by calling
 * [MockedObject.close] manually.
 *
 * Example usage:
 * ```
 * mockObject(MyObject).use {
 *     whenever(MyObject.foo()).thenReturn("hello")
 * }
 * ```
 *
 * or with a `companion object` with method `bar()`:
 * ```
 * mockObject(MyClass.Companion).use {
 *     whenever(MyClass.bar()).thenReturn("hello")
 * }
 * ```
 */
fun <T : Any> mockObject(instance: T): MockedObject<T> {
    if (instance::class.objectInstance == null && !instance::class.isCompanion) {
        throw MockitoKotlinException("$instance is not an object or companion object")
    }
    val singleton = Mockito.mockSingleton(instance)
    val static = createMockedStaticIfNeeded(instance)
    return MockedObject(singleton, static)
}

/**
 * Creates a thread-local mock for an `object` or `companion object` singleton, allowing for
 * immediate stubbing.
 *
 * NOTE: This returns a [MockedObject] which must be closed after test execution to prevent mocking
 * state from leaking to other test cases. You can do this with a `.use {}` block or by calling
 * [MockedObject.close] manually.
 *
 * Example usage:
 * ```
 * mockObject(MyObject) {
 *     on { foo() } doReturn "hello"
 * }.use { ... }
 * ```
 *
 * or with a `companion object` with method `bar()`:
 * ```
 * mockObject(MyClass.Companion) {
 *     on { bar() } doReturn "hello"
 * }.use { ... }
 * ```
 */
fun <T : Any> mockObject(instance: T, stubbing: KStubbing<T>.(T) -> Unit): MockedObject<T> {
    return mockObject(instance).apply { KStubbing(instance).stubbing(instance) }
}

/**
 * If [instance] is a top-level `object` (not a companion) with `@JvmStatic` methods, creates a
 * [MockedStatic] for its class. Returns `null` otherwise.
 */
private fun <T : Any> createMockedStaticIfNeeded(instance: T): MockedStatic<T>? {
    // Companion objects don't need mockStatic — calling Kotlin code always invokes the underlying
    // instance method even for @JvmStatic methods so mockSingleton is sufficient.
    if (instance::class.isCompanion) return null

    val javaClass = instance::class.java
    val hasStaticMethods =
        javaClass.declaredMethods.any { method ->
            Modifier.isStatic(method.modifiers) && !method.isSynthetic
        }

    if (!hasStaticMethods) return null

    return Mockito.mockStatic(javaClass) as MockedStatic<T>
}

/**
 * Creates a thread-local mock for constructions of [T].
 *
 * @param mockInitializer a callback to prepare the methods on a mock after its instantiation
 * @see Mockito.mockConstruction
 */
inline fun <reified T> mockConstruction(
    mockInitializer: MockedConstruction.MockInitializer<T>
): MockedConstruction<T> {
    return Mockito.mockConstruction(T::class.java, mockInitializer)
}

/**
 * Creates a thread-local mock for the static methods of the class that contains this top-level
 * extension function.
 *
 * Top-level Kotlin extension functions compile to static methods in a `*Kt` class. This helper
 * simplifies creating a [MockedStatic] for them.
 *
 * Usage:
 * ```
 * fun String.isHello(): Boolean = this == "Hello"
 *
 * mockExtensionFun(String::isHello).use {
 *     whenever("test".isHello()).thenReturn(true)
 * }
 * ```
 *
 * For overloaded extension functions, specify the type to disambiguate:
 * ```
 * fun String.isHello(): Boolean = this == "Hello"
 * fun String.isHello(mood: String): Boolean = this == "Hello" && mood == "happy"
 *
 * val ref: KFunction2<String, String, Boolean> = String::isHello
 * mockExtensionFun(ref).use {
 *     whenever("test".isHello("sad")).thenReturn(true)
 * }
 * ```
 *
 * Note: member extension functions (extension functions declared inside a class) do not need this
 * helper. They can be mocked by creating a regular [mock] of the containing class.
 *
 * @param function a reference to the top-level extension function to mock.
 * @see Mockito.mockStatic
 */
fun mockExtensionFun(function: KFunction<*>): MockedStatic<*> {
    requireNotNull(function.extensionReceiverParameter) {
        "Expected an extension function reference, but $function has no extension receiver."
    }
    val declaringClass =
        requireNotNull(function.javaMethod?.declaringClass) {
            "Could not determine declaring class for function $function. " +
                "Ensure this is a top-level extension function reference."
        }
    return Mockito.mockStatic(declaringClass)
}

class UseConstructor private constructor(val args: Array<Any>) {

    companion object {

        /** Invokes the parameterless constructor. */
        fun parameterless() = UseConstructor(emptyArray())

        /** Invokes a constructor with given arguments. */
        fun withArguments(vararg arguments: Any): UseConstructor {
            return UseConstructor(arguments.asList().toTypedArray())
        }
    }
}

@Deprecated(
    "Use mock() with optional arguments instead.",
    ReplaceWith("mock<T>(defaultAnswer = a)"),
    level = ERROR,
)
inline fun <reified T : Any> mock(a: Answer<Any>): T = mock(defaultAnswer = a)

@Deprecated(
    "Use mock() with optional arguments instead.",
    ReplaceWith("mock<T>(name = s)"),
    level = ERROR,
)
inline fun <reified T : Any> mock(s: String): T = mock(name = s)

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("Use mock() with optional arguments instead.", level = ERROR)
inline fun <reified T : Any> mock(s: MockSettings): T = Mockito.mock(T::class.java, s)!!
