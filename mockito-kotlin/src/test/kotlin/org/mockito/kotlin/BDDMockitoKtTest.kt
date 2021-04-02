package org.mockito.kotlin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class BDDMockitoKtTest {

    @Test
    fun willSuspendableAnswer_withoutArgument() = runBlocking {
        val fixture: SomeInterface = mock()

        given(fixture.suspending()).willSuspendableAnswer {
            withContext(Dispatchers.Default) { 42 }
        }

        assertEquals(42, fixture.suspending())
        then(fixture).should().suspending()
        Unit
    }

    @Test
    fun willSuspendableAnswer_witArgument() = runBlocking {
        val fixture: SomeInterface = mock()

        given(fixture.suspendingWithArg(any())).willSuspendableAnswer {
            withContext(Dispatchers.Default) { it.getArgument<Int>(0) }
        }

        assertEquals(42, fixture.suspendingWithArg(42))
        then(fixture).should().suspendingWithArg(42)
        Unit
    }

    @Test
    fun willThrow_kclass_single() {
        val fixture: SomeInterface = mock()

        given(fixture.foo()).willThrow(RuntimeException::class)

        assertFailsWith(RuntimeException::class) {
            fixture.foo()
        }
    }

    @Test
    fun willThrow_kclass_multiple() {
        val fixture: SomeInterface = mock()

        given(fixture.foo()).willThrow(RuntimeException::class, IllegalArgumentException::class)

        assertFailsWith(RuntimeException::class) {
            fixture.foo()
        }
        assertFailsWith(IllegalArgumentException::class) {
            fixture.foo()
        }
    }

    @Test
    fun willReturnConsecutively() {
        val fixture: SomeInterface = mock()

        given(fixture.foo()).willReturnConsecutively(listOf(42, 24))

        assertEquals(42, fixture.foo())
        assertEquals(24, fixture.foo())
    }
}

interface SomeInterface {
    fun foo(): Int

    suspend fun suspending(): Int
    suspend fun suspendingWithArg(arg: Int): Int
}
