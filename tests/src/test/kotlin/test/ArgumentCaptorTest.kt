package test

import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import com.nhaarman.mockitokotlin2.*
import org.junit.Test
import java.util.*

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
        val (captor1, captor2, captor3, captor4, captor5) = argumentCaptor<Long, Long, Long, Long, Long>()
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
        val m: Methods = mock()

        /* When */
        m.nullableString(null)

        /* Then */
        val captor = argumentCaptor<String>()
        verify(m).nullableString(captor.capture())
        expect(captor.lastValue).toBeNull()
    }

    @Test
    fun argumentCaptor_withNullValue_usingNullable() {
        /* Given */
        val m: Methods = mock()

        /* When */
        m.nullableString(null)

        /* Then */
        val captor = nullableArgumentCaptor<String>()
        verify(m).nullableString(captor.capture())
        expect(captor.lastValue).toBeNull()
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
        val m: Methods = mock()

        /* When */
        m.nullableString("test")
        m.nullableString(null)

        /* Then */
        val captor = nullableArgumentCaptor<String>()
        verify(m, times(2)).nullableString(captor.capture())
        expect(captor.allValues).toBe(listOf("test", null))
    }

    @Test
    fun argumentCaptor_callProperties() {
        /* Given */
        val m: Methods = mock()

        /* When */
        m.int(1)
        m.int(2)
        m.int(3)
        m.int(4)
        m.int(5)

        /* Then */
        argumentCaptor<Int>().apply {
            verify(m, times(5)).int(capture())

            expect(firstValue).toBe(1)
            expect(secondValue).toBe(2)
            expect(thirdValue).toBe(3)
            expect(lastValue).toBe(5)
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun argumentCaptor_callPropertyNotAvailable() {
        /* Given */
        val m: Methods = mock()

        /* When */
        m.int(1)

        /* Then */
        argumentCaptor<Int>().apply {
            verify(m).int(capture())

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
        expectErrorWithMessage("Expected: 3 but was: 5") on {
            argumentCaptor<Long> {
                verify(date).time = capture()
                expect(lastValue).toBe(3L)
            }
        }
    }
}
