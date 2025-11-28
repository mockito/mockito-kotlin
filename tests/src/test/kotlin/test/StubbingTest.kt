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
    fun `should stub already existing mock, using stubbing function`() {
        /* Given */
        val mock = mock<SynchronousFunctions>()

        /* When */
        stubbing(mock) {
            on { stringResult() } doReturn "result"
        }

        /* Then */
        expect(mock.stringResult()).toBe("result")
    }

    @Test
    fun `should stub already existing mock, using stub extension function`() {
        val mock = mock<SynchronousFunctions>()

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
    fun `should override default stub of mock`() {
        /* Given mock with stub */
        val mock = mock<SynchronousFunctions> {
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
    fun `should stub 2 method calls determined by ArgumentMatchers`() {
        /* When */
        val mock = mock<SynchronousFunctions> {
            on { stringResult(argThat { this == "A" }) } doReturn "A"
            on { stringResult(argThat { this == "B" }) } doReturn "B"
        }

        /* Then */
        expect(mock.stringResult("A")).toBe("A")
        expect(mock.stringResult("B")).toBe("B")
    }

    @Test
    fun `should throw when trying to stub a real object with stub extension method`() {
        val notAMock = ""

        /* Expect */
        expectErrorWithMessage("is not a mock!").on {
            notAMock.stub { }
        }
    }

    @Test
    fun `should throw when check ArgumentMatcher is applied twice`() {
        /* Expect */
        expectErrorWithMessage("null").on {
            mock<SynchronousFunctions> {
                on { stringResult(check { }) } doReturn "A"
                on { stringResult(check { }) } doReturn "B"
            }
        }
    }

    @Test
    fun `should throw when stubbing is incomplete`() {
        /* Given */
        val mock = mock<Open>()
        whenever(mock.stringResult())

        /* When */
        try {
            mock.stringResult()
        } catch (e: UnfinishedStubbingException) {
            /* Then */
            expect(e.message).toContain("Unfinished stubbing detected here:")
            expect(e.message).toContain("-> at test.StubbingTest.should throw when stubbing is incomplete")
        }
    }
}
