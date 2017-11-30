package com.nhaarman.mockito_kotlin.createinstance

import kotlin.reflect.KClass
import java.lang.reflect.Array as JavaArray

interface NonNullProvider {

    fun <T : Any> createInstance(kClass: KClass<T>): T

    companion object Factory {

        fun create(): NonNullProvider = NullCaster()
    }
}
