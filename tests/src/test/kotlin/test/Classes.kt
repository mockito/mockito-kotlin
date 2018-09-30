package test

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

interface Methods {

    fun intArray(i: IntArray)
    fun closed(c: Closed)
    fun closedArray(a: Array<Closed>)
    fun closedNullableArray(a: Array<Closed?>)
    fun closedCollection(c: Collection<Closed>)
    fun closedList(c: List<Closed>)
    fun closedStringMap(m: Map<Closed, String>)
    fun closedSet(s: Set<Closed>)
    fun string(s: String)
    fun int(i: Int)
    fun closedVararg(vararg c: Closed)
    fun throwableClass(t: ThrowableClass)
    fun nullableString(s: String?)

    fun stringResult(): String
    fun stringResult(s: String): String
    fun nullableStringResult(): String?
    fun builderMethod(): Methods
    fun varargBooleanResult(vararg values: String): Boolean

    fun nonDefaultReturnType(): ExtraInterface
}

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
