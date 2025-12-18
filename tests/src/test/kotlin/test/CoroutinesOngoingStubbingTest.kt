package test

import com.nhaarman.expect.expect
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doCallRealMethod
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doReturnConsecutively
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.stubbing.Answer

class CoroutinesOngoingStubbingTest {
    @Test
    fun `should stub suspendable function call`() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            on { stringResult() } doReturn "A"
        }

        /* When */
        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub suspendable function call within a coroutine scope`() = runTest {
        /* Given */
        val mock = mock<SuspendFunctions> {
            on { stringResult() } doReturn "A"
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub consecutive suspendable function calls`() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            on { stringResult() }.doReturn("A", "B", "C")
        }

        /* When */
        val result = runBlocking {
            (1..3).map { _ ->
                mock.stringResult()
            }
        }

        /* Then */
        expect(result).toBe(listOf("A", "B", "C"))
    }

    @Test
    fun `should stub consecutive suspendable function calls by a list of answers`() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            on { stringResult() } doReturnConsecutively listOf("A", "B", "C")
        }

        /* When */
        val result = runBlocking {
            (1..3).map { _ ->
                mock.stringResult()
            }
        }

        /* Then */
        expect(result).toBe(listOf("A", "B", "C"))
    }

    @Ignore("Default answers do not yet work for coroutines, see https://github.com/mockito/mockito-kotlin/issues/550")
    @Test
    fun `should stub builder method returning mock itself via defaultAnswer`() {
        /* Given */
        val mock = mock<SuspendFunctions>(defaultAnswer = Mockito.RETURNS_SELF)

        /* When */
        val result = runBlocking { mock.builderMethod() }

        /* Then */
        expect(result).toBeTheSameAs(mock)
    }

    @Ignore("Default answers do not yet work for coroutines, see https://github.com/mockito/mockito-kotlin/issues/550")
    @Test
    fun `should stub builder method returning mock itself via answer`() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            on { builderMethod() } doAnswer Mockito.RETURNS_SELF
        }

        /* When */
        val result = runBlocking { mock.builderMethod() }

        /* Then */
        expect(result).toBeTheSameAs(mock)
    }

    @Test
    fun `should stub builder method returning mock itself`() {
        /* Given */
        val mock = mock<SuspendFunctions> { mock ->
            on { builderMethod() } doReturn mock
        }

        /* When */
        val result = runBlocking { mock.builderMethod() }

        /* Then */
        expect(result).toBe(mock)
    }

    @Test
    fun `should stub suspendable function call with nullable result`() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            on { nullableStringResult() } doReturn "Test"
        }

        /* When */
        val result = runBlocking { mock.nullableStringResult() }

        /* Then */
        expect(result).toBe("Test")
    }

    @Test
    fun `should throw exception instance on suspendable function call`() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            on { builderMethod() } doThrow IllegalArgumentException()
        }

        /* When, Then */
        runBlocking {
            assertThrows<IllegalArgumentException> {
                mock.builderMethod()
            }
        }
    }

    @Test
    fun `should throw exception class on suspendable function call`() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            on { builderMethod() } doThrow IllegalArgumentException::class
        }

        /* When, Then */
        runBlocking {
            assertThrows<IllegalArgumentException> {
                mock.builderMethod()
            }
        }
    }

    @Test
    fun `should throw exception instances on consecutive suspendable function calls`() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            on { builderMethod() }.doThrow(
                IllegalArgumentException(),
                UnsupportedOperationException()
            )
        }

        /* When, Then */
        runBlocking {
            assertThrows<IllegalArgumentException> {
                mock.builderMethod()
            }
            assertThrows<UnsupportedOperationException> {
                mock.builderMethod()
            }
        }
    }

    @Test
    fun `should throw exception classes on consecutive suspendable function calls`() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            on { builderMethod() }.doThrow(
                IllegalArgumentException::class,
                UnsupportedOperationException::class
            )
        }

        /* When, Then */
        runBlocking {
            assertThrows<IllegalArgumentException> {
                mock.builderMethod()
            }
            assertThrows<UnsupportedOperationException> {
                mock.builderMethod()
            }
        }
    }

    @Test
    fun `should stub suspendable function call with result from answer instance`() {
        /* Given */
        val answer: Answer<String> = Answer { "result" }
        val mock = mock<SuspendFunctions> {
            on { stringResult() } doAnswer answer
        }

        /* When */

        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("result")
    }

    @Test
    fun `should stub suspendable function call with result from lambda`() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            on { stringResult() } doAnswer { "result" }
        }

        /* When */
        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("result")
    }

    @Test
    fun `should stub suspendable function call with result from lambda with argument`() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            on { stringResult(any()) } doAnswer { "${it.arguments[0]}-result" }
        }

        /* When */
        val result = runBlocking { mock.stringResult("argument") }

        /* Then */
        expect(result).toBe("argument-result")
    }

    @Test
    fun `should stub suspendable function call with result from lambda with deconstructed argument`() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            on { stringResult(any()) } doAnswer { (s: String) -> "$s-result" }
        }

        /* When */
        val result = runBlocking { mock.stringResult("argument") }

        /* Then */
        expect(result).toBe("argument-result")
    }

    @Test
    fun `should stub suspendable function call with result from lambda with deconstructed arguments`() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            on { stringResult(any(), any()) } doAnswer { (a: String, b: String) ->
                "$a + $b"
            }
        }

        /* When */
        val result = runBlocking { mock.stringResult("apple", "banana") }

        /* Then */
        expect(result).toBe("apple + banana")
    }

    @Test
    fun `should stub suspendable function call with value class result`() = runTest {
        /* Given */
        val valueClass = ValueClass("A")
        val mock = mock<SuspendFunctions> {
            // TODO: simplify to "on (mock.valueClassResult()) doReturn valueClass"
            on (mock.valueClassResult()) doSuspendableAnswer {
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
    fun `should stub suspendable function call with nullable value class result`() {
        /* Given */
        val valueClass = ValueClass("A")
        val mock = mock<SuspendFunctions> {
            // TODO: simplify to "on { nullableValueClassResult() } doReturn valueClass"
            on { nullableValueClassResult() } doSuspendableAnswer {
                delay(1)
                valueClass
            }
        }

        /* When */
        val result: ValueClass? = runBlocking { mock.nullableValueClassResult() }

        /* Then */
        expect(result).toBe(valueClass)
    }

    @Test
    fun `should stub suspendable function call with nested value class result`() {
        /* Given */
        val nestedValueClass = NestedValueClass(ValueClass("A"))
        val mock = mock<SuspendFunctions> {
            // TODO: simplify to "on { nestedValueClassResult() } doReturn nestedValueClass"
            on { nestedValueClassResult() } doSuspendableAnswer {
                delay(1)
                nestedValueClass
            }
        }

        /* When */
        val result: NestedValueClass = runBlocking { mock.nestedValueClassResult() }

        /* Then */
        expect(result).toBe(nestedValueClass)
        expect(result.value).toBe(nestedValueClass.value)
    }

    @Test
    fun `should stub suspendable function call with primitive value class result`() = runTest {
        /* Given */
        val primitiveValueClass = PrimitiveValueClass(42)
        val mock = mock<SuspendFunctions> {
            on(mock.primitiveValueClassResult()) doSuspendableAnswer {
                delay(1)
                primitiveValueClass
            }
        }

        /* When */
        val result: PrimitiveValueClass = mock.primitiveValueClassResult()

        /* Then */
        expect(result).toBe(primitiveValueClass)
    }

    @Test
    fun `should stub suspendable function call with nullable primitive value class result`() = runTest {
        /* Given */
        val primitiveValueClass = PrimitiveValueClass(42)
        val mock = mock<SuspendFunctions> {
            on (mock.nullablePrimitiveValueClassResult()) doSuspendableAnswer {
                delay(1)
                primitiveValueClass
            }
        }

        /* When */
        val result: PrimitiveValueClass? = mock.nullablePrimitiveValueClassResult()

        /* Then */
        expect(result).toBe(primitiveValueClass)
    }

    @Test
    fun `should stub consecutive suspendable function call with value class results`() {
        /* Given */
        val valueClassA = ValueClass("A")
        val valueClassB = ValueClass("B")
        val mock = mock<SuspendFunctions> {
            // TODO: simplify to "on { valueClassResult() }.doReturnConsecutively(valueClassA, valueClassB)"
            on { valueClassResult() } doSuspendableAnswer {
                delay(1)
                valueClassA
            } doSuspendableAnswer {
                delay(1)
                valueClassB
            }
        }

        /* When */
        val (result1, result2) = runBlocking {
            mock.valueClassResult() to mock.valueClassResult()
        }

        /* Then */
        expect(result1).toBe(valueClassA)
        expect(result2).toBe(valueClassB)
    }
}
