package org.mockito.kotlin.internal

import com.nhaarman.expect.expect
import org.junit.Test
import org.mockito.kotlin.anyVararg
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import test.SynchronousFunctions
import test.assertThrows

class KAnswerTest {
    @Test
    fun `should answer with first invocation argument`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            on { varargStringResult(anyVararg()) } doAnswer { it.first() }
        }

        /* When */
        val result = mock.varargStringResult("A", "B", "C", "D", "E")

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should answer with second invocation argument`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            on { varargStringResult(anyVararg()) } doAnswer { it.second() }
        }

        /* When */
        val result = mock.varargStringResult("A", "B", "C", "D", "E")

        /* Then */
        expect(result).toBe("B")
    }

    @Test
    fun `should answer with third invocation argument`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            on { varargStringResult(anyVararg()) } doAnswer { it.third() }
        }

        /* When */
        val result = mock.varargStringResult("A", "B", "C", "D", "E")

        /* Then */
        expect(result).toBe("C")
    }

    @Test
    fun `should answer with fourth invocation argument`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            on { varargStringResult(anyVararg()) } doAnswer { it.fourth() }
        }

        /* When */
        val result = mock.varargStringResult("A", "B", "C", "D", "E")

        /* Then */
        expect(result).toBe("D")
    }

    @Test
    fun `should answer with fifth invocation argument`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            on { varargStringResult(anyVararg()) } doAnswer { it.fifth() }
        }

        /* When */
        val result = mock.varargStringResult("A", "B", "C", "D", "E")

        /* Then */
        expect(result).toBe("E")
    }

    @Test
    fun `should answer with last invocation argument`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            on { varargStringResult(anyVararg()) } doAnswer { it.last() }
        }

        /* When */
        val result = mock.varargStringResult("A", "B", "C", "D", "E")

        /* Then */
        expect(result).toBe("E")
    }

    @Test
    fun `should answer with all invocation arguments`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            on { varargStringResult(anyVararg()) } doAnswer {
                it.all().joinToString("; ")
            }
        }

        /* When */
        val result = mock.varargStringResult("A", "B", "C", "D", "E")

        /* Then */
        expect(result).toBe("A; B; C; D; E")
    }

    @Test
    fun `should answer with single invocation argument`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            on { varargStringResult(anyVararg()) } doAnswer { it.single() }
        }

        /* When */
        val result = mock.varargStringResult("A")

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun `should throw when trying to answer with single invocation argument, but with actually more`() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            on { varargStringResult(anyVararg()) } doAnswer { it.single() }
        }

        /* When, Then */
        val exception: IllegalArgumentException = assertThrows {
            mock.varargStringResult("A", "B")
        }

        /* Then */
        expect(exception.message).toContain("xpected to have exactly 1 argument but got 2")
    }
}
