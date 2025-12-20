package org.mockito.kotlin.internal

import com.nhaarman.expect.expect
import kotlin.coroutines.Continuation
import kotlin.reflect.jvm.javaMethod
import kotlin.test.assertEquals
import kotlinx.coroutines.delay
import org.junit.Test
import org.mockito.internal.invocation.InterceptedInvocation
import org.mockito.internal.stubbing.answers.Returns
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.internal.CoroutineAwareAnswer.Companion.wrap
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

private val stringValue = "test"

class Functions {
    fun syncString(): String {
        return stringValue
    }

    suspend fun suspendString(): String {
        delay(1)
        return stringValue
    }
}

class CoroutineAwareAnswerTest {
    @Test
    fun `should not wrap an answer to a sync method call`() {
        val invocationOnMock: InvocationOnMock = mock {
            on { it.method } doReturn Functions::syncString.javaMethod!!
        }
        val answer = Returns(stringValue)

        val result = answer.wrap(isInvocationOnSuspendFunction = false)

        expect(result).toBeInstanceOf<Returns>()
        assertEquals(stringValue, result.answer(invocationOnMock))
    }

    @Test
    fun `should wrap an answer to a suspend function call`() {
        val continuation: Continuation<String> = mock { on { context } doReturn mock() }
        val invocationOnMock: InterceptedInvocation = mock {
            on { it.method } doReturn Functions::suspendString.javaMethod!!
            on { it.rawArguments } doReturn arrayOf(continuation)
        }
        val answer = Returns(stringValue)

        val wrapped = answer.wrap(isInvocationOnSuspendFunction = true)
        wrapped.answer(invocationOnMock)

        expect(wrapped::class.simpleName).toBe("SuspendableAnswer")

        verify(continuation).context
        val resultCaptor = argumentCaptor<Result<String>>()
        verify(continuation).resumeWith(resultCaptor.capture())
        expect(resultCaptor.firstValue.getOrNull()).toBe(stringValue)
    }
}
