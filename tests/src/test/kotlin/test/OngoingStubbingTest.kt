package test

import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import kotlinx.coroutines.runBlocking
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
    fun `should stub builder method returning mock itself via answer`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            on { builderMethod() } doAnswer Mockito.RETURNS_SELF
        }

        /* When */
        val result = mock.builderMethod()

        /* Then */
        expect(result).toBe(mock)
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
        // any consecutive call should throw the last specified exception
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
        // any consecutive call should throw the last specified exception
        assertThrows<UnsupportedOperationException> {
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
        // any consecutive call should throw the last specified exception
        assertThrows<IllegalArgumentException> {
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
        // any consecutive call should throw the last specified exception
        assertThrows<UnsupportedOperationException> {
            mock.builderMethod()
        }
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
    fun `should stub function call with result from an answer instance`() {        /* Given */
        /* Given */
        val answer = Answer { "result" }
        val mock = mock<SynchronousFunctions> {
            on { stringResult() } doAnswer answer
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
    fun `should stub generics function call with explicit generics type`() {
        /* Given */
        val mock = mock<GenericMethods<Int>> {
            onGeneric({ genericMethod() }, Int::class) doReturn 2
        }

        /* Then */
        expect(mock.genericMethod()).toBe(2)
    }

    @Test
    fun `should stub nullable generics function call with string result`() {
        /* Given */
        val mock = mock<GenericMethods<String>> {
            on { nullableReturnType() } doReturn "Test"
        }

        /* When */
        val result = mock.nullableReturnType()

        /* Then */
        expect(result).toBe("Test")
    }

    @Test
    fun `should stub suspendable generics function call with integer result`() {
        /* Given */
        val mock = mock<GenericMethods<Int>> {
            on { suspendableGenericMethod() } doReturn 2
        }

        /* When */
        val result = runBlocking { mock.suspendableGenericMethod() }

        /* Then */
        expect(result).toBe(2)
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
    fun `should stub function call with primitive value class result`() {
        /* Given */
        val primitiveValueClass = PrimitiveValueClass(42)
        val mock = mock<SynchronousFunctions> {
            on { primitiveValueClassResult() } doReturn primitiveValueClass
        }

        /* When */
        val result: PrimitiveValueClass = mock.primitiveValueClassResult()

        /* Then */
        expect(result).toBe(primitiveValueClass)
    }

    @Test
    fun `should stub function call with nullable primitive value class result`() {
        /* Given */
        val primitiveValueClass = PrimitiveValueClass(42)
        val mock = mock<SynchronousFunctions> {
            on { nullablePrimitiveValueClassResult() } doReturn primitiveValueClass
        }

        /* When */
        val result: PrimitiveValueClass? = mock.nullablePrimitiveValueClassResult()

        /* Then */
        expect(result).toBe(primitiveValueClass)
    }

    @Test
    fun `should stub consecutive function calls with value class results`() {
        /* Given */
        val valueClassA = ValueClass("A")
        val valueClassB = ValueClass("B")
        val mock = mock<SynchronousFunctions> {
            on { valueClassResult() } doReturnConsecutively listOf(valueClassA, valueClassB)
        }

        /* When */
        val result1 = mock.valueClassResult()
        val result2 = mock.valueClassResult()

        /* Then */
        expect(result1).toBe(valueClassA)
        expect(result2).toBe(valueClassB)
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
