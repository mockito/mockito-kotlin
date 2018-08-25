package test

import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import com.nhaarman.expect.fail
import com.nhaarman.mockitokotlin2.*
import org.junit.Assume.assumeFalse
import org.junit.Test
import org.mockito.Mockito
import org.mockito.exceptions.misusing.UnfinishedStubbingException
import org.mockito.stubbing.Answer

class OngoingStubbingTest : TestBase() {

    @Test
    fun testOngoingStubbing_methodCall() {
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
        } catch (e: IllegalArgumentException) {
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
        } catch (e: IllegalArgumentException) {
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
        } catch (e: IllegalArgumentException) {
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch (e: UnsupportedOperationException) {
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
        } catch (e: IllegalArgumentException) {
        }

        try {
            /* When */
            mock.builderMethod()
            fail("No exception thrown")
        } catch (e: UnsupportedOperationException) {
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
        val mock = mock<Methods> {
            on { stringResult() } doAnswer Answer<String> { "result" }
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
    fun testMockStubbingAfterCreatingMock() {
        val mock = mock<Methods>()

        //create stub after creation of mock
        mock.stub {
            on { stringResult() } doReturn "result"
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("result")
    }

    @Test
    fun testOverrideDefaultStub() {
        /* Given mock with stub */
        val mock = mock<Methods> {
            on { stringResult() } doReturn "result1"
        }

        /* override stub */
        mock.stub {
            on { stringResult() } doReturn "result2"
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("result2")
    }


    @Test
    fun stubbingTwiceWithArgumentMatchers() {
        /* When */
        val mock = mock<Methods> {
            on { stringResult(argThat { this == "A" }) } doReturn "A"
            on { stringResult(argThat { this == "B" }) } doReturn "B"
        }

        /* Then */
        expect(mock.stringResult("A")).toBe("A")
        expect(mock.stringResult("B")).toBe("B")
    }

    @Test
    fun stubbingTwiceWithCheckArgumentMatchers_throwsException() {
        /* Expect */
        expectErrorWithMessage("null").on {
            mock<Methods> {
                on { stringResult(check { }) } doReturn "A"
                on { stringResult(check { }) } doReturn "B"
            }
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
    fun doReturn_throwsNPE() {
        assumeFalse(mockMakerInlineEnabled())
        expectErrorWithMessage("look at the stack trace below") on {

            /* When */
            mock<Open> {
                on { throwsNPE() } doReturn "result"
            }
        }
    }

    @Test
    fun doReturn_withGenericIntReturnType_on() {
        /* Expect */
        expectErrorWithMessage("onGeneric") on {

            /* When */
            mock<GenericMethods<Int>> {
                on { genericMethod() } doReturn 2
            }
        }
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
    fun stubbingExistingMock() {
        /* Given */
        val mock = mock<Methods>()

        /* When */
        stubbing(mock) {
            on { stringResult() } doReturn "result"
        }

        /* Then */
        expect(mock.stringResult()).toBe("result")
    }

    @Test
    fun testMockitoStackOnUnfinishedStubbing() {
        /* Given */
        val mock = mock<Open>()
        whenever(mock.stringResult())

        /* When */
        try {
            mock.stringResult()
        } catch(e: UnfinishedStubbingException) {
            /* Then */
            expect(e.message).toContain("Unfinished stubbing detected here:")
            expect(e.message).toContain("-> at test.OngoingStubbingTest.testMockitoStackOnUnfinishedStubbing")
        }
    }
}
