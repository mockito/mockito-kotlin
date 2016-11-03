import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import com.nhaarman.expect.fail
import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import org.mockito.exceptions.base.MockitoAssertionError
import java.io.IOException

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

class MockitoTest {

    @Test
    fun anyString() {
        mock<Methods>().apply {
            string("")
            verify(this).string(any())
        }
    }

    @Test
    fun anyClosedClass() {
        mock<Methods>().apply {
            closed(Closed())
            verify(this).closed(any())
        }
    }

    @Test
    fun anyIntArray() {
        mock<Methods>().apply {
            intArray(intArrayOf())
            verify(this).intArray(any())
        }
    }

    @Test
    fun anyClassArray() {
        mock<Methods>().apply {
            closedArray(arrayOf(Closed()))
            verify(this).closedArray(anyArray())
        }
    }

    @Test
    fun anyNullableClassArray() {
        mock<Methods>().apply {
            closedNullableArray(arrayOf(Closed(), null))
            verify(this).closedNullableArray(anyArray())
        }
    }

    @Test
    fun anyStringVararg() {
        mock<Methods>().apply {
            closedVararg(Closed(), Closed())
            verify(this).closedVararg(anyVararg())
        }
    }

    @Test
    fun anyNull_neverVerifiesAny() {
        mock<Methods>().apply {
            nullableString(null)
            verify(this, never()).nullableString(any())
        }
    }

    @Test
    fun anyNull_verifiesAnyOrNull() {
        mock<Methods>().apply {
            nullableString(null)
            verify(this).nullableString(anyOrNull())
        }
    }

    /** https://github.com/nhaarman/mockito-kotlin/issues/27 */
    @Test
    fun anyThrowableWithSingleThrowableConstructor() {
        mock<Methods>().apply {
            throwableClass(ThrowableClass(IOException()))
            verify(this).throwableClass(any())
        }
    }

    @Test
    fun listArgThat() {
        mock<Methods>().apply {
            closedList(listOf(Closed(), Closed()))
            verify(this).closedList(argThat {
                size == 2
            })
        }
    }

    @Test
    fun listArgForWhich() {
        mock<Methods>().apply {
            closedList(listOf(Closed(), Closed()))
            verify(this).closedList(argForWhich {
                size == 2
            })
        }
    }

    @Test
    fun listArgCheck() {
        mock<Methods>().apply {
            closedList(listOf(Closed(), Closed()))
            verify(this).closedList(check {
                expect(it.size).toBe(2)
            })
        }
    }

    @Test
    fun atLeastXInvocations() {
        mock<Methods>().apply {
            string("")
            string("")

            verify(this, atLeast(2)).string(any())
        }
    }

    @Test
    fun testAtLeastOnce() {
        mock<Methods>().apply {
            string("")
            string("")

            verify(this, atLeastOnce()).string(any())
        }
    }

    @Test
    fun atMostXInvocations() {
        mock<Methods>().apply {
            string("")
            string("")

            verify(this, atMost(2)).string(any())
        }
    }

    @Test
    fun testCalls() {
        mock<Methods>().apply {
            string("")
            string("")

            inOrder(this).verify(this, calls(2)).string(any())
        }
    }

    @Test
    fun testClearInvocations() {
        val mock = mock<Methods>().apply {
            string("")
        }

        clearInvocations(mock)

        verify(mock, never()).string(any())
    }

    @Test
    fun testDescription() {
        try {
            mock<Methods>().apply {
                verify(this, description("Test")).string(any())
            }
            throw AssertionError("Verify should throw Exception.")
        } catch (e: MockitoAssertionError) {
            expect(e.message).toContain("Test")
        }
    }

    @Test
    fun testDoAnswer() {
        val mock = mock<Methods>()

        doAnswer { "Test" }
                .whenever(mock)
                .stringResult()

        expect(mock.stringResult()).toBe("Test")
    }

    @Test
    fun testDoCallRealMethod() {
        val mock = mock<Open>()

        doReturn("Test").whenever(mock).stringResult()
        doCallRealMethod().whenever(mock).stringResult()

        expect(mock.stringResult()).toBe("Default")
    }

    @Test
    fun testDoNothing() {
        val spy = spy(Open())
        val array = intArrayOf(3)

        doNothing().whenever(spy).modifiesContents(array)
        spy.modifiesContents(array)

        expect(array[0]).toBe(3)
    }

    @Test
    fun testDoReturnValue() {
        val mock = mock<Methods>()

        doReturn("test").whenever(mock).stringResult()

        expect(mock.stringResult()).toBe("test")
    }

    @Test
    fun testDoReturnNullValue() {
        val mock = mock<Methods>()

        doReturn(null).whenever(mock).stringResult()

        expect(mock.stringResult()).toBeNull()
    }

    @Test
    fun testDoReturnNullValues() {
        val mock = mock<Methods>()

        doReturn(null, null).whenever(mock).stringResult()

        expect(mock.stringResult()).toBeNull()
        expect(mock.stringResult()).toBeNull()
    }

    @Test
    fun testDoReturnValues() {
        val mock = mock<Methods>()

        doReturn("test", "test2").whenever(mock).stringResult()

        expect(mock.stringResult()).toBe("test")
        expect(mock.stringResult()).toBe("test2")
    }

    @Test
    fun testDoThrowClass() {
        val mock = mock<Open>()

        doThrow(IllegalStateException::class).whenever(mock).go()

        try {
            mock.go()
            throw AssertionError("Call should have thrown.")
        } catch(e: IllegalStateException) {
        }
    }

    @Test
    fun testDoThrow() {
        val mock = mock<Open>()

        doThrow(IllegalStateException("test")).whenever(mock).go()

        expectErrorWithMessage("test").on {
            mock.go()
        }
    }

    @Test
    fun testMockStubbing_lambda() {
        /* Given */
        val mock = mock<Open>() {
            on { stringResult() } doReturn "A"
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun testMockStubbing_normalOverridesLambda() {
        /* Given */
        val mock = mock<Open>() {
            on { stringResult() }.doReturn("A")
        }
        whenever(mock.stringResult()).thenReturn("B")

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("B")
    }

    @Test
    fun testMockStubbing_methodCall() {
        /* Given */
        val mock = mock<Open>()
        mock<Open> {
            on(mock.stringResult()).doReturn("A")
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun testMockStubbing_builder() {
        /* Given */
        val mock = mock<Methods> { mock ->
            on { builderMethod() } doReturn mock
        }

        /* When */
        val result = mock.builderMethod()

        /* Then */
        expect(result).toBeTheSameAs(mock)
    }

    @Test
    fun testMockStubbing_doThrow() {
        /* Given */
        val mock = mock<Methods> { mock ->
            on { builderMethod() } doThrow IllegalArgumentException()
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch(e: IllegalArgumentException) {
        }
    }

    @Test
    fun testMockStubbing_doThrowClass() {
        /* Given */
        val mock = mock<Methods> { mock ->
            on { builderMethod() } doThrow IllegalArgumentException::class
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch(e: IllegalArgumentException) {
        }
    }

    @Test
    fun testMockStubbing_doThrowVarargs() {
        /* Given */
        val mock = mock<Methods> { mock ->
            on { builderMethod() }.doThrow(IllegalArgumentException(), UnsupportedOperationException())
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch(e: IllegalArgumentException) {
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch(e: UnsupportedOperationException) {
        }
    }

    @Test
    fun testMockStubbing_doThrowClassVarargs() {
        /* Given */
        val mock = mock<Methods> { mock ->
            on { builderMethod() }.doThrow(IllegalArgumentException::class, UnsupportedOperationException::class)
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch(e: IllegalArgumentException) {
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch(e: UnsupportedOperationException) {
        }
    }

    @Test
    fun doReturn_withSingleItemList() {
        /* Given */
        val mock = mock<Open> {
            on { stringResult() } doReturn listOf("a", "b")
        }

        /* Then */
        expect(mock.stringResult()).toBe("a")
        expect(mock.stringResult()).toBe("b")
    }

    @Test
    fun doReturn_withGenericIntReturnType() {
        /* Given */
        val mock = mock<GenericMethods<Int>> {
            on { genericMethod() } doReturn 2
        }

        /* Then */
        expect(mock.genericMethod()).toBe(2)
    }
}
