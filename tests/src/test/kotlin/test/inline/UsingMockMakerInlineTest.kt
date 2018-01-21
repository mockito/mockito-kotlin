/*
 * The MIT License
 *
 * Copyright (c) 2016 Ian J. De Silva
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
import com.nhaarman.mockitokotlin2.*
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import test.mockMakerInlineEnabled
import java.io.IOException
import java.math.BigInteger

class UsingMockMakerInlineTest {

    class ClassToBeMocked {

        fun doSomething(@Suppress("UNUSED_PARAMETER") c: ClassToBeMocked) {
        }

        fun doSomethingElse(value: BigInteger): BigInteger {
            return value.plus(BigInteger.ONE)
        }
    }

    @Before
    fun setup() {
        mockMakerInlineEnabled = null
        assumeTrue(mockMakerInlineEnabled())
    }

    @Test
    fun mockClosedClass() {
        /* When */
        val result = mock<ClassToBeMocked>()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun anyClosedClass() {
        /* Given */
        val mock = mock<ClassToBeMocked>()

        /* When */
        mock.doSomething(mock)

        /* Then */
        verify(mock).doSomething(any())
    }

    @Test
    fun mockClosedFunction_mockStubbing() {
        /* Given */
        val mock = mock<ClassToBeMocked> {
            on { doSomethingElse(any()) } doReturn (BigInteger.ONE)
        }

        /* When */
        val result = mock.doSomethingElse(BigInteger.TEN)

        /* Then */
        expect(result).toBe(BigInteger.ONE)
    }

    @Test
    fun mockClosedFunction_whenever() {
        /* Given */
        val mock = mock<ClassToBeMocked>()
        whenever(mock.doSomethingElse(any())).doReturn(BigInteger.ONE)

        /* When */
        val result = mock.doSomethingElse(BigInteger.TEN)

        /* Then */
        expect(result).toBe(BigInteger.ONE)
    }

    /** https://github.com/nhaarman/mockito-kotlin/issues/27 */
    @Test
    fun anyThrowableWithSingleThrowableConstructor() {
        mock<Methods>().apply {
            throwableClass(ThrowableClass(IOException()))
            verify(this).throwableClass(any())
        }
    }

    interface Methods {

        fun throwableClass(t: ThrowableClass)
    }

    class ThrowableClass(cause: Throwable) : Throwable(cause)

}
