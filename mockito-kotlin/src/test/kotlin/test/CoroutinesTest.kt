@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package test

import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withContext
import org.junit.Test


class CoroutinesTest {

    @Test
    fun stubbingSuspending() {
        /* Given */
        val m = mock<SomeInterface> {
            onBlocking { suspending() } doReturn 42
        }

        /* When */
        val result = runBlocking { m.suspending() }

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun stubbingSuspending_usingSuspendingFunction() {
        /* Given */
        val m = mock<SomeInterface> {
            onBlocking { suspending() } doReturn runBlocking { SomeClass().result(42) }
        }

        /* When */
        val result = runBlocking { m.suspending() }

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun stubbingSuspending_runBlocking() = runBlocking {
        /* Given */
        val m = mock<SomeInterface> {
            onBlocking { suspending() } doReturn 42
        }

        /* When */
        val result = m.suspending()

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun stubbingNonSuspending() {
        /* Given */
        val m = mock<SomeInterface> {
            onBlocking { nonsuspending() } doReturn 42
        }

        /* When */
        val result = m.nonsuspending()

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun stubbingNonSuspending_runBlocking() = runBlocking {
        /* Given */
        val m = mock<SomeInterface> {
            onBlocking { nonsuspending() } doReturn 42
        }

        /* When */
        val result = m.nonsuspending()

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun delayingResult() {
        /* Given */
        val m = SomeClass()

        /* When */
        val result = runBlocking { m.delaying() }

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun delayingResult_runBlocking() = runBlocking {
        /* Given */
        val m = SomeClass()

        /* When */
        val result = m.delaying()

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun verifySuspendFunctionCalled() {
        /* Given */
        val m = mock<SomeInterface>()

        /* When */
        runBlocking { m.suspending() }

        /* Then */
        runBlocking { verify(m).suspending() }
    }

    @Test
    fun verifySuspendFunctionCalled_runBlocking() = runBlocking<Unit> {
        val m = mock<SomeInterface>()

        m.suspending()

        verify(m).suspending()
    }

    @Test
    fun verifySuspendFunctionCalled_verifyBlocking() {
        val m = mock<SomeInterface>()

        runBlocking { m.suspending() }

        verifyBlocking(m) { suspending() }
    }

    @Test
    fun verifySuspendMethod() = runBlocking {
        val testSubject : SomeInterface = mock()

        testSubject.suspending()

        inOrder(testSubject) {
            verify(testSubject).suspending()
        }
    }
}

interface SomeInterface {

    suspend fun suspending(): Int
    fun nonsuspending(): Int
}

class SomeClass {

    suspend fun result(r: Int) = withContext(CommonPool) { r }

    suspend fun delaying() = withContext(CommonPool) {
        delay(100)
        42
    }
}
