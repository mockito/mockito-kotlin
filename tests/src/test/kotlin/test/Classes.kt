package test

import kotlinx.coroutines.delay

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

open class Open {
    open fun go(vararg arg: Any?) {
    }

    open fun modifiesContents(a: IntArray) {
        for (i in 0..a.size - 1) {
            a[i] = a[i] + 1
        }
    }

    open fun stringResult() = "Default"

    fun throwsNPE(): Any = throw NullPointerException("Test")
}

class Closed

interface SynchronousFunctions {
    fun closed(c: Closed)
    fun classClosed(c: Class<Closed>)
    fun closedArray(a: Array<Closed>)
    fun closedNullableArray(a: Array<Closed?>)
    fun closedCollection(c: Collection<Closed>)
    fun closedList(c: List<Closed>)
    fun closedStringMap(m: Map<Closed, String>)
    fun closedSet(s: Set<Closed>)
    fun string(s: String)
    fun boolean(b: Boolean)
    fun booleanArray(d: BooleanArray)
    fun byte(b: Byte)
    fun byteArray(b: ByteArray)
    fun char(c: Char)
    fun charArray(c: CharArray)
    fun short(s: Short)
    fun shortArray(s: ShortArray)
    fun int(i: Int)
    fun intArray(i: IntArray)
    fun long(l: Long)
    fun longArray(l: LongArray)
    fun float(f: Float)
    fun floatArray(f: FloatArray)
    fun double(d: Double)
    fun doubleArray(d: DoubleArray)
    fun closedVararg(vararg c: Closed)
    fun throwableClass(t: ThrowableClass)
    fun nullableString(s: String?)

    fun stringResult(): String
    fun stringResult(s: String): String
    fun nullableStringResult(): String?
    fun builderMethod(): SynchronousFunctions
    fun varargBooleanResult(vararg values: String): Boolean
    fun varargStringResult(vararg values: String): String
    fun stringArray(a: Array<String>)
    fun argAndVararg(s: String, vararg a: String)

    fun nonDefaultReturnType(): ExtraInterface

    fun valueClass(v: ValueClass)
    fun nullableValueClass(v: ValueClass?)
    fun nestedValueClass(v: NestedValueClass)
    fun valueClassResult(): ValueClass
    fun nullableValueClassResult(): ValueClass?
    fun nestedValueClassResult(): NestedValueClass
}

interface SuspendFunctions {
    suspend fun closed(c: Closed)
    suspend fun closedBooleanResult(c: Closed): Boolean
    suspend fun classClosedBooleanResult(c: Class<Closed>): Boolean
    suspend fun stringResult(): String
    suspend fun stringResult(s: String): String
    suspend fun stringResult(s1: String, s2: String): String
    suspend fun nullableStringResult(): String?
    suspend fun valueClassResult(): ValueClass
    suspend fun nullableValueClassResult(): ValueClass?
    suspend fun nestedValueClassResult(): NestedValueClass
    suspend fun builderMethod(): SuspendFunctions
}

@JvmInline
value class ValueClass(private val content: String)

@JvmInline
value class NestedValueClass(val value: ValueClass)

interface ExtraInterface

abstract class ThrowingConstructor {

    constructor() {
        error("Error in constructor")
    }
}

abstract class ThrowingConstructorWithArgument {

    constructor(s: String) {
        error("Error in constructor: $s")
    }
}

abstract class NonThrowingConstructorWithArgument {

    constructor() {
        error("Error in constructor")
    }

    @Suppress("UNUSED_PARAMETER")
    constructor(s: String)
}

interface GenericMethods<T> {
    fun genericMethod(): T
    fun nullableReturnType(): T?
}

class ThrowableClass(cause: Throwable) : Throwable(cause)

object SomeObject {
    @JvmStatic
    fun aStaticMethod() {}
}
