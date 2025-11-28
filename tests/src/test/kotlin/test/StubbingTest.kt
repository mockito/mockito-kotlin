package test

import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import org.junit.Test
import org.mockito.exceptions.misusing.UnfinishedStubbingException
import org.mockito.kotlin.argThat
import org.mockito.kotlin.check
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.stubbing
import org.mockito.kotlin.whenever


class StubbingTest {
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
    fun stubbingRealObject() {
        val notAMock = ""

        /* Expect */
        expectErrorWithMessage("is not a mock!").on {
            notAMock.stub { }
        }
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
    fun testMockitoStackOnUnfinishedStubbing() {
        /* Given */
        val mock = mock<Open>()
        whenever(mock.stringResult())

        /* When */
        try {
            mock.stringResult()
        } catch (e: UnfinishedStubbingException) {
            /* Then */
            expect(e.message).toContain("Unfinished stubbing detected here:")
            expect(e.message).toContain("-> at test.StubbingTest.testMockitoStackOnUnfinishedStubbing")
        }
    }
}
