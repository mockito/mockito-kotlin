package test

import com.nhaarman.expect.expect
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.*
import org.mockito.stubbing.Answer

class CoroutinesOngoingStubbingTest {
    @Test
    fun `should stub suspendable function call`() {
        /* Given */
        val mock = mock<SuspendFunctions> { on { stringResult() } doReturn "A" }

        /* When */
        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub suspendable function call within a coroutine scope`() = runTest {
        /* Given */
        val mock = mock<SuspendFunctions> { on { stringResult() } doReturn "A" }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should stub consecutive suspendable function calls`() {
        /* Given */
        val mock = mock<SuspendFunctions> { on { stringResult() }.doReturn("A", "B", "C") }

        /* When */
        val result = runBlocking { (1..3).map { _ -> mock.stringResult() } }

        /* Then */
        expect(result).toBe(listOf("A", "B", "C"))
    }

    @Test
    fun `should stub consecutive suspendable function calls by a list of answers`() {
        /* Given */
        val mock =
            mock<SuspendFunctions> {
                on { stringResult() } doReturnConsecutively listOf("A", "B", "C")
            }

        /* When */
        val result = runBlocking { (1..3).map { _ -> mock.stringResult() } }

        /* Then */
        expect(result).toBe(listOf("A", "B", "C"))
    }

    @Ignore(
        "Default answers do not yet work for coroutines, see https://github.com/mockito/mockito-kotlin/issues/550"
    )
    @Test
    fun `should stub builder method returning mock itself via defaultAnswer`() {
        /* Given */
        val mock = mock<SuspendFunctions>(defaultAnswer = Mockito.RETURNS_SELF)

        /* When */
        val result = runBlocking { mock.builderMethod() }

        /* Then */
        expect(result).toBeTheSameAs(mock)
    }

    @Ignore(
        "Default answers do not yet work for coroutines, see https://github.com/mockito/mockito-kotlin/issues/550"
    )
    @Test
    fun `should stub builder method returning mock itself via answer`() {
        /* Given */
        val mock = mock<SuspendFunctions> { on { builderMethod() } doAnswer Mockito.RETURNS_SELF }

        /* When */
        val result = runBlocking { mock.builderMethod() }

        /* Then */
        expect(result).toBeTheSameAs(mock)
    }

    @Test
    fun `should stub builder method returning mock itself`() {
        /* Given */
        val mock = mock<SuspendFunctions> { mock -> on { builderMethod() } doReturn mock }

        /* When */
        val result = runBlocking { mock.builderMethod() }

        /* Then */
        expect(result).toBe(mock)
    }

    @Test
    fun `should stub suspendable function call with nullable result`() {
        /* Given */
        val mock = mock<SuspendFunctions> { on { nullableStringResult() } doReturn "Test" }

        /* When */
        val result = runBlocking { mock.nullableStringResult() }

        /* Then */
        expect(result).toBe("Test")
    }

    @Test
    fun `should throw exception instance on suspendable function call`() {
        /* Given */
        val mock =
            mock<SuspendFunctions> { on { builderMethod() } doThrow IllegalArgumentException() }

        /* When, Then */
        runBlocking { assertThrows<IllegalArgumentException> { mock.builderMethod() } }
    }

    @Test
    fun `should throw exception class on suspendable function call`() {
        /* Given */
        val mock =
            mock<SuspendFunctions> {
                on { builderMethod() } doThrow IllegalArgumentException::class
            }

        /* When, Then */
        runBlocking { assertThrows<IllegalArgumentException> { mock.builderMethod() } }
    }

    @Test
    fun `should throw exception instances on consecutive suspendable function calls`() {
        /* Given */
        val mock =
            mock<SuspendFunctions> {
                on { builderMethod() }
                    .doThrow(IllegalArgumentException(), UnsupportedOperationException())
            }

        /* When, Then */
        runBlocking {
            assertThrows<IllegalArgumentException> { mock.builderMethod() }
            assertThrows<UnsupportedOperationException> { mock.builderMethod() }
        }
    }

    @Test
    fun `should throw exception classes on consecutive suspendable function calls`() {
        /* Given */
        val mock =
            mock<SuspendFunctions> {
                on { builderMethod() }
                    .doThrow(IllegalArgumentException::class, UnsupportedOperationException::class)
            }

        /* When, Then */
        runBlocking {
            assertThrows<IllegalArgumentException> { mock.builderMethod() }
            assertThrows<UnsupportedOperationException> { mock.builderMethod() }
        }
    }

    @Test
    fun `should stub suspendable function call with result from answer instance`() {
        /* Given */
        val answer: Answer<String> = Answer { "result" }
        val mock = mock<SuspendFunctions> { on { stringResult() } doAnswer answer }

        /* When */

        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("result")
    }

    @Test
    fun `should stub suspendable function call with result from lambda`() {
        /* Given */
        val mock = mock<SuspendFunctions> { on { stringResult() } doAnswer { "result" } }

        /* When */
        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("result")
    }

    @Test
    fun `should stub suspendable function call with result from lambda with argument`() {
        /* Given */
        val mock =
            mock<SuspendFunctions> {
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
        val mock =
            mock<SuspendFunctions> {
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
        val mock =
            mock<SuspendFunctions> {
                on { stringResult(any(), any()) } doAnswer { (a: String, b: String) -> "$a + $b" }
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
        val mock = mock<SuspendFunctions> { on(mock.valueClassResult()) doReturn valueClass }

        /* When */
        val result: ValueClass = mock.valueClassResult()

        /* Then */
        expect(result).toBe(valueClass)
    }

    @Test
    fun `should stub suspendable function call with nullable value class result`() {
        /* Given */
        val valueClass = ValueClass("A")
        val mock = mock<SuspendFunctions> { on { nullableValueClassResult() } doReturn valueClass }

        /* When */
        val result: ValueClass? = runBlocking { mock.nullableValueClassResult() }

        /* Then */
        expect(result).toBe(valueClass)
    }

    @Test
    fun `should stub suspendable function call with nested value class result`() {
        /* Given */
        val nestedValueClass = NestedValueClass(ValueClass("A"))
        val mock =
            mock<SuspendFunctions> { on { nestedValueClassResult() } doReturn nestedValueClass }

        /* When */
        val result: NestedValueClass = runBlocking { mock.nestedValueClassResult() }

        /* Then */
        expect(result).toBe(nestedValueClass)
        expect(result.value).toBe(nestedValueClass.value)
    }

    @Test
    fun `should stub suspendable function call with long value class result`() = runTest {
        /* Given */
        val longValueClass = LongValueClass(42)
        val mock =
            mock<SuspendFunctions> { on(mock.longValueClassResult()) doReturn longValueClass }

        /* When */
        val result: LongValueClass = mock.longValueClassResult()

        /* Then */
        expect(result).toBe(longValueClass)
    }

    @Test
    fun `should stub suspendable function call with nullable long value class result`() = runTest {
        /* Given */
        val longValueClass = LongValueClass(42) as LongValueClass?
        val mock =
            mock<SuspendFunctions> {
                on(mock.nullableLongValueClassResult()) doReturn longValueClass
            }

        /* When */
        val result: LongValueClass? = mock.nullableLongValueClassResult()

        /* Then */
        // expect(result).toBe(longValueClass) // expect does not deal well with nullable
        // expected value
        assertEquals(longValueClass, result)
    }

    @Test
    fun `should stub suspendable function call with boolean value class result`() = runTest {
        /* Given */
        val booleanValueClass = BooleanValueClass(true)
        val mock =
            mock<SuspendFunctions> { on(mock.booleanValueClassResult()) doReturn booleanValueClass }

        /* When */
        val result: BooleanValueClass = mock.booleanValueClassResult()

        /* Then */
        expect(result).toBe(booleanValueClass)
    }

    @Test
    fun `should stub suspendable function call with nullable boolean value class result`() =
        runTest {
            /* Given */
            val booleanValueClass = BooleanValueClass(false) as BooleanValueClass?
            val mock =
                mock<SuspendFunctions> {
                    on(mock.nullableBooleanValueClassResult()) doReturn booleanValueClass
                }

            /* When */
            val result: BooleanValueClass? = mock.nullableBooleanValueClassResult()

            /* Then */
            assertEquals(booleanValueClass, result)
        }

    @Test
    fun `should stub suspendable function call with char value class result`() = runTest {
        /* Given */
        val charValueClass = CharValueClass('a')
        val mock =
            mock<SuspendFunctions> { on(mock.charValueClassResult()) doReturn charValueClass }

        /* When */
        val result: CharValueClass = mock.charValueClassResult()

        /* Then */
        expect(result).toBe(charValueClass)
    }

    @Test
    fun `should stub suspendable function call with nullable char value class result`() = runTest {
        /* Given */
        val charValueClass = CharValueClass('a') as CharValueClass?
        val mock =
            mock<SuspendFunctions> {
                on(mock.nullableCharValueClassResult()) doReturn charValueClass
            }

        /* When */
        val result: CharValueClass? = mock.nullableCharValueClassResult()

        /* Then */
        assertEquals(charValueClass, result)
    }

    @Test
    fun `should stub consecutive suspendable function call with value class results`() {
        /* Given */
        val valueClassA = ValueClass("A")
        val valueClassB = ValueClass("B")
        val mock =
            mock<SuspendFunctions> {
                on { valueClassResult() }.doReturnConsecutively(valueClassA, valueClassB)
            }

        /* When */
        val (result1, result2) = runBlocking { mock.valueClassResult() to mock.valueClassResult() }

        /* Then */
        expect(result1).toBe(valueClassA)
        expect(result2).toBe(valueClassB)
    }

    @Test
    fun `should stub suspendable function call to make real function call into mock`() {
        /* Given */
        val mock = mock<Open> { on { suspendValueClassResult(any()) }.doCallRealMethod() }

        /* When */
        val result: ValueClass = runBlocking {
            mock.suspendValueClassResult { ValueClass("Value") }
        }

        /* Then */
        expect(result.content).toBe("Result: Value")
    }

    @Test
    fun `should stub suspendable function call with doSuspendableAnswer to return Result of integer`() =
        runTest {
            val successValue = 123
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<Int>() } doSuspendableAnswer { Result.success(successValue) }
                }

            val result = mock.resultResult<Int>()

            expect(result.getOrNull()).toBe(successValue)
        }

    @Test
    fun `should stub suspendable function call with doSuspendableAnswer to return Result of nullable integer`() =
        runTest {
            val successValue = 123 as Int?
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<Int?>() } doSuspendableAnswer { Result.success(successValue) }
                }

            val result = mock.resultResult<Int?>()

            assertEquals(result.getOrNull(), successValue)
        }

    @Test
    fun `should stub suspendable function call with doSuspendableAnswer to return Result of boolean`() =
        runTest {
            val successValue = true
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<Boolean>() } doSuspendableAnswer
                        {
                            Result.success(successValue)
                        }
                }

            val result = mock.resultResult<Boolean>()

            expect(result.getOrNull()).toBe(successValue)
        }

    @Test
    fun `should stub suspendable function call with doSuspendableAnswer to return Result of nullable boolean`() =
        runTest {
            val successValue = true
            val mock =
                mock<SuspendFunctions> {
                    val nullableResult = Result.success(successValue as Boolean?)
                    on { resultResult<Boolean?>() } doSuspendableAnswer { nullableResult }
                }

            val result = mock.resultResult<Boolean?>()

            assertEquals(result.getOrNull(), successValue)
        }

    @Test
    fun `should stub suspendable function call with doSuspendableAnswer to return Result of value class`() =
        runTest {
            val successValue = ValueClass("test")
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<ValueClass>() } doSuspendableAnswer
                        {
                            Result.success(successValue)
                        }
                }

            val result = mock.resultResult<ValueClass>()

            expect(result.getOrNull()).toBe(successValue)
        }

    @Test
    fun `should stub suspendable function call with doSuspendableAnswer to return Result of nullable value class`() =
        runTest {
            val successValue = ValueClass("test") as ValueClass?
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<ValueClass?>() } doSuspendableAnswer
                        {
                            Result.success(successValue)
                        }
                }

            val result = mock.resultResult<ValueClass?>()

            assertEquals(result.getOrNull(), successValue)
        }

    @Test
    fun `should stub suspendable function call with doSuspendableAnswer to return Result of long value class`() =
        runTest {
            val successValue = LongValueClass(123)
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<LongValueClass>() } doSuspendableAnswer
                        {
                            Result.success(successValue)
                        }
                }

            val result = mock.resultResult<LongValueClass>()

            expect(result.getOrNull()).toBe(successValue)
        }

    @Test
    fun `should stub suspendable function call with doSuspendableAnswer to return Result of nullable long value class`() =
        runTest {
            val successValue = LongValueClass(123) as LongValueClass?
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<LongValueClass?>() } doSuspendableAnswer
                        {
                            Result.success(successValue)
                        }
                }

            val result = mock.resultResult<LongValueClass?>()

            assertEquals(result.getOrNull(), successValue)
        }

    @Test
    fun `should stub suspendable function call with doSuspendableAnswer to return Result of object`() =
        runTest {
            val successValue = Open()
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<Open>() } doSuspendableAnswer { Result.success(successValue) }
                }

            val result = mock.resultResult<Open>()

            expect(result.getOrNull()).toBe(successValue)
        }

    @Test
    fun `should stub suspendable function call with doSuspendableAnswer to return Result of nullable object`() =
        runTest {
            val successValue = Open() as Open?
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<Open?>() } doSuspendableAnswer
                        {
                            Result.success(successValue)
                        }
                }

            val result = mock.resultResult<Open?>()

            assertEquals(result.getOrNull(), successValue)
        }

    @Test
    fun `should stub suspendable function call with doSuspendableAnswer to return Result with null value`() =
        runTest {
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<Int?>() } doSuspendableAnswer
                          {
                              Result.success(null as Int?)
                          }
                }

            val result = mock.resultResult<Int?>()

            expect(result.getOrNull()).toBeNull()
        }

    @Test
    fun `should stub suspendable function call with doReturn to return Result of integer`() =
        runTest {
            val successValue = 123
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<Int>() } doReturn Result.success(successValue)
                }

            val result = mock.resultResult<Int>()

            expect(result.getOrNull()).toBe(successValue)
        }

    @Test
    fun `should stub suspendable function call with doReturn to return Result of nullable integer`() =
        runTest {
            val successValue = 123 as Int?
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<Int?>() } doReturn Result.success(successValue)
                }

            val result = mock.resultResult<Int?>()

            assertEquals(result.getOrNull(), successValue)
        }

    @Test
    fun `should stub suspendable function call with doReturn to return Result of boolean`() =
        runTest {
            val successValue = true
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<Boolean>() } doReturn Result.success(successValue)
                }

            val result = mock.resultResult<Boolean>()

            expect(result.getOrNull()).toBe(successValue)
        }

    @Test
    fun `should stub suspendable function call with doReturn to return Result of nullable boolean`() =
        runTest {
            val successValue = true
            val mock =
                mock<SuspendFunctions> {
                    val nullableResult = Result.success(successValue as Boolean?)
                    on { resultResult<Boolean?>() } doReturn nullableResult
                }

            val result = mock.resultResult<Boolean?>()

            assertEquals(result.getOrNull(), successValue)
        }

    @Test
    fun `should stub suspendable function call with doReturn to return Result of value class`() =
        runTest {
            val successValue = ValueClass("test")
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<ValueClass>() } doReturn Result.success(successValue)
                }

            val result = mock.resultResult<ValueClass>()

            expect(result.getOrNull()).toBe(successValue)
        }

    @Test
    fun `should stub suspendable function call with doReturn to return Result of nullable value class`() =
        runTest {
            val successValue = ValueClass("test") as ValueClass?
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<ValueClass?>() } doReturn Result.success(successValue)
                }

            val result = mock.resultResult<ValueClass?>()

            assertEquals(result.getOrNull(), successValue)
        }

    @Test
    fun `should stub suspendable function call with doReturn to return Result of long value class`() =
        runTest {
            val successValue = LongValueClass(123)
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<LongValueClass>() } doReturn Result.success(successValue)
                }

            val result = mock.resultResult<LongValueClass>()

            expect(result.getOrNull()).toBe(successValue)
        }

    @Test
    fun `should stub suspendable function call with doReturn to return Result of nullable long value class`() =
        runTest {
            val successValue = LongValueClass(123) as LongValueClass?
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<LongValueClass?>() } doReturn Result.success(successValue)
                }

            val result = mock.resultResult<LongValueClass?>()

            assertEquals(result.getOrNull(), successValue)
        }

    @Test
    fun `should stub suspendable function call with doReturn to return Result of object`() =
        runTest {
            val successValue = Open()
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<Open>() } doReturn Result.success(successValue)
                }

            val result = mock.resultResult<Open>()

            expect(result.getOrNull()).toBe(successValue)
        }

    @Test
    fun `should stub suspendable function call with doReturn to return Result of nullable object`() =
        runTest {
            val successValue = Open() as Open?
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<Open?>() } doReturn Result.success(successValue)
                }

            val result = mock.resultResult<Open?>()

            assertEquals(result.getOrNull(), successValue)
        }

    @Test
    fun `should stub suspendable function call with doReturn to return Result with null value`() =
        runTest {
            val mock =
                mock<SuspendFunctions> {
                    on { resultResult<Int?>() } doReturn Result.success(null as Int?)

                }

            val result = mock.resultResult<Int?>()

            expect(result.getOrNull()).toBeNull()
        }
}
