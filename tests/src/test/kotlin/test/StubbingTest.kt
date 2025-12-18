package test

import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.exceptions.misusing.NotAMockException
import org.mockito.exceptions.misusing.UnfinishedStubbingException
import org.mockito.kotlin.argThat
import org.mockito.kotlin.check
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.stubbing
import org.mockito.kotlin.whenever
import org.mockito.kotlin.wheneverBlocking


class StubbingTest {
    @Test
    fun `should stub sync method call as part of mock creation`() {
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
    fun `should stub already existing mock, using whenever function`() {
        val mock = mock<SynchronousFunctions>()
        whenever { mock.stringResult() } doReturn "result"

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
        /* Given */
        val notAMock = Open()

        /* When, Then */
        val exception: NotAMockException = assertThrows {
            notAMock.stub {  }
        }
        expect(exception.message).toContain("Stubbing target is not a mock!")
    }

    @Test
    fun `should throw when trying to stub a real object with stubbing method`() {
        /* Given */
        val notAMock = Open()

        /* When, Then */
        val exception: NotAMockException = assertThrows {
            stubbing(notAMock) {  }
        }
        expect(exception.message).toContain("Stubbing target is not a mock!")
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
        whenever { mock.stringResult() }

        /* When */
        try {
            mock.stringResult()
        } catch (e: UnfinishedStubbingException) {
            /* Then */
            expect(e.message).toContain("Unfinished stubbing detected here:")
        }
    }

    @Test
    fun `should stub sync method call in reverse manner as part of mock creation`() {
        val mock = mock<SynchronousFunctions> {
            doReturn("A") on { stringResult() }
        }

        expect(mock.stringResult()).toBe("A")
    }

    @Test
    fun `should stub sync method call, with whenever`() {
        /* Given */
        val mock = mock<Open>()
        whenever { mock.stringResult() } doReturn "A"

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub sync method call, with reverse whenever`() {
        /* Given */
        val mock = mock<Open>()
        doReturn("A").whenever(mock).stringResult()

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub sync method call, using stub extension method`() {
        /* Given */
        val mock = mock<Open>()
        mock.stub{
            on { stringResult() } doReturn "A"
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub sync method call, using stubbing method`() {
        /* Given */
        val mock = mock<Open>()
        stubbing(mock) {
            on { stringResult() } doReturn "A"
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub suspendable function call, with whenever as part of mock creation`() {
        /* Given */
        val mock = mock<SuspendFunctions> { mock ->
            whenever { mock.stringResult() } doReturn "A"
        }

        /* When */
        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub sync method call in reverse manner, with whenever() and lambda as part of mock creation`() {
        /* Given */
        val mock = mock<SynchronousFunctions> { mock ->
            doReturn( "A").whenever(mock) { stringResult() }
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub suspendable function call in reverse manner with on() as part of mock creation`() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            doReturn( "A") on { stringResult() }
        }

        /* When */
        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub suspendable function call in reverse manner, with whenever() and lambda as part of mock creation`() {
        /* Given */
        val mock = mock<SynchronousFunctions> { mock ->
            doReturn( "A").whenever(mock) { stringResult() }
        }

        /* When */
        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub suspendable function call in reverse manner, with whenever() as part of mock creation`() = runTest{
        /* Given */
        val mock = mock<SuspendFunctions> {
            doReturn( "A").whenever(mock).stringResult()
        }

        /* When */
        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub sync function call within a suspendable lambda`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            on { stringResult() } doReturn "A"
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub sync function call within a suspendable lambda of whenever`() {
        /* Given */
        val mock = mock<SynchronousFunctions>()
        whenever { mock.stringResult() } doReturn "A"

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub suspendable function call with a call to the sync version of whenever`() = runTest {
        /* Given */
        val mock = mock<SuspendFunctions>()
        whenever (mock.stringResult()) doReturn "A"

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should support to stub suspendable function call with synchronous 'whenever' method`() = runTest {
        /* Given */
        val mock = mock<SuspendFunctions>()
        whenever(mock.stringResult()) doSuspendableAnswer {
            delay(0)
            "A"
        }

        /* When */
        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    @Suppress("DEPRECATION")
    fun `should provide backwards compatibility support to stub suspendable function call with 'wheneverBlocking' method`() {
        /* Given */
        val mock = mock<SuspendFunctions>()
        wheneverBlocking { mock.stringResult() } doReturn "A"

        /* When */
        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    @Suppress("DEPRECATION")
    fun `should provide backwards compatibility support to stub suspendable function call in reverse manner, with wheneverBlocking`() {
        /* Given */
        val mock = mock<SuspendFunctions> { mock ->
            doReturn( "A").wheneverBlocking(mock) { stringResult() }
        }

        /* When */
        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    @Suppress("DEPRECATION")
    fun `should provide backwards compatibility support to stub suspendable function call with 'onBlocking' method`() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            onBlocking { mock.stringResult() } doReturn "A"
        }

        /* When */
        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    @Suppress("DEPRECATION")
    fun `should provide backwards compatibility support to stub generics function calls with integer result`() {
        /* Given */
        val mock = mock<GenericMethods<Int>> {
            onGeneric { genericMethod() } doReturn 2
        }

        /* When */
        val result = mock.genericMethod()

        /* Then */
        expect(result).toBe(2)
    }
}
