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
import java.util.UUID

class CreateInstanceTest {

    @Test
    fun uuid() {
        val result = createInstance<UUID>()

        expect(result.leastSignificantBits).toBe(0)
        expect(result.mostSignificantBits).toBe(0)
    }

    @Test
    fun byte() {
        /* When */
        val result = createInstance<Byte>()

        /* Then */
        expect(result).toBe(0)
    }

    @Test
    fun short() {
        /* When */
        val result = createInstance<Short>()

        /* Then */
        expect(result).toBe(0)
    }

    @Test
    fun int() {
        /* When */
        val result = createInstance<Int>()

        /* Then */
        expect(result).toBe(0)
    }

    @Test
    fun long() {
        /* When */
        val result = createInstance<Long>()

        /* Then */
        expect(result).toBe(0)
    }

    @Test
    fun double() {
        /* When */
        val result = createInstance<Double>()

        /* Then */
        expect(result).toBeIn(-0.000001..0.000001)
    }

    @Test
    fun float() {
        /* When */
        val result = createInstance<Float>()

        /* Then */
        expect(result).toBeIn(-0.000001f..0.000001f)
    }

    @Test
    fun boolean() {
        /* When */
        val result = createInstance<Boolean>()

        /* Then */
        expect(result).toBe(true)
    }

    @Test
    fun string() {
        /* When */
        val result = createInstance<String>()

        /* Then */
        expect(result).toBeEqualTo("")
    }

    @Test
    fun byteArray() {
        /* When */
        val result = createInstance<ByteArray>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun shortArray() {
        /* When */
        val result = createInstance<ShortArray>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun intArray() {
        /* When */
        val result = createInstance<IntArray>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun longArray() {
        /* When */
        val result = createInstance<LongArray>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun doubleArray() {
        /* When */
        val result = createInstance<DoubleArray>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun floatArray() {
        /* When */
        val result = createInstance<FloatArray>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test(expected = UnsupportedOperationException::class)
    fun classArray_usingAny() {
        /* When */
        createInstance<Array<Open>>()
    }

    @Test
    fun closedClass() {
        /* When */
        val result = createInstance<ClosedClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun closedClass_withOpenParameter() {
        /* When */
        val result = createInstance<ClosedParameterizedClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun closedClass_withClosedParameter() {
        /* When */
        val result = createInstance<ClosedClosedParameterizedClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun singleParameterizedClass() {
        /* When */
        val result = createInstance<SingleParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun twoParameterizedClass() {
        /* When */
        val result = createInstance<TwoParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun threeParameterizedClass() {
        /* When */
        val result = createInstance<ThreeParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun fourParameterizedClass() {
        /* When */
        val result = createInstance<FourParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun fiveParameterizedClass() {
        /* When */
        val result = createInstance<FiveParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun sixParameterizedClass() {
        /* When */
        val result = createInstance<SixParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun sevenParameterizedClass() {
        /* When */
        val result = createInstance<SevenParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun nestedSingleParameterizedClass() {
        /* When */
        val result = createInstance<NestedSingleParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun nestedTwoParameterizedClass() {
        /* When */
        val result = createInstance<NestedTwoParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun nestedThreeParameterizedClass() {
        /* When */
        val result = createInstance<NestedThreeParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun nestedFourParameterizedClass() {
        /* When */
        val result = createInstance<NestedFourParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun nestedFiveParameterizedClass() {
        /* When */
        val result = createInstance<NestedFiveParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun nestedSixParameterizedClass() {
        /* When */
        val result = createInstance<NestedSixParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun nestedSevenParameterizedClass() {
        /* When */
        val result = createInstance<NestedSevenParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun parameterizedClass() {
        /* When */
        val result = createInstance<ParameterizedClass<ClosedClass>>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun nullableParameterClass() {
        /* When */
        val result = createInstance<NullableParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun stringList() {
        /* When */
        val result = createInstance<List<String>>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun enum() {
        /* When */
        val result = createInstance<MyEnum>()

        /* Then */
        expect(result).toBe(MyEnum.VALUE)
    }

    @Test
    fun unit() {
        /* When */
        val result = createInstance<Unit>()

        /* Then */
        expect(result).toBe(Unit)
    }

    @Test
    fun privateClass() {
        /* When */
        val result = createInstance<PrivateClass>()

        /* Then */
        expect(result).toNotBeNull()
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

    enum class MyEnum { VALUE, ANOTHER_VALUE }
}
