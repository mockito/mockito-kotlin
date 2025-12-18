package org.mockito.kotlin

import kotlin.test.assertFailsWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Test

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
    fun willSuspendableAnswer_withArgument() = runBlocking {
        val fixture: SomeInterface = mock()

        given(fixture.suspendingWithArg(any())).willSuspendableAnswer {
            withContext(Dispatchers.Default) { it.getArgument<Int>(0) }
        }

        assertEquals(42, fixture.suspendingWithArg(42))
        then(fixture).should().suspendingWithArg(42)
        Unit
    }

    @Test
    fun willSuspendableAnswer_givenBlocking() {
        val fixture: SomeInterface = mock()

        givenBlocking { fixture.suspending() }
            .willSuspendableAnswer { withContext(Dispatchers.Default) { 42 } }

        val result = runBlocking { fixture.suspending() }

        assertEquals(42, result)
        then(fixture).shouldBlocking { suspending() }
        Unit
    }

    @Test
    fun willSuspendableAnswer_givenBlocking_withArgument() {
        val fixture: SomeInterface = mock()

        givenBlocking { fixture.suspendingWithArg(any()) }
            .willSuspendableAnswer { withContext(Dispatchers.Default) { it.getArgument<Int>(0) } }

        val result = runBlocking { fixture.suspendingWithArg(42) }

        assertEquals(42, result)
        then(fixture).shouldBlocking { suspendingWithArg(42) }
        Unit
    }

    @Test
    fun willThrow_kclass_single() {
        val fixture: SomeInterface = mock()

        given(fixture.foo()).willThrow(RuntimeException::class)

        assertFailsWith(RuntimeException::class) { fixture.foo() }
    }

    @Test
    fun willThrow_kclass_multiple() {
        val fixture: SomeInterface = mock()

        given(fixture.foo()).willThrow(RuntimeException::class, IllegalArgumentException::class)

        assertFailsWith(RuntimeException::class) { fixture.foo() }
        assertFailsWith(IllegalArgumentException::class) { fixture.foo() }
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
