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

import com.nhaarman.expect.expect
import com.nhaarman.mockito_kotlin.createInstance
import org.junit.Test
import java.util.*
import kotlin.reflect.KClass

class CreateInstanceTest {

    @Test
    fun byte() {
        /* When */
        val result = createInstance<Byte>()

        /* Then */
        expect(result is Byte)
    }

    @Test
    fun short() {
        /* When */
        val result = createInstance<Short>()

        /* Then */
        expect(result is Short)
    }

    @Test
    fun int() {
        /* When */
        val result = createInstance<Int>()

        /* Then */
        expect(result is Int)
    }

    @Test
    fun long() {
        /* When */
        val result = createInstance<Long>()

        /* Then */
        expect(result is Long)
    }

    @Test
    fun double() {
        /* When */
        val result = createInstance<Double>()

        /* Then */
        expect(result is Double)
    }

    @Test
    fun float() {
        /* When */
        val result = createInstance<Float>()

        /* Then */
        expect(result is Float)
    }

    @Test
    fun boolean() {
        /* When */
        val result = createInstance<Boolean>()

        /* Then */
        expect(result is Boolean)
    }

    @Test
    fun string() {
        /* When */
        val result = createInstance<String>()

        /* Then */
        expect(result is String)
    }

    @Test
    fun byteArray() {
        /* When */
        val result = createInstance<ByteArray>()

        /* Then */
        expect(result is ByteArray)
    }

    @Test
    fun shortArray() {
        /* When */
        val result = createInstance<ShortArray>()

        /* Then */
        expect(result is ShortArray)
    }

    @Test
    fun intArray() {
        /* When */
        val result = createInstance<IntArray>()

        /* Then */
        expect(result is IntArray)
    }

    @Test
    fun longArray() {
        /* When */
        val result = createInstance<LongArray>()

        /* Then */
        expect(result is LongArray)
    }

    @Test
    fun doubleArray() {
        /* When */
        val result = createInstance<DoubleArray>()

        /* Then */
        expect(result is DoubleArray)
    }

    @Test
    fun floatArray() {
        /* When */
        val result = createInstance<FloatArray>()

        /* Then */
        expect(result is FloatArray)
    }

    @Test
    fun classArray_usingAny() {
        /* When */
        val result = createInstance<Array<Open>>()

        /* Then */
        expect(result is Array<Open>)
    }

    @Test
    fun closedClass() {
        /* When */
        val result = createInstance<ClosedClass>()

        /* Then */
        expect(result is ClosedClass)
    }

    @Test
    fun closedClass_withOpenParameter() {
        /* When */
        val result = createInstance<ClosedParameterizedClass>()

        /* Then */
        expect(result is ClosedParameterizedClass)
    }

    @Test
    fun closedClass_withClosedParameter() {
        /* When */
        val result = createInstance<ClosedClosedParameterizedClass>()

        /* Then */
        expect(result is ClosedClosedParameterizedClass)
    }

    @Test
    fun singleParameterizedClass() {
        /* When */
        val result = createInstance<SingleParameterClass>()

        /* Then */
        expect(result is SingleParameterClass)
    }

    @Test
    fun twoParameterizedClass() {
        /* When */
        val result = createInstance<TwoParameterClass>()

        /* Then */
        expect(result is TwoParameterClass)
    }

    @Test
    fun threeParameterizedClass() {
        /* When */
        val result = createInstance<ThreeParameterClass>()

        /* Then */
        expect(result is ThreeParameterClass)
    }

    @Test
    fun fourParameterizedClass() {
        /* When */
        val result = createInstance<FourParameterClass>()

        /* Then */
        expect(result is FourParameterClass)
    }

    @Test
    fun fiveParameterizedClass() {
        /* When */
        val result = createInstance<FiveParameterClass>()

        /* Then */
        expect(result is FiveParameterClass)
    }

    @Test
    fun sixParameterizedClass() {
        /* When */
        val result = createInstance<SixParameterClass>()

        /* Then */
        expect(result is SixParameterClass)
    }

    @Test
    fun sevenParameterizedClass() {
        /* When */
        val result = createInstance<SevenParameterClass>()

        /* Then */
        expect(result is SevenParameterClass)
    }

    @Test
    fun nestedSingleParameterizedClass() {
        /* When */
        val result = createInstance<NestedSingleParameterClass>()

        /* Then */
        expect(result is NestedSingleParameterClass)
    }

    @Test
    fun nestedTwoParameterizedClass() {
        /* When */
        val result = createInstance<NestedTwoParameterClass>()

        /* Then */
        expect(result is NestedTwoParameterClass)
    }

    @Test
    fun nestedThreeParameterizedClass() {
        /* When */
        val result = createInstance<NestedThreeParameterClass>()

        /* Then */
        expect(result is NestedThreeParameterClass)
    }

    @Test
    fun nestedFourParameterizedClass() {
        /* When */
        val result = createInstance<NestedFourParameterClass>()

        /* Then */
        expect(result is NestedFourParameterClass)
    }

    @Test
    fun nestedFiveParameterizedClass() {
        /* When */
        val result = createInstance<NestedFiveParameterClass>()

        /* Then */
        expect(result is NestedFiveParameterClass)
    }

    @Test
    fun nestedSixParameterizedClass() {
        /* When */
        val result = createInstance<NestedSixParameterClass>()

        /* Then */
        expect(result is NestedSixParameterClass)
    }

    @Test
    fun nestedSevenParameterizedClass() {
        /* When */
        val result = createInstance<NestedSevenParameterClass>()

        /* Then */
        expect(result is NestedSevenParameterClass)
    }

    @Test
    fun parameterizedClass() {
        /* When */
        val result = createInstance<ParameterizedClass<ClosedClass>>()

        /* Then */
        expect(result is ParameterizedClass<ClosedClass>)
    }

    @Test
    fun nullableParameterClass() {
        /* When */
        val result = createInstance<NullableParameterClass>()

        /* Then */
        expect(result is NullableParameterClass)
    }

    @Test
    fun stringList() {
        /* When */
        val result = createInstance<List<String>>()

        /* Then */
        expect(result is List<String>)
    }

    @Test
    fun enum() {
        /* When */
        val result = createInstance<MyEnum>()

        /* Then */
        expect(result is MyEnum)
    }

    @Test
    fun unit() {
        /* When */
        val result = createInstance<Unit>()

        /* Then */
        expect(result is Unit)
    }

    @Test
    fun privateClass() {
        /* When */
        val result = createInstance<PrivateClass>()

        /* Then */
        expect(result is PrivateClass)
    }

    @Test
    fun classObject() {
        /* When */
        val result = createInstance<Class<String>>()

        /* Then */
        expect(result is Class<String>)
    }

    @Test
    fun kClassObject() {
        /* When */
        val result = createInstance<KClass<String>>()

        /* Then */
        expect(result is KClass<String>)
    }

    @Test
    fun uuid() {
        /* When */
        val result = createInstance<UUID>()

        /* Then */
        expect(result is UUID)
    }

    @Test
    fun forbiddenConstructor() {
        /* When */
        val result = createInstance<ForbiddenConstructor>()

        /* Then */
        expect(result is ForbiddenConstructor)
    }

    private class PrivateClass private constructor(val data: String)

    class ClosedClass
    class ClosedParameterizedClass(val open: Open)
    class ClosedClosedParameterizedClass(val closed: ClosedParameterizedClass)

    class SingleParameterClass(val first: Byte)
    class TwoParameterClass(val first: Byte, val second: Short)
    class ThreeParameterClass(val first: Byte, val second: Short, val third: Int)
    class FourParameterClass(val first: Byte, val second: Short, val third: Int, val fourth: Double)
    class FiveParameterClass(val first: Byte, val second: Short, val third: Int, val fourth: Double, val fifth: Float)
    class SixParameterClass(val first: Byte, val second: Short, val third: Int, val fourth: Double, val fifth: Float, val sixth: Long)
    class SevenParameterClass(val first: Byte, val second: Short, val third: Int, val fourth: Double, val fifth: Float, val sixth: Long, val seventh: String)

    class NestedSingleParameterClass(val nested: SingleParameterClass)
    class NestedTwoParameterClass(val nested: TwoParameterClass)
    class NestedThreeParameterClass(val nested: ThreeParameterClass)
    class NestedFourParameterClass(val nested: FourParameterClass)
    class NestedFiveParameterClass(val nested: FiveParameterClass)
    class NestedSixParameterClass(val nested: SixParameterClass)
    class NestedSevenParameterClass(val nested: SevenParameterClass)

    class ParameterizedClass<T>(val t: T)
    class NullableParameterClass(val s: String?)

    class ForbiddenConstructor {

        constructor() {
            throw AssertionError("Forbidden.")
        }

        constructor(value: Int) {
        }
    }

    enum class MyEnum { VALUE, ANOTHER_VALUE }
}
