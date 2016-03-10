import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import org.mockito.exceptions.base.MockitoAssertionError

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
    fun anyCollectionOfClosed() {
        mock<Methods>().apply {
            closedCollection(listOf())
            verify(this).closedCollection(any())
            verify(this).closedCollection(anyCollection())
        }
    }

    @Test
    fun anyListOfClosed() {
        mock<Methods>().apply {
            closedList(listOf())
            verify(this).closedList(any())
            verify(this).closedList(anyList())
        }
    }

    @Test
    fun anyClosedStringMap() {
        mock<Methods>().apply {
            closedStringMap(mapOf())
            verify(this).closedStringMap(any())
            verify(this).closedStringMap(anyMap())
        }
    }

    @Test
    fun anyClosedSet() {
        mock<Methods>().apply {
            closedSet(setOf())
            verify(this).closedSet(any())
            verify(this).closedSet(anySet())
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
    fun listArgThat() {
        mock<Methods>().apply {
            closedList(listOf(Closed(), Closed()))
            verify(this).closedList(argThat {
                size == 2
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
}
