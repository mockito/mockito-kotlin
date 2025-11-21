package test

import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assume.assumeFalse
import org.junit.Test
import org.mockito.Mockito
import org.mockito.exceptions.misusing.UnfinishedStubbingException
import org.mockito.kotlin.*
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
    fun `should stub consecutive function calls`() {
        /* Given */
        val mock = mock<Open> {
            on { stringResult() }.doReturn ("A", "B", "C")
        }

        /* When */
        val result =(1..3).map { _ ->
            mock.stringResult()
        }

        /* Then */
        expect(result).toBe(listOf("A", "B", "C"))
    }

    @Test
    fun `should stub consecutive function calls by a list of answers`() {
        /* Given */
        val mock = mock<Open> {
            on { stringResult() } doReturnConsecutively listOf("A", "B", "C")
        }

        /* When */
        val result =(1..3).map { _ ->
            mock.stringResult()
        }

        /* Then */
        expect(result).toBe(listOf("A", "B", "C"))
    }

    @Test
    fun `should stub builder method returning mock itself via defaultAnswer`() {
        /* Given */
        val mock = mock<SynchronousFunctions>(defaultAnswer = Mockito.RETURNS_SELF)

        /* When */
        val result = mock.builderMethod()

        /* Then */
        expect(result).toBeTheSameAs(mock)
    }

    @Test
    fun `should stub builder method returning mock itself via answer`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            on { builderMethod() } doAnswer Mockito.RETURNS_SELF
        }

        /* When */
        val result = mock.builderMethod()

        /* Then */
        expect(result).toBeTheSameAs(mock)
    }

    @Test
    fun `should stub builder method returning mock itself`() {
        /* Given */
        val mock = mock<SynchronousFunctions> { mock ->
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
        val mock = mock<SynchronousFunctions> {
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
        val mock = mock<SynchronousFunctions> {
            on { builderMethod() } doThrow IllegalArgumentException()
        }

        /* When, Then */
        assertThrows<IllegalArgumentException> {
            mock.builderMethod()
        }
    }

    @Test
    fun `should throw exception class on function call`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            on { builderMethod() } doThrow IllegalArgumentException::class
        }

        /* When, Then */
        assertThrows<IllegalArgumentException> {
            mock.builderMethod()
        }
    }

    @Test
    fun `should throw exception instances on consecutive function calls`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            on { builderMethod() }.doThrow(
                  IllegalArgumentException(),
                  UnsupportedOperationException()
            )
        }

        /* When, Then */
        assertThrows<IllegalArgumentException> {
            mock.builderMethod()
        }
        assertThrows<UnsupportedOperationException> {
            mock.builderMethod()
        }
    }

    @Test
    fun `should throw exception classes on consecutive function calls`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            on { builderMethod() }.doThrow(
                  IllegalArgumentException::class,
                  UnsupportedOperationException::class
            )
        }

        /* When, Then */
        assertThrows<IllegalArgumentException> {
            mock.builderMethod()
        }
        assertThrows<UnsupportedOperationException> {
            mock.builderMethod()
        }
    }

    @Test
    fun `should stub function call with result from answer instance`() {
        /* Given */
        val answer: Answer<String> = Answer { "result" }

        val mock = mock<SynchronousFunctions> {
            on { stringResult() } doAnswer answer
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("result")
    }

    @Test
    fun `should stub function call with result from lambda`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            on { stringResult() } doAnswer { "result" }
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("result")
    }

    @Test
    fun `should stub function call with result from lambda with argument`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
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
        val mock = mock<SynchronousFunctions> {
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
        val mock = mock<SynchronousFunctions> {
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
    fun `should stub already created mock`() {
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
    fun `should override stub of already created mock`() {
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
    // stubbingTwiceWithCheckArgumentMatchers_throwsException
    fun `should throw when check ArgumentMatcher is applied twice`() {
        val exception: IllegalStateException = assertThrows {
            mock<SynchronousFunctions> {
                on { stringResult(check { }) } doReturn "A"
                on { stringResult(check { }) } doReturn "B"
            }
        }
        expect(exception.message).toContain("The argument passed to the predicate was null")
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
        val mock = mock<GenericMethods<String>> {
            onGeneric { nullableReturnType() } doReturn "Test"
        }

        expect(mock.nullableReturnType()).toBe("Test")
    }

    @Test
    fun `should stub function call with value class result`() {
        /* Given */
        val valueClass = ValueClass("A")
        val mock = mock<SynchronousFunctions> {
            on { valueClassResult() } doReturn valueClass
        }

        /* When */
        val result: ValueClass = mock.valueClassResult()

        /* Then */
        expect(result).toBe(valueClass)
    }

    @Test
    fun `should stub function call with nullable value class result`() {
        /* Given */
        val valueClass = ValueClass("A")
        val mock = mock<SynchronousFunctions> {
            on { nullableValueClassResult() } doReturn valueClass
        }

        /* When */
        val result: ValueClass? = mock.nullableValueClassResult()

        /* Then */
        expect(result).toBe(valueClass)
    }

    @Test
    fun `should stub function call with nested value class result`() {
        /* Given */
        val nestedValueClass = NestedValueClass(ValueClass("A"))
        val mock = mock<SynchronousFunctions> {
            on { nestedValueClassResult() } doReturn nestedValueClass
        }

        /* When */
        val result: NestedValueClass = mock.nestedValueClassResult()

        /* Then */
        expect(result).toBe(nestedValueClass)
        expect(result.value).toBe(nestedValueClass.value)
    }

    @Test
    fun `should stub consecutive function call with value class results`() {
        /* Given */
        val valueClassA = ValueClass("A")
        val valueClassB = ValueClass("B")
        val mock = mock<SynchronousFunctions> {
            on { valueClassResult() }.doReturnConsecutively(valueClassA, valueClassB)
        }

        /* When */
        val result1 = mock.valueClassResult()
        val result2 = mock.valueClassResult()

        /* Then */
        expect(result1).toBe(valueClassA)
        expect(result2).toBe(valueClassB)
    }

    @Test
    fun `should stub suspendable function call with value class result`() =
        runTest {
            /* Given */
            val valueClass = ValueClass("A")
            val mock = mock<SuspendFunctions> {
                on(mock.valueClassResult()) doSuspendableAnswer {
                    delay(1)
                    valueClass
                }
            }

            /* When */
            val result: ValueClass = mock.valueClassResult()

            /* Then */
            expect(result).toBe(valueClass)
        }

    @Test
    fun `should stub function call to make real function call into mock`() {
        /* Given */
        val mock = mock<Open> {
            on { valueClassResult(any()) }.doCallRealMethod()
        }

        /* When */
        val result = mock.valueClassResult(ValueClass("Value"))

        /* Then */
        expect(result.content).toBe("Result: Value")
    }

    @Test
    fun `should throw when stubbing is incomplete`() {
        /* Given */
        val mock = mock<Open>()
        whenever(mock.stringResult())

        /* When */
        val exception = assertThrows<UnfinishedStubbingException> {
            mock.stringResult()
        }
        expect(exception.message).toContain("Unfinished stubbing detected here:")
    }

    @Test
    fun doReturn_throwsNPE() {
        assumeFalse("mockMakerInline is not enabled", mockMakerInlineEnabled())
        expectErrorWithMessage("look at the stack trace below") on {

            /* When */
            mock<Open> {
                on { throwsNPE() } doReturn "result"
            }
        }
    }
}
