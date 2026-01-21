package test

import com.nhaarman.expect.expect
import org.junit.Test
import org.mockito.exceptions.base.MockitoAssertionError
import org.mockito.kotlin.*
import org.mockito.kotlin.verify

class VerificationTest : TestBase() {

    @Test
    fun atLeastXInvocations() {
        mock<SynchronousFunctions>().apply {
            string("")
            string("")

            verify(this, atLeast(2)).string(any())
        }
    }

    @Test
    fun testAtLeastOnce() {
        mock<SynchronousFunctions>().apply {
            string("")
            string("")

            verify(this, atLeastOnce()).string(any())
        }
    }

    @Test
    fun atMostXInvocations() {
        mock<SynchronousFunctions>().apply {
            string("")
            string("")

            verify(this, atMost(2)).string(any())
        }
    }

    @Test
    fun testCalls() {
        mock<SynchronousFunctions>().apply {
            string("")
            string("")

            inOrder(this).verify(this, calls(2)).string(any())
        }
    }

    @Test
    fun testInOrderWithLambda() {
        /* Given */
        val a = mock<() -> Unit>()
        val b = mock<() -> Unit>()

        /* When */
        b()
        a()

        /* Then */
        inOrder(a, b) {
            verify(b).invoke()
            verify(a).invoke()
        }
    }

    @Test
    fun testInOrderWithReceiver() {
        /* Given */
        val mock = mock<SynchronousFunctions>()

        /* When */
        mock.string("")
        mock.int(0)

        /* Then */
        mock.inOrder {
            verify().string(any())
            verify().int(any())
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun testClearInvocations() {
        val mock = mock<SynchronousFunctions>().apply { string("") }

        clearInvocations(mock)

        verify(mock, never()).string(any())
    }

    @Test
    fun testDescription() {
        try {
            mock<SynchronousFunctions>().apply { verify(this, description("Test")).string(any()) }
            throw AssertionError("Verify should throw Exception.")
        } catch (e: MockitoAssertionError) {
            expect(e.message).toContain("Test")
        }
    }

    @Test
    fun testAfter() {
        mock<SynchronousFunctions>().apply {
            int(3)
            verify(this, after(10)).int(3)
        }
    }

    @Test
    fun testResultArgumentUnit() {
        mock<SynchronousFunctions>().apply {
            resultArgument(Result.success(Unit))

            verify(this).resultArgument(Result.success(Unit))
        }
    }

    @Test
    fun testResultArgumentUnitWithEq() {
        mock<SynchronousFunctions>().apply {
            resultArgument(Result.success(Unit))

            verify(this).resultArgument(eq(Result.success(Unit)))
        }
    }

    @Test
    fun testResultArgumentValueClass() {
        mock<SynchronousFunctions>().apply {
            val result = Result.success(ValueClass("test"))
            resultArgument(result)

            verify(this).resultArgument(result)
        }
    }

    @Test
    fun testResultArgumentValueClassWithEq() {
        mock<SynchronousFunctions>().apply {
            val result = Result.success(ValueClass("test"))
            resultArgument(result)

            verify(this).resultArgument(eq(result))
        }
    }

    @Test
    fun testResultArgumentLongValueClass() {
        mock<SynchronousFunctions>().apply {
            val result = Result.success(LongValueClass(123))
            resultArgument(result)

            verify(this).resultArgument(result)
        }
    }

    @Test
    fun testResultArgumentLongValueClassWithEq() {
        mock<SynchronousFunctions>().apply {
            val result = Result.success(LongValueClass(123))
            resultArgument(result)

            verify(this).resultArgument(eq(result))
        }
    }
}
