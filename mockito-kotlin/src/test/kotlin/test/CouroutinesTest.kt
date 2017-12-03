@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package test

import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.KStubbing
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing

interface SomeInterface {

    suspend fun foo(): Int
}

class CouroutinesTest {

    interface SomeInterface {

        suspend fun foo(): Int
    }

    @Test
    fun test() = runBlocking {
        val m = mock<SomeInterface> {
            onBlocking { foo() } doReturn 42
        }

        val result = m.foo()

        expect(result).toBe(42)
    }
}

fun <T : Any, R> KStubbing<T>.onBlocking(
    m: suspend T.() -> R
): OngoingStubbing<R> {
    return runBlocking { Mockito.`when`(mock.m()) }
}