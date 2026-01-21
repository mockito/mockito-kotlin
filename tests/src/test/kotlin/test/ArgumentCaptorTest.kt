package test

import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import java.util.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.mock
import org.mockito.kotlin.nullableArgumentCaptor
import org.mockito.kotlin.suspendFunctionArgumentCaptor
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ArgumentCaptorTest : TestBase() {

    @Test
    fun argumentCaptor_withSingleValue() {
        /* Given */
        val date: Date = mock()

        /* When */
        date.time = 5L

        /* Then */
        val captor = argumentCaptor<Long>()
        verify(date).time = captor.capture()
        expect(captor.lastValue).toBe(5L)
    }

    @Test
    fun argumentCaptor_destructuring2() {
        /* Given */
        val date: Date = mock()

        /* When */
        date.time = 5L

        /* Then */
        val (captor1, captor2) = argumentCaptor<Long, Long>()
        verify(date).time = captor1.capture()
        verify(date).time = captor2.capture()
        expect(captor1.lastValue).toBe(5L)
        expect(captor2.lastValue).toBe(5L)
    }

    @Test
    fun argumentCaptor_destructuring3() {
        /* Given */
        val date: Date = mock()

        /* When */
        date.time = 5L

        /* Then */
        val (captor1, captor2, captor3) = argumentCaptor<Long, Long, Long>()
        val verifyCaptor: KArgumentCaptor<Long>.() -> Unit = {
            verify(date).time = capture()
            expect(lastValue).toBe(5L)
        }
        captor1.apply(verifyCaptor)
        captor2.apply(verifyCaptor)
        captor3.apply(verifyCaptor)
    }

    @Test
    fun argumentCaptor_destructuring4() {
        /* Given */
        val date: Date = mock()

        /* When */
        date.time = 5L

        /* Then */
        val (captor1, captor2, captor3, captor4) = argumentCaptor<Long, Long, Long, Long>()
        val verifyCaptor: KArgumentCaptor<Long>.() -> Unit = {
            verify(date).time = capture()
            expect(lastValue).toBe(5L)
        }
        captor1.apply(verifyCaptor)
        captor2.apply(verifyCaptor)
        captor3.apply(verifyCaptor)
        captor4.apply(verifyCaptor)
    }

    @Test
    fun argumentCaptor_destructuring5() {
        /* Given */
        val date: Date = mock()

        /* When */
        date.time = 5L

        /* Then */
        val (captor1, captor2, captor3, captor4, captor5) =
            argumentCaptor<Long, Long, Long, Long, Long>()
        val verifyCaptor: KArgumentCaptor<Long>.() -> Unit = {
            verify(date).time = capture()
            expect(lastValue).toBe(5L)
        }
        captor1.apply(verifyCaptor)
        captor2.apply(verifyCaptor)
        captor3.apply(verifyCaptor)
        captor4.apply(verifyCaptor)
        captor5.apply(verifyCaptor)
    }

    @Test
    fun argumentCaptor_withNullValue_usingNonNullable() {
        /* Given */
        val mock: SynchronousFunctions = mock()

        /* When */
        mock.nullableString(null)

        /* Then */
        val captor = argumentCaptor<String>()
        verify(mock).nullableString(captor.capture())
        expect(captor.lastValue).toBeNull()
    }

    @Test
    fun argumentCaptor_withNullValue_usingNullable() {
        /* Given */
        val mock: SynchronousFunctions = mock()

        /* When */
        mock.nullableString(null)

        /* Then */
        val captor = nullableArgumentCaptor<String>()
        verify(mock).nullableString(captor.capture())
        expect(captor.lastValue).toBeNull()
    }

    @Test
    fun argumentCaptor_singleValue() {
        /* Given */
        val date: Date = mock()

        /* When */
        date.time = 5L

        /* Then */
        val captor = argumentCaptor<Long>()
        verify(date).time = captor.capture()
        expect(captor.singleValue).toBe(5L)
    }

    @Test(expected = IllegalArgumentException::class)
    fun argumentCaptor_singleValue_properlyFails() {
        /* Given */
        val date: Date = mock()
        val captor = argumentCaptor<Long>()
        doNothing().whenever(date).time = captor.capture()

        /* When */
        date.time = 5L
        date.time = 7L

        /* Then */
        expect(captor.singleValue).toBe(5)
    }

    @Test
    fun argumentCaptor_multipleValues() {
        /* Given */
        val date: Date = mock()

        /* When */
        date.time = 5L
        date.time = 7L

        /* Then */
        val captor = argumentCaptor<Long>()
        verify(date, times(2)).time = captor.capture()
        expect(captor.allValues).toBe(listOf(5, 7))
    }

    @Test
    fun argumentCaptor_multipleValuesIncludingNull() {
        /* Given */
        val mock: SynchronousFunctions = mock()

        /* When */
        mock.nullableString("test")
        mock.nullableString(null)

        /* Then */
        val captor = nullableArgumentCaptor<String>()
        verify(mock, times(2)).nullableString(captor.capture())
        expect(captor.allValues).toBe(listOf("test", null))
    }

    @Test
    fun argumentCaptor_callProperties() {
        /* Given */
        val mock: SynchronousFunctions = mock()

        /* When */
        mock.int(1)
        mock.int(2)
        mock.int(3)
        mock.int(4)
        mock.int(5)

        /* Then */
        argumentCaptor<Int>().apply {
            verify(mock, times(5)).int(capture())

            expect(firstValue).toBe(1)
            expect(secondValue).toBe(2)
            expect(thirdValue).toBe(3)
            expect(lastValue).toBe(5)
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun argumentCaptor_callPropertyNotAvailable() {
        /* Given */
        val mock: SynchronousFunctions = mock()

        /* When */
        mock.int(1)

        /* Then */
        argumentCaptor<Int>().apply {
            verify(mock).int(capture())

            expect(secondValue).toBe(2)
        }
    }

    @Test
    fun argumentCaptor_withSingleValue_lambda() {
        /* Given */
        val date: Date = mock()

        /* When */
        date.time = 5L

        /* Then */
        argumentCaptor<Long> {
            verify(date).time = capture()
            expect(lastValue).toBe(5L)
        }
    }

    @Test
    fun argumentCaptor_withSingleValue_lambda_properlyFails() {
        /* Given */
        val date: Date = mock()

        /* When */
        date.time = 5L

        /* Then */
        expectErrorWithMessage("Expected: 3 but was: 5") on
            {
                argumentCaptor<Long> {
                    verify(date).time = capture()
                    expect(lastValue).toBe(3L)
                }
            }
    }

    @Test
    fun argumentCaptor_vararg() {
        /* Given */
        val mock: SynchronousFunctions = mock()

        /* When */
        mock.varargBooleanResult("a", "b", "c")

        /* Then */
        val captor = argumentCaptor<Array<String>>()
        verify(mock).varargBooleanResult(*captor.capture())
        expect(captor.firstValue.toList()).toBe(listOf("a", "b", "c"))
    }

    @Test
    fun argumentCaptor_empty_vararg() {
        /* Given */
        val mock: SynchronousFunctions = mock()

        /* When */
        mock.varargBooleanResult()

        /* Then */
        val captor = argumentCaptor<Array<String>>()
        verify(mock).varargBooleanResult(*captor.capture())
        expect(captor.firstValue.toList()).toBe(listOf())
    }

    @Test
    fun argumentCaptor_arg_vararg() {
        /* Given */
        val mock: SynchronousFunctions = mock()

        /* When */
        mock.argAndVararg("first", "a", "b", "c")

        /* Then */
        val captor = argumentCaptor<Array<String>>()
        verify(mock).argAndVararg(any(), *captor.capture())
        expect(captor.firstValue.toList()).toBe(listOf("a", "b", "c"))
    }

    @Test
    fun argumentCaptor_intarray() {
        /* Given */
        val mock: SynchronousFunctions = mock()

        /* When */
        mock.intArray(intArrayOf(1, 2, 3))

        /* Then */
        val captor = argumentCaptor<IntArray>()
        verify(mock).intArray(captor.capture())
        expect(captor.firstValue.toList()).toBe(listOf(1, 2, 3))
    }

    @Test
    fun argumentCaptor_array() {
        /* Given */
        val mock: SynchronousFunctions = mock()

        /* When */
        mock.stringArray(arrayOf("a", "b", "c"))

        /* Then */
        val captor = argumentCaptor<Array<String>>()
        verify(mock).stringArray(captor.capture())
        expect(captor.firstValue.toList()).toBe(listOf("a", "b", "c"))
    }

    @Test
    fun argumentCaptor_empty_array() {
        /* Given */
        val mock: SynchronousFunctions = mock()

        /* When */
        mock.stringArray(arrayOf())

        /* Then */
        val captor = argumentCaptor<Array<String>>()
        verify(mock).stringArray(captor.capture())
        expect(captor.firstValue.toList()).toBe(listOf())
    }

    @Test
    fun argumentCaptor_value_class() {
        /* Given */
        val mock: SynchronousFunctions = mock()
        val valueClass = ValueClass("Content")

        /* When */
        mock.valueClass(valueClass)

        /* Then */
        val captor = argumentCaptor<ValueClass>()
        verify(mock).valueClass(captor.capture())
        expect(captor.firstValue).toBe(valueClass)
    }

    @Test
    fun argumentCaptor_value_class_withNullValue_usingNonNullable() {
        /* Given */
        val mock: SynchronousFunctions = mock()

        /* When */
        mock.nullableValueClass(null)

        /* Then */
        val captor = argumentCaptor<ValueClass>()
        verify(mock).nullableValueClass(captor.capture())
        expect(captor.firstValue).toBeNull()
    }

    @Test
    fun argumentCaptor_value_class_withNullValue_usingNullable() {
        /* Given */
        val mock: SynchronousFunctions = mock()

        /* When */
        mock.nullableValueClass(null)

        /* Then */
        val captor = nullableArgumentCaptor<ValueClass>()
        verify(mock).nullableValueClass(captor.capture())
        expect(captor.firstValue).toBeNull()
    }

    @Test
    fun argumentCaptor_long_value_class() {
        /* Given */
        val mock: SynchronousFunctions = mock()
        val valueClass = LongValueClass(123)

        /* When */
        mock.longValueClass(valueClass)

        /* Then */
        val captor = argumentCaptor<LongValueClass>()
        verify(mock).longValueClass(captor.capture())
        expect(captor.firstValue).toBe(valueClass)
    }

    @Test
    fun argumentCaptor_nullable_long_value_class() {
        /* Given */
        val mock: SynchronousFunctions = mock()
        val valueClass = LongValueClass(123)

        /* When */
        mock.nullableLongValueClass(valueClass)

        /* Then */
        val captor = argumentCaptor<LongValueClass?>()
        verify(mock).nullableLongValueClass(captor.capture())
        expect(captor.firstValue).toBe(valueClass)
    }

    @Test
    fun argumentCaptor_function() {
        /* Given */
        var counter = 0
        val mock: SynchronousFunctions = mock()
        val function: () -> Unit = { counter++ }

        /* When */
        mock.functionArgument(function)

        /* Then */
        val captor = argumentCaptor<() -> Unit>()
        verify(mock).functionArgument(captor.capture())
        captor.firstValue.invoke()
        expect(counter).toBe(1)
    }

    @Test
    fun argumentCaptor_suspend_function() {
        /* Given */
        var counter = 0
        val mock: SynchronousFunctions = mock()
        val function: suspend () -> Unit = suspend { counter++ }

        /* When */
        mock.suspendFunctionArgument(function)

        /* Then */
        val captor = suspendFunctionArgumentCaptor<suspend () -> Unit>()
        verify(mock).suspendFunctionArgument(captor.capture())
        runBlocking { captor.firstValue.invoke() }
        expect(counter).toBe(1)
    }

    @Test
    fun argumentCaptor_Result_Unit() {
        /* Given */
        val mock: SynchronousFunctions = mock()
        val result = Result.success(Unit)

        /* When */
        mock.resultArgument(result)

        /* Then */
        val captor = argumentCaptor<Result<Unit>>()
        verify(mock).resultArgument(captor.capture())
        expect(captor.firstValue).toBe(result)
    }

    @Test
    fun argumentCaptor_Result_ValueClass() {
        /* Given */
        val mock: SynchronousFunctions = mock()
        val result = Result.success(ValueClass("test"))

        /* When */
        mock.resultArgument(result)

        /* Then */
        val captor = argumentCaptor<Result<ValueClass>>()
        verify(mock).resultArgument(captor.capture())
        expect(captor.firstValue).toBe(result)
    }

    @Test
    fun argumentCaptor_Result_LongValueClass() {
        /* Given */
        val mock: SynchronousFunctions = mock()
        val result = Result.success(LongValueClass(123))

        /* When */
        mock.resultArgument(result)

        /* Then */
        val captor = argumentCaptor<Result<LongValueClass>>()
        verify(mock).resultArgument(captor.capture())
        expect(captor.firstValue).toBe(result)
    }
}
