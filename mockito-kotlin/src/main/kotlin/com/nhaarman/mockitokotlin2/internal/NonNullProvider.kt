package com.nhaarman.mockitokotlin2.internal

import kotlin.reflect.KClass
import java.lang.reflect.Array as JavaArray

internal interface NonNullProvider {

    fun <T : Any> createInstance(kClass: KClass<T>): T

    companion object Factory {

        fun create(): NonNullProvider = NullCaster()
    }
}
