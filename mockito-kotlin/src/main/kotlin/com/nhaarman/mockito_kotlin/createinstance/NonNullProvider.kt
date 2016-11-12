package com.nhaarman.mockito_kotlin.createinstance

import kotlin.reflect.KClass
import java.lang.reflect.Array as JavaArray

interface NonNullProvider {

    fun <T : Any> createInstance(kClass: KClass<T>): T
}

fun nonNullProvider(): NonNullProvider = NonNullProviderImpl(listOf(NullCaster(), InstanceCreator()))

internal class NonNullProviderImpl(
        private val nonNullProviders: List<NonNullProvider>
) : NonNullProvider {

    override fun <T : Any> createInstance(kClass: KClass<T>): T {
        return firstNonErroring(
                nonNullProviders.map { { it.createInstance(kClass) } }
        )
    }

    private fun <T> firstNonErroring(functions: List<() -> T>): T {
        var error: Throwable? = null
        functions.forEach { f ->
            try {
                return f()
            } catch(e: Throwable) {
                error = e
            }
        }
        throw error ?: IllegalStateException()
    }
}
