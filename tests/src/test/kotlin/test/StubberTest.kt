package test

import com.nhaarman.expect.expect
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doCallRealMethod
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever

class StubberTest : TestBase() {

    @Test
    fun `should stub function call with result from lambda`() {
        val mock = mock<SynchronousFunctions>()

        doAnswer { "Test" }.whenever(mock).stringResult()

        expect(mock.stringResult()).toBe("Test")
    }

    @Test
    fun `should stub function call to call real method implementation`() {
        val mock = mock<Open>()

        doReturn("Test").whenever(mock).stringResult()
        doCallRealMethod().whenever(mock).stringResult()

        expect(mock.stringResult()).toBe("Default")
    }

    @Test
    fun `should stub function call to do nothing in the spied instance`() {
        val spy = spy(Open())
        val array = intArrayOf(3)

        doNothing().whenever(spy).modifiesContents(array)
        spy.modifiesContents(array)

        expect(array[0]).toBe(3)
    }

    @Test
    fun `should stub function call with fixed return value`() {
        val mock = mock<SynchronousFunctions>()

        doReturn("test").whenever(mock).stringResult()

        expect(mock.stringResult()).toBe("test")
    }

    @Test
    fun `should stub function call with consecutive fixed return values`() {
        val mock = mock<SynchronousFunctions>()

        doReturn("test", "test2").whenever(mock).stringResult()

        expect(mock.stringResult()).toBe("test")
        expect(mock.stringResult()).toBe("test2")
    }

    @Test
    fun `should stub function call with null return value`() {
        val mock = mock<SynchronousFunctions>()

        doReturn(null).whenever(mock).stringResult()

        expect(mock.stringResult()).toBeNull()
    }

    @Test
    fun `should stub function call with consecutive null return values`() {
        val mock = mock<SynchronousFunctions>()

        doReturn(null, null).whenever(mock).stringResult()

        expect(mock.stringResult()).toBeNull()
        expect(mock.stringResult()).toBeNull()
    }

    @Test
    fun `should stub function call to throw exception instance`() {
        val mock = mock<Open>()

        doThrow(IllegalStateException("test")).whenever(mock).go()

        val exception: IllegalStateException = assertThrows { mock.go() }
        assertEquals("test", exception.message)
        // any consecutive call should throw the last specified exception
        assertThrows<IllegalStateException> { mock.go() }
    }

    @Test
    fun `should stub function call to throw exception instances consecutively`() {
        val mock = mock<Open>()

        doThrow(IllegalStateException("test"), NullPointerException("test2")).whenever(mock).go()

        val first: IllegalStateException = assertThrows { mock.go() }
        assertEquals("test", first.message)
        val second: NullPointerException = assertThrows { mock.go() }
        assertEquals("test2", second.message)
        // any consecutive call should throw the last specified exception
        assertThrows<NullPointerException> { mock.go() }
    }

    @Test
    fun `should stub function call to throw exception class`() {
        val mock = mock<Open>()

        doThrow(IllegalStateException::class).whenever(mock).go()

        assertThrows<IllegalStateException> { mock.go() }
        // any consecutive call should throw the last specified exception
        assertThrows<IllegalStateException> { mock.go() }
    }

    @Test
    fun `should stub function call to throw exception classes consecutively`() {
        val mock = mock<Open>()

        doThrow(IllegalStateException::class, NullPointerException::class).whenever(mock).go()

        assertThrows<IllegalStateException> { mock.go() }
        assertThrows<NullPointerException> { mock.go() }
        // any consecutive call should throw the last specified exception
        assertThrows<NullPointerException> { mock.go() }
    }

    @Test
    fun `should stub suspendable function call in reverse manner, with on() as part of mock creation`() =
        runTest {
            /* Given */
            val mock = mock<SuspendFunctions> { doReturn("A") on { stringResult() } }

            /* When */
            val result = runBlocking { mock.stringResult() }

            /* Then */
            expect(result).toBe("A")
        }

    @Test
    fun `should stub synchronous function call in reverse manner, with on() as part of mock creation`() =
        runTest {
            val mock = mock<SynchronousFunctions> { doReturn("Test") on { stringResult() } }

            expect(mock.stringResult()).toBe("Test")
        }

    @Test
    fun `should stub suspendable function call with doReturn to return long value class`() =
        runTest {
            val longValueClass = LongValueClass(123)
            val mock =
                mock<SuspendFunctions> { doReturn(longValueClass) on { longValueClassResult() } }

            val result = mock.longValueClassResult()

            expect(result).toBe(longValueClass)
        }

    @Test
    fun `should stub suspendable function call with doSuspendableAnswer to return Result of integer`() =
        runTest {
            val successValue = 123
            val mock =
                mock<SuspendFunctions> {
                    doSuspendableAnswer { Result.success(successValue) } on { resultResult<Int>() }
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
                    doSuspendableAnswer { Result.success(successValue) } on { resultResult<Int?>() }
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
                    doSuspendableAnswer { Result.success(successValue) } on
                        {
                            resultResult<Boolean>()
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
                    doSuspendableAnswer { nullableResult } on { resultResult<Boolean?>() }
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
                    doSuspendableAnswer { Result.success(successValue) } on
                        {
                            resultResult<ValueClass>()
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
                    doSuspendableAnswer { Result.success(successValue) } on
                        {
                            resultResult<ValueClass?>()
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
                    doSuspendableAnswer { Result.success(successValue) } on
                        {
                            resultResult<LongValueClass>()
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
                    doSuspendableAnswer { Result.success(successValue) } on
                        {
                            resultResult<LongValueClass?>()
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
                    doSuspendableAnswer { Result.success(successValue) } on { resultResult<Open>() }
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
                    doSuspendableAnswer { Result.success(successValue) } on
                        {
                            resultResult<Open?>()
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
                    doSuspendableAnswer { Result.success(null as Int?) } on { resultResult<Int?>() }
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
                    doReturn(Result.success(successValue)) on { resultResult<Int>() }
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
                    doReturn(Result.success(successValue)) on { resultResult<Int?>() }
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
                    doReturn(Result.success(successValue)) on { resultResult<Boolean>() }
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
                    doReturn(nullableResult) on { resultResult<Boolean?>() }
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
                    doReturn(Result.success(successValue)) on { resultResult<ValueClass>() }
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
                    doReturn(Result.success(successValue)) on { resultResult<ValueClass?>() }
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
                    doReturn(Result.success(successValue)) on { resultResult<LongValueClass>() }
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
                    doReturn(Result.success(successValue)) on { resultResult<LongValueClass?>() }
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
                    doReturn(Result.success(successValue)) on { resultResult<Open>() }
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
                    doReturn(Result.success(successValue)) on { resultResult<Open?>() }
                }

            val result = mock.resultResult<Open?>()

            assertEquals(result.getOrNull(), successValue)
        }

    @Test
    fun `should stub suspendable function call with doReturn to return Result with null value`() =
        runTest {
            val mock =
                mock<SuspendFunctions> {
                    doReturn(Result.success(null as Int?)) on { resultResult<Int?>() }
                }

            val result = mock.resultResult<Int?>()

            expect(result.getOrNull()).toBeNull()
        }
}
