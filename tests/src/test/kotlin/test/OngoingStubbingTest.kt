package test

import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeFalse
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doCallRealMethod
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doReturnConsecutively
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.stubbing.Answer

class OngoingStubbingTest : TestBase() {
    @Test
    fun `should stub function call`() {
        /* Given */
        val mock = mock<Open> { on { stringResult() } doReturn "A" }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub consecutive function calls`() {
        /* Given */
        val mock = mock<Open> { on { stringResult() }.doReturn("A", "B", "C") }

        /* When */
        val result = (1..3).map { _ -> mock.stringResult() }

        /* Then */
        expect(result).toBe(listOf("A", "B", "C"))
    }

    @Test
    fun `should stub consecutive function calls by a list of answers`() {
        /* Given */
        val mock = mock<Open> { on { stringResult() } doReturnConsecutively listOf("A", "B", "C") }

        /* When */
        val result = (1..3).map { _ -> mock.stringResult() }

        /* Then */
        expect(result).toBe(listOf("A", "B", "C"))
    }

    @Test
    fun `should stub builder method returning mock itself via answer`() {
        /* Given */
        val mock =
            mock<SynchronousFunctions> { on { builderMethod() } doAnswer Mockito.RETURNS_SELF }

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
        val mock = mock<SynchronousFunctions> { mock -> on { builderMethod() } doReturn mock }

        /* When */
        val result = mock.builderMethod()

        /* Then */
        expect(result).toBeTheSameAs(mock)
    }

    @Test
    fun `should stub function call with nullable result`() {
        /* Given */
        val mock = mock<SynchronousFunctions> { on { nullableStringResult() } doReturn "Test" }

        /* When */
        val result = mock.nullableStringResult()

        /* Then */
        expect(result).toBe("Test")
    }

    @Test
    fun `should throw exception instance on function call`() {
        /* Given */
        val mock =
            mock<SynchronousFunctions> { on { builderMethod() } doThrow IllegalArgumentException() }

        /* When, Then */
        assertThrows<IllegalArgumentException> { mock.builderMethod() }
        // any consecutive call should throw the last specified exception
        assertThrows<IllegalArgumentException> { mock.builderMethod() }
    }

    @Test
    fun `should throw exception instances on consecutive function calls`() {
        /* Given */
        val mock =
            mock<SynchronousFunctions> {
                on { builderMethod() }
                    .doThrow(IllegalArgumentException(), UnsupportedOperationException())
            }

        /* When, Then */
        assertThrows<IllegalArgumentException> { mock.builderMethod() }
        assertThrows<UnsupportedOperationException> { mock.builderMethod() }
        // any consecutive call should throw the last specified exception
        assertThrows<UnsupportedOperationException> { mock.builderMethod() }
    }

    @Test
    fun `should throw exception class on function call`() {
        /* Given */
        val mock =
            mock<SynchronousFunctions> {
                on { builderMethod() } doThrow IllegalArgumentException::class
            }

        /* When, Then */
        assertThrows<IllegalArgumentException> { mock.builderMethod() }
        // any consecutive call should throw the last specified exception
        assertThrows<IllegalArgumentException> { mock.builderMethod() }
    }

    @Test
    fun `should throw exception classes on consecutive function calls`() {
        /* Given */
        val mock =
            mock<SynchronousFunctions> {
                on { builderMethod() }
                    .doThrow(IllegalArgumentException::class, UnsupportedOperationException::class)
            }

        /* When, Then */
        assertThrows<IllegalArgumentException> { mock.builderMethod() }
        assertThrows<UnsupportedOperationException> { mock.builderMethod() }
        // any consecutive call should throw the last specified exception
        assertThrows<UnsupportedOperationException> { mock.builderMethod() }
    }

    @Test
    fun `should stub function call with result from lambda`() {
        /* Given */
        val mock = mock<SynchronousFunctions> { on { stringResult() } doAnswer { "result" } }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("result")
    }

    @Test
    fun `should stub function call with result from an answer instance`() {
        /* Given */
        /* Given */
        val answer = Answer { "result" }
        val mock = mock<SynchronousFunctions> { on { stringResult() } doAnswer answer }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("result")
    }

    @Test
    fun `should stub function call with result from lambda with argument`() {
        /* Given */
        val mock =
            mock<SynchronousFunctions> {
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
        val mock =
            mock<SynchronousFunctions> {
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
        val mock =
            mock<SynchronousFunctions> {
                on { varargBooleanResult(any(), any()) } doAnswer
                    { (a: String, b: String) ->
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
        val mock =
            mock<GenericMethods<Int>> { onGeneric({ genericMethod() }, Int::class) doReturn 2 }

        /* Then */
        expect(mock.genericMethod()).toBe(2)
    }

    @Test
    fun `should stub nullable generics function call with string result`() {
        /* Given */
        val mock = mock<GenericMethods<String>> { on { nullableReturnType() } doReturn "Test" }

        /* When */
        val result = mock.nullableReturnType()

        /* Then */
        expect(result).toBe("Test")
    }

    @Test
    fun `should stub suspendable generics function call with integer result`() {
        /* Given */
        val mock = mock<GenericMethods<Int>> { on { suspendableGenericMethod() } doReturn 2 }

        /* When */
        val result = runBlocking { mock.suspendableGenericMethod() }

        /* Then */
        expect(result).toBe(2)
    }

    @Test
    fun `should stub function call with value class result`() {
        /* Given */
        val valueClass = ValueClass("A")
        val mock = mock<SynchronousFunctions> { on { valueClassResult() } doReturn valueClass }

        /* When */
        val result: ValueClass = mock.valueClassResult()

        /* Then */
        expect(result).toBe(valueClass)
    }

    @Test
    fun `should stub function call with nullable value class result`() {
        /* Given */
        val valueClass = ValueClass("A")
        val mock =
            mock<SynchronousFunctions> { on { nullableValueClassResult() } doReturn valueClass }

        /* When */
        val result: ValueClass? = mock.nullableValueClassResult()

        /* Then */
        expect(result).toBe(valueClass)
    }

    @Test
    fun `should stub function call with nested value class result`() {
        /* Given */
        val nestedValueClass = NestedValueClass(ValueClass("A"))
        val mock =
            mock<SynchronousFunctions> { on { nestedValueClassResult() } doReturn nestedValueClass }

        /* When */
        val result: NestedValueClass = mock.nestedValueClassResult()

        /* Then */
        expect(result).toBe(nestedValueClass)
        expect(result.value).toBe(nestedValueClass.value)
    }

    @Test
    fun `should stub function call with long value class result`() {
        /* Given */
        val longValueClass = LongValueClass(42)
        val mock =
            mock<SynchronousFunctions> { on { longValueClassResult() } doReturn longValueClass }

        /* When */
        val result: LongValueClass = mock.longValueClassResult()

        /* Then */
        expect(result).toBe(longValueClass)
    }

    @Test
    fun `should stub function call with nullable long value class result`() {
        /* Given */
        val longValueClass = LongValueClass(42)
        val mock =
            mock<SynchronousFunctions> {
                on { nullableLongValueClassResult() } doReturn longValueClass
            }

        /* When */
        val result: LongValueClass? = mock.nullableLongValueClassResult()

        /* Then */
        expect(result).toBe(longValueClass)
    }

    @Test
    fun `should stub function call with boolean value class result`() {
        /* Given */
        val booleanValueClass = BooleanValueClass(true)
        val mock =
            mock<SynchronousFunctions> {
                on { booleanValueClassResult() } doReturn booleanValueClass
            }

        /* When */
        val result: BooleanValueClass = mock.booleanValueClassResult()

        /* Then */
        expect(result).toBe(booleanValueClass)
    }

    @Test
    fun `should stub function call with nullable boolean value class result`() {
        /* Given */
        val booleanValueClass = BooleanValueClass(false)
        val mock =
            mock<SynchronousFunctions> {
                on { nullableBooleanValueClassResult() } doReturn booleanValueClass
            }

        /* When */
        val result: BooleanValueClass? = mock.nullableBooleanValueClassResult()

        /* Then */
        expect(result).toBe(booleanValueClass)
    }

    @Test
    fun `should stub function call with char value class result`() {
        /* Given */
        val charValueClass = CharValueClass('a')
        val mock =
            mock<SynchronousFunctions> { on { charValueClassResult() } doReturn charValueClass }

        /* When */
        val result: CharValueClass = mock.charValueClassResult()

        /* Then */
        expect(result).toBe(charValueClass)
    }

    @Test
    fun `should stub function call with nullable char value class result`() {
        /* Given */
        val charValueClass = CharValueClass('a')
        val mock =
            mock<SynchronousFunctions> {
                on { nullableCharValueClassResult() } doReturn charValueClass
            }

        /* When */
        val result: CharValueClass? = mock.nullableCharValueClassResult()

        /* Then */
        expect(result).toBe(charValueClass)
    }

    @Test
    fun `should stub consecutive function calls with value class results`() {
        /* Given */
        val valueClassA = ValueClass("A")
        val valueClassB = ValueClass("B")
        val mock =
            mock<SynchronousFunctions> {
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
    fun `should stub function call to make real function call into mock`() {
        /* Given */
        val mock = mock<Open> { on { valueClassResult(any()) }.doCallRealMethod() }

        /* When */
        val result = mock.valueClassResult(ValueClass("Value"))

        /* Then */
        expect(result.content).toBe("Result: Value")
    }

    @Test
    fun `should stub mocked function object`() {
        /* Given */
        val mockFunction: (String) -> String = mock { on { invoke(any()) } doReturn "Text" }

        /* When */
        val result = mockFunction.invoke("")

        /* Then */
        expect(result).toBe("Text")
    }

    @Test
    fun doReturn_throwsNPE() {
        assumeFalse(mockMakerInlineEnabled())
        expectErrorWithMessage("look at the stack trace below") on
            {
                /* When */
                mock<Open> { on { throwsNPE() } doReturn "result" }
            }
    }

    @Test
    fun `should stub function call with doAnswer to return Result of integer`() {
        val successValue = 123
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<Int>() } doAnswer { Result.success(successValue) }
            }

        val result = mock.resultResult<Int>()

        expect(result.getOrNull()).toBe(successValue)
    }

    @Test
    fun `should stub function call with doAnswer to return Result of nullable integer`() {
        val successValue = 123 as Int?
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<Int?>() } doAnswer { Result.success(successValue) }
            }

        val result = mock.resultResult<Int?>()

        assertEquals(result.getOrNull(), successValue)
    }

    @Test
    fun `should stub function call with doAnswer to return Result of boolean`() {
        val successValue = true
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<Boolean>() } doAnswer { Result.success(successValue) }
            }

        val result = mock.resultResult<Boolean>()

        expect(result.getOrNull()).toBe(successValue)
    }

    @Test
    fun `should stub function call with doAnswer to return Result of nullable boolean`() {
        val successValue = true
        val mock =
            mock<SynchronousFunctions> {
                val nullableResult = Result.success(successValue as Boolean?)
                on { resultResult<Boolean?>() } doAnswer { nullableResult }
            }

        val result = mock.resultResult<Boolean?>()

        assertEquals(result.getOrNull(), successValue)
    }

    @Test
    fun `should stub function call with doAnswer to return Result of value class`() {
        val successValue = ValueClass("test")
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<ValueClass>() } doAnswer { Result.success(successValue) }
            }

        val result = mock.resultResult<ValueClass>()

        expect(result.getOrNull()).toBe(successValue)
    }

    @Test
    fun `should stub function call with doAnswer to return Result of nullable value class`() {
        val successValue = ValueClass("test") as ValueClass?
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<ValueClass?>() } doAnswer { Result.success(successValue) }
            }

        val result = mock.resultResult<ValueClass?>()

        assertEquals(result.getOrNull(), successValue)
    }

    @Test
    fun `should stub function call with doAnswer to return Result of long value class`() {
        val successValue = LongValueClass(123)
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<LongValueClass>() } doAnswer { Result.success(successValue) }
            }

        val result = mock.resultResult<LongValueClass>()

        expect(result.getOrNull()).toBe(successValue)
    }

    @Test
    fun `should stub function call with doAnswer to return Result of nullable long value class`() {
        val successValue = LongValueClass(123) as LongValueClass?
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<LongValueClass?>() } doAnswer { Result.success(successValue) }
            }

        val result = mock.resultResult<LongValueClass?>()

        assertEquals(result.getOrNull(), successValue)
    }

    @Test
    fun `should stub function call with doAnswer to return Result of object`() {
        val successValue = Open()
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<Open>() } doAnswer { Result.success(successValue) }
            }

        val result = mock.resultResult<Open>()

        expect(result.getOrNull()).toBe(successValue)
    }

    @Test
    fun `should stub function call with doAnswer to return Result of nullable object`() {
        val successValue = Open() as Open?
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<Open?>() } doAnswer { Result.success(successValue) }
            }

        val result = mock.resultResult<Open?>()

        assertEquals(result.getOrNull(), successValue)
    }

    @Test
    fun `should stub function call with doAnswer to return Result with null value`() {
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<Int?>() } doAnswer { Result.success(null as Int?) }
            }

        val result = mock.resultResult<Int?>()

        expect(result.getOrNull()).toBeNull()
    }

    @Test
    fun `should stub function call with doAnswer to return Result with failure`() {
        val exception = RuntimeException("deliberate")
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<Int>() } doAnswer { Result.failure(exception) }
            }

        val result = mock.resultResult<Int>()

        val actual = result.exceptionOrNull()
        expect(actual).toBe(exception)
    }

    @Test
    fun `should stub function call with doReturn to return Result of integer`() {
        val successValue = 123
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<Int>() } doReturn Result.success(successValue)
            }

        val result = mock.resultResult<Int>()

        expect(result.getOrNull()).toBe(successValue)
    }

    @Test
    fun `should stub function call with doReturn to return Result of nullable integer`() {
        val successValue = 123 as Int?
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<Int?>() } doReturn Result.success(successValue)
            }

        val result = mock.resultResult<Int?>()

        assertEquals(result.getOrNull(), successValue)
    }

    @Test
    fun `should stub function call with doReturn to return Result of boolean`() {
        val successValue = true
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<Boolean>() } doReturn Result.success(successValue)
            }

        val result = mock.resultResult<Boolean>()

        expect(result.getOrNull()).toBe(successValue)
    }

    @Test
    fun `should stub function call with doReturn to return Result of nullable boolean`() {
        val successValue = true
        val mock =
            mock<SynchronousFunctions> {
                val nullableResult = Result.success(successValue as Boolean?)
                on { resultResult<Boolean?>() } doReturn nullableResult
            }

        val result = mock.resultResult<Boolean?>()

        assertEquals(result.getOrNull(), successValue)
    }

    @Test
    fun `should stub function call with doReturn to return Result of value class`() {
        val successValue = ValueClass("test")
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<ValueClass>() } doReturn Result.success(successValue)
            }

        val result = mock.resultResult<ValueClass>()

        expect(result.getOrNull()).toBe(successValue)
    }

    @Test
    fun `should stub function call with doReturn to return Result of nullable value class`() {
        val successValue = ValueClass("test") as ValueClass?
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<ValueClass?>() } doReturn Result.success(successValue)
            }

        val result = mock.resultResult<ValueClass?>()

        assertEquals(result.getOrNull(), successValue)
    }

    @Test
    fun `should stub function call with doReturn to return Result of long value class`() {
        val successValue = LongValueClass(123)
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<LongValueClass>() } doReturn Result.success(successValue)
            }

        val result = mock.resultResult<LongValueClass>()

        expect(result.getOrNull()).toBe(successValue)
    }

    @Test
    fun `should stub function call with doReturn to return Result of nullable long value class`() {
        val successValue = LongValueClass(123) as LongValueClass?
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<LongValueClass?>() } doReturn Result.success(successValue)
            }

        val result = mock.resultResult<LongValueClass?>()

        assertEquals(result.getOrNull(), successValue)
    }

    @Test
    fun `should stub function call with doReturn to return Result of object`() {
        val successValue = Open()
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<Open>() } doReturn Result.success(successValue)
            }

        val result = mock.resultResult<Open>()

        expect(result.getOrNull()).toBe(successValue)
    }

    @Test
    fun `should stub function call with doReturn to return Result of nullable object`() {
        val successValue = Open() as Open?
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<Open?>() } doReturn Result.success(successValue)
            }

        val result = mock.resultResult<Open?>()

        assertEquals(result.getOrNull(), successValue)
    }

    @Test
    fun `should stub function call with doReturn to return Result with null value`() {
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<Int?>() } doReturn Result.success(null as Int?)
            }

        val result = mock.resultResult<Int?>()

        expect(result.getOrNull()).toBeNull()
    }

    @Test
    fun `should stub function call with doReturn to return Result with failure`() {
        val exception = RuntimeException("deliberate")
        val mock =
            mock<SynchronousFunctions> {
                on { resultResult<Int>() } doReturn Result.failure(exception)
            }

        val result = mock.resultResult<Int>()

        val actual = result.exceptionOrNull()
        expect(actual).toBe(exception)
    }
}
