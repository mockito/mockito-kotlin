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
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyArray
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

/*
 * Copyright 2016 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class AnyTest {

    private lateinit var doAnswer: Fake

    @Before
    fun setup() {
        /* Create an 'any' Mockito state */
        doAnswer = Mockito.doAnswer { }.`when`(mock())
    }

    @After
    fun tearDown() {
        /* Close `any` Mockito state */
        doAnswer.go(0)
    }

    @Test
    fun anyByte() {
        /* When */
        val result = any<Byte>()

        /* Then */
        expect(result).toBe(0)
    }

    @Test
    fun anyShort() {
        /* When */
        val result = any<Short>()

        /* Then */
        expect(result).toBe(0)
    }

    @Test
    fun anyInt() {
        /* When */
        val result = any<Int>()

        /* Then */
        expect(result).toBe(0)
    }

    @Test
    fun anyLong() {
        /* When */
        val result = any<Long>()

        /* Then */
        expect(result).toBe(0)
    }

    @Test
    fun anyDouble() {
        /* When */
        val result = any<Double>()

        /* Then */
        expect(result).toBeIn(-0.000001..0.000001)
    }

    @Test
    fun anyFloat() {
        /* When */
        val result = any<Float>()

        /* Then */
        expect(result).toBeIn(-0.000001f..0.000001f)
    }

    @Test
    fun anyString() {
        /* When */
        val result = any<String>()

        /* Then */
        expect(result).toBeEqualTo("")
    }

    @Test
    fun anyByteArray() {
        /* When */
        val result = any<ByteArray>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyShortArray() {
        /* When */
        val result = any<ShortArray>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyIntArray() {
        /* When */
        val result = any<IntArray>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyLongArray() {
        /* When */
        val result = any<LongArray>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyDoubleArray() {
        /* When */
        val result = any<DoubleArray>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyFloatArray() {
        /* When */
        val result = any<FloatArray>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test(expected = UnsupportedOperationException::class)
    fun anyClassArray_usingAny() {
        /* When */
        any<Array<Fake>>()
    }

    @Test
    fun anyClassArray_usingAnyArray() {
        /* When */
        val result = anyArray<Array<Fake>>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyClosedClass() {
        /* When */
        val result = any<ClosedClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyClosedClass_withOpenParameter() {
        /* When */
        val result = any<ClosedParameterizedClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyClosedClass_withClosedParameter() {
        /* When */
        val result = any<ClosedClosedParameterizedClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anySingleParameterizedClass() {
        /* When */
        val result = any<SingleParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyTwoParameterizedClass() {
        /* When */
        val result = any<TwoParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyThreeParameterizedClass() {
        /* When */
        val result = any<ThreeParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyFourParameterizedClass() {
        /* When */
        val result = any<FourParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyFiveParameterizedClass() {
        /* When */
        val result = any<FiveParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anySixParameterizedClass() {
        /* When */
        val result = any<SixParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anySevenParameterizedClass() {
        /* When */
        val result = any<SevenParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyNestedSingleParameterizedClass() {
        /* When */
        val result = any<NestedSingleParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyNestedTwoParameterizedClass() {
        /* When */
        val result = any<NestedTwoParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyNestedThreeParameterizedClass() {
        /* When */
        val result = any<NestedThreeParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyNestedFourParameterizedClass() {
        /* When */
        val result = any<NestedFourParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyNestedFiveParameterizedClass() {
        /* When */
        val result = any<NestedFiveParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyNestedSixParameterizedClass() {
        /* When */
        val result = any<NestedSixParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyNestedSevenParameterizedClass() {
        /* When */
        val result = any<NestedSevenParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyParameterizedClass() {
        /* When */
        val result = any<ParameterizedClass<ClosedClass>>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyNullableParameterClass() {
        /* When */
        val result = any<NullableParameterClass>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyStringList() {
        /* When */
        val result = any<List<String>>()

        /* Then */
        expect(result).toNotBeNull()
    }

    open class Fake {
        open fun go(arg: Any?) {
        }
    }

    class ClosedClass
    class ClosedParameterizedClass(val fake: Fake)
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
}
