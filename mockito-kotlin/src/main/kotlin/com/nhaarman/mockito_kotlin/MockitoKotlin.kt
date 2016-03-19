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

import java.util.*
import kotlin.reflect.KClass

class MockitoKotlin {

    companion object {

        /**
         * Maps KClasses to functions that can create an instance of that KClass.
         */
        private val creators: MutableMap<KClass<*>, () -> Any> = HashMap()

        /**
         * Registers a function to be called when an instance of T is necessary.
         */
        inline fun <reified T : Any> registerInstanceCreator(noinline creator: () -> T) = registerInstanceCreator(T::class, creator)

        /**
         * Registers a function to be called when an instance of T is necessary.
         */
        fun <T : Any> registerInstanceCreator(kClass: KClass<T>, creator: () -> T) = creators.put(kClass, creator)

        /**
         * Unregisters an instance creator.
         */
        inline fun <reified T : Any> unregisterInstanceCreator() = unregisterInstanceCreator(T::class)

        /**
         * Unregisters an instance creator.
         */
        fun <T : Any> unregisterInstanceCreator(kClass: KClass<T>) = creators.remove(kClass)

        /**
         * Clears al instance creators.
         */
        fun resetInstanceCreators() = creators.clear()

        internal fun <T : Any> instanceCreator(kClass: KClass<T>): (() -> Any)? {
            return creators[kClass]
        }
    }
}
