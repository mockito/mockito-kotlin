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

import org.mockito.Incubating
import org.mockito.MockSettings
import org.mockito.Mockito
import org.mockito.listeners.InvocationListener
import org.mockito.mock.SerializableMode
import org.mockito.stubbing.Answer
import kotlin.DeprecationLevel.ERROR
import kotlin.reflect.KClass

/**
 * Creates a mock for [T].
 *
 * @param extraInterfaces Specifies extra interfaces the mock should implement.
 * @param name Specifies mock name. Naming mocks can be helpful for debugging - the name is used in all verification errors.
 * @param spiedInstance Specifies the instance to spy on. Makes sense only for spies/partial mocks.
 * @param defaultAnswer Specifies default answers to interactions.
 * @param serializable Configures the mock to be serializable.
 * @param serializableMode Configures the mock to be serializable with a specific serializable mode.
 * @param verboseLogging Enables real-time logging of method invocations on this mock.
 * @param invocationListeners Registers a listener for method invocations on this mock. The listener is notified every time a method on this mock is called.
 * @param stubOnly A stub-only mock does not record method invocations, thus saving memory but disallowing verification of invocations.
 * @param useConstructor Mockito attempts to use constructor when creating instance of the mock.
 * @param outerInstance Makes it possible to mock non-static inner classes in conjunction with [useConstructor].
 * @param lenient Lenient mocks bypass "strict stubbing" validation.
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
    @Incubating useConstructor: UseConstructor? = null,
    @Incubating outerInstance: Any? = null,
    @Incubating lenient: Boolean = false
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
                lenient = lenient
          )
    )!!
}

/**
 * Creates a mock for [T], allowing for immediate stubbing.
 *
 * @param extraInterfaces Specifies extra interfaces the mock should implement.
 * @param name Specifies mock name. Naming mocks can be helpful for debugging - the name is used in all verification errors.
 * @param spiedInstance Specifies the instance to spy on. Makes sense only for spies/partial mocks.
 * @param defaultAnswer Specifies default answers to interactions.
 * @param serializable Configures the mock to be serializable.
 * @param serializableMode Configures the mock to be serializable with a specific serializable mode.
 * @param verboseLogging Enables real-time logging of method invocations on this mock.
 * @param invocationListeners Registers a listener for method invocations on this mock. The listener is notified every time a method on this mock is called.
 * @param stubOnly A stub-only mock does not record method invocations, thus saving memory but disallowing verification of invocations.
 * @param useConstructor Mockito attempts to use constructor when creating instance of the mock.
 * @param outerInstance Makes it possible to mock non-static inner classes in conjunction with [useConstructor].
 * @param lenient Lenient mocks bypass "strict stubbing" validation.
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
    @Incubating useConstructor: UseConstructor? = null,
    @Incubating outerInstance: Any? = null,
    @Incubating lenient: Boolean = false,
    stubbing: KStubbing<T>.(T) -> Unit
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
                lenient = lenient
          )
    ).apply { KStubbing(this).stubbing(this) }!!
}

/**
 * Allows mock creation with additional mock settings.
 * See [MockSettings].
 *
 * @param extraInterfaces Specifies extra interfaces the mock should implement.
 * @param name Specifies mock name. Naming mocks can be helpful for debugging - the name is used in all verification errors.
 * @param spiedInstance Specifies the instance to spy on. Makes sense only for spies/partial mocks.
 * @param defaultAnswer Specifies default answers to interactions.
 * @param serializable Configures the mock to be serializable.
 * @param serializableMode Configures the mock to be serializable with a specific serializable mode.
 * @param verboseLogging Enables real-time logging of method invocations on this mock.
 * @param invocationListeners Registers a listener for method invocations on this mock. The listener is notified every time a method on this mock is called.
 * @param stubOnly A stub-only mock does not record method invocations, thus saving memory but disallowing verification of invocations.
 * @param useConstructor Mockito attempts to use constructor when creating instance of the mock.
 * @param outerInstance Makes it possible to mock non-static inner classes in conjunction with [useConstructor].
 * @param lenient Lenient mocks bypass "strict stubbing" validation.
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
    @Incubating useConstructor: UseConstructor? = null,
    @Incubating outerInstance: Any? = null,
    @Incubating lenient: Boolean = false
): MockSettings = Mockito.withSettings().apply {
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
    if (lenient) lenient()
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
      level = ERROR
)
inline fun <reified T : Any> mock(a: Answer<Any>): T = mock(defaultAnswer = a)

@Deprecated(
      "Use mock() with optional arguments instead.",
      ReplaceWith("mock<T>(name = s)"),
      level = ERROR
)
inline fun <reified T : Any> mock(s: String): T = mock(name = s)

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("Use mock() with optional arguments instead.", level = ERROR)
inline fun <reified T : Any> mock(s: MockSettings): T = Mockito.mock(T::class.java, s)!!