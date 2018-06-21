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

import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.*
import org.junit.After
import org.junit.Test
import org.mockito.Mockito
import java.util.*

class SpyTest : TestBase() {

    private val interfaceInstance: MyInterface = MyClass()
    private val openClassInstance: MyClass = MyClass()
    private val closedClassInstance: ClosedClass = ClosedClass()

    @After
    override fun tearDown() {
        super.tearDown()
        Mockito.validateMockitoUsage()
    }

    @Test
    fun spyInterfaceInstance() {
        /* When */
        val result = spy(interfaceInstance)

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun spyOpenClassInstance() {
        /* When */
        val result = spy(openClassInstance)

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun doReturnWithSpy() {
        val date = spy(Date())
        doReturn(123L).whenever(date).time
        expect(date.time).toBe(123L)
    }

    @Test
    fun doNothingWithSpy() {
        val date = spy(Date(0))
        doNothing().whenever(date).time = 5L
        date.time = 5L
        expect(date.time).toBe(0L)
    }

    @Test(expected = IllegalArgumentException::class)
    fun doThrowWithSpy() {
        val date = spy(Date(0))
        doThrow(IllegalArgumentException()).whenever(date).time
        date.time
    }

    @Test
    fun doCallRealMethodWithSpy() {
        val date = spy(Date(0))
        doReturn(123L).whenever(date).time
        doCallRealMethod().whenever(date).time
        expect(date.time).toBe(0L)
    }

    @Test
    fun doReturnWithDefaultInstanceSpyStubbing() {
        val timeVal = 12L

        val dateSpy = spy<Date> {
            on { time } doReturn timeVal
        }

        expect(dateSpy.time).toBe(timeVal)
    }

    @Test
    fun doReturnWithSpyStubbing() {
        val timeVal = 15L

        val dateSpy = spy(Date(0)) {
            on { time } doReturn timeVal
        }

        expect(dateSpy.time).toBe(timeVal)
    }

    @Test
    fun passAnyStringToSpy() {
        /* Given */
        val my = spy(MyClass())

        /* When */
        doReturn("mocked").whenever(my).foo(any())

        /* Then */
        expect(my.foo("hello")).toBe("mocked")
    }

    private interface MyInterface {

        fun foo(value: String): String
    }

    private open class MyClass : MyInterface {

        override fun foo(value: String): String = value
    }

    private class ClosedClass
}

