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
    fun testOngoingStubbing_methodCall() {
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
    fun testOngoingStubbing_builder() {
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
    fun testOngoingStubbing_nullable() {
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
    fun testOngoingStubbing_doThrow() {
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
    fun testOngoingStubbing_doThrowClass() {
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
    fun testOngoingStubbing_doThrowVarargs() {
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
    fun testOngoingStubbing_doThrowClassVarargs() {
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
    fun testOngoingStubbing_doAnswer_lambda() {
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
    fun testOngoingStubbing_doAnswer_instance() {
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
    fun testOngoingStubbing_doAnswer_returnsSelf() {
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
    fun testOngoingStubbing_doAnswer_withArgument() {
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
    fun testOngoingStubbing_doAnswer_withDestructuredArgument() {
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
    fun testOngoingStubbing_doAnswer_withDestructuredArguments() {
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
    fun doReturn_withSingleItemList() {
        /* Given */
        val mock = mock<Open> {
            on { stringResult() } doReturnConsecutively listOf("a", "b")
        }

        /* Then */
        expect(mock.stringResult()).toBe("a")
        expect(mock.stringResult()).toBe("b")
    }

    @Test
    fun doReturn_withGenericIntReturnType_onGeneric() {
        /* Given */
        val mock = mock<GenericMethods<Int>> {
            onGeneric { genericMethod() } doReturn 2
        }

        /* Then */
        expect(mock.genericMethod()).toBe(2)
    }

    @Test
    fun doReturn_withGenericNullableReturnType_onGeneric() {
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
