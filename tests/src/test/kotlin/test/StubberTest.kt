package test

import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import com.nhaarman.mockitokotlin2.*
import org.junit.Test

class StubberTest : TestBase() {

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
        } catch (e: IllegalStateException) {
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
    fun testThenReturnVararg() {
        val mock = mock<Methods>()

        mockVararg(mock, "test1", "test2")

        expect(mock.stringResult()).toBe("test1")
        expect(mock.stringResult()).toBe("test2")
    }

    private fun mockVararg(mock: Methods, vararg values: String) =
        whenever(mock.stringResult()).thenReturn(*values)
}
