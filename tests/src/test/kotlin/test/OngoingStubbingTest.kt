package test

import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import com.nhaarman.expect.fail
import org.junit.Assume.assumeFalse
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doReturnConsecutively
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.stubbing.Answer

class OngoingStubbingTest : TestBase() {
    @Test
    fun `should stub function call`() {
        /* Given */
        val mock = mock<Open> {
            on { stringResult() } doReturn "A"
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub builder method returning mock itself`() {
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
    fun `should stub function call with nullable result`() {
        /* Given */
        val mock = mock<Methods> {
            on { nullableStringResult() } doReturn "Test"
        }

        /* When */
        val result = mock.nullableStringResult()

        /* Then */
        expect(result).toBe("Test")
    }

    @Test
    fun `should throw exception instance on function call`() {
        /* Given */
        val mock = mock<Methods> {
            on { builderMethod() } doThrow IllegalArgumentException()
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch (_: IllegalArgumentException) {
        }
    }

    @Test
    fun `should throw exception class on function call`() {
        /* Given */
        val mock = mock<Methods> {
            on { builderMethod() } doThrow IllegalArgumentException::class
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch (_: IllegalArgumentException) {
        }
    }

    @Test
    fun `should throw exception instances on consecutive function calls`() {
        /* Given */
        val mock = mock<Methods> {
            on { builderMethod() }.doThrow(
                  IllegalArgumentException(),
                  UnsupportedOperationException()
            )
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch (_: IllegalArgumentException) {
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch (_: UnsupportedOperationException) {
        }
    }

    @Test
    fun `should throw exception classes on consecutive function calls`() {
        /* Given */
        val mock = mock<Methods> {
            on { builderMethod() }.doThrow(
                  IllegalArgumentException::class,
                  UnsupportedOperationException::class
            )
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch (_: IllegalArgumentException) {
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch (_: UnsupportedOperationException) {
        }
    }

    @Test
    fun `should stub function call with result from lambda`() {
        /* Given */
        val mock = mock<Methods> {
            on { stringResult() } doAnswer { "result" }
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("result")
    }

    @Test
    fun `should stub function call with result from an answer instance`() {        /* Given */
        /* Given */
        val answer = Answer { "result" }
        val mock = mock<Methods> {
            on { stringResult() } doAnswer answer
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("result")
    }

    @Test
    fun `should stub builder method returning mock itself via answer`() {
        /* Given */
        val mock = mock<Methods> {
            on { builderMethod() } doAnswer Mockito.RETURNS_SELF
        }

        /* When */
        val result = mock.builderMethod()

        /* Then */
        expect(result).toBe(mock)
    }

    @Test
    fun `should stub function call with result from lambda with argument`() {
        /* Given */
        val mock = mock<Methods> {
            on { stringResult(any()) } doAnswer { "${it.arguments[0]}-result" }
        }

        /* When */
        val result = mock.stringResult("argument")

        /* Then */
        expect(result).toBe("argument-result")
    }

    @Test
    fun `should stub function call with result from lambda with deconstructed argument`() {
        /* Given */
        val mock = mock<Methods> {
            on { stringResult(any()) } doAnswer { (s: String) -> "$s-result" }
        }

        /* When */
        val result = mock.stringResult("argument")

        /* Then */
        expect(result).toBe("argument-result")
    }

    @Test
    fun `should stub function call with result from lambda with deconstructed arguments`() {
        /* Given */
        val mock = mock<Methods> {
            on { varargBooleanResult(any(), any()) } doAnswer { (a: String, b: String) ->
                a == b.trim()
            }
        }

        /* When */
        val result = mock.varargBooleanResult("argument", "   argument   ")

        /* Then */
        expect(result).toBe(true)
    }

    @Test
    fun `should stub consecutive function calls by a list of answers`() {
        /* Given */
        val mock = mock<Open> {
            on { stringResult() } doReturnConsecutively listOf("a", "b")
        }

        /* Then */
        expect(mock.stringResult()).toBe("a")
        expect(mock.stringResult()).toBe("b")
    }

    @Test
    fun `should stub function call with integer result`() {
        /* Given */
        val mock = mock<GenericMethods<Int>> {
            onGeneric { genericMethod() } doReturn 2
        }

        /* Then */
        expect(mock.genericMethod()).toBe(2)
    }

    @Test
    fun `should stub nullable function call with string result`() {
        val m = mock<GenericMethods<String>> {
            onGeneric { nullableReturnType() } doReturn "Test"
        }

        expect(m.nullableReturnType()).toBe("Test")
    }

    @Test
    fun doReturn_throwsNPE() {
        assumeFalse(mockMakerInlineEnabled())
        expectErrorWithMessage("look at the stack trace below") on {

            /* When */
            mock<Open> {
                on { throwsNPE() } doReturn "result"
            }
        }
    }
}
