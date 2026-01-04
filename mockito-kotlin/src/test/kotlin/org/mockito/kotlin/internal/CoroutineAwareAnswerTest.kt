package org.mockito.kotlin.internal

import com.nhaarman.expect.expect
import kotlin.coroutines.Continuation
import kotlin.reflect.jvm.javaMethod
import kotlin.test.assertEquals
import org.junit.Test
import org.mockito.internal.invocation.InterceptedInvocation
import org.mockito.internal.stubbing.answers.Returns
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.internal.CoroutineAwareAnswer.Companion.wrap
import org.mockito.kotlin.mock
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify

@JvmInline private value class ValueClass(val value: String)

private interface Functions {
    fun syncString(): String

    suspend fun suspendString(): String

    suspend fun suspendValueClass(): ValueClass
}

class CoroutineAwareAnswerTest {
    @Test
    fun `should not wrap an answer to a sync method call`() {
        val function = Functions::syncString
        val stringValue = "test"
        val invocationOnMock: InvocationOnMock = mock {
            on { it.method } doReturn function.javaMethod!!
        }
        val answer = Returns(stringValue)

        val result = answer.wrap(function)

        expect(result).toBeInstanceOf<Returns>()
        assertEquals(stringValue, result.answer(invocationOnMock))
    }

    @Test
    fun `should wrap an answer to a suspend function call`() {
        val function = Functions::suspendString
        val stringValue = "test"
        val continuation: Continuation<String> = mock { on { context } doReturn mock() }
        val invocationOnMock: InterceptedInvocation = mock {
            on { it.method } doReturn function.javaMethod!!
            on { it.rawArguments } doReturn arrayOf(continuation)
        }
        val answer = Returns(stringValue)

        val wrapped = answer.wrap(function)
        wrapped.answer(invocationOnMock)

        expect(wrapped::class.simpleName).toBe("SuspendableAnswer")

        verify(continuation).context
        val resultCaptor = argumentCaptor<Result<String>>()
        verify(continuation, timeout(20)).resumeWith(resultCaptor.capture())
        expect(resultCaptor.firstValue.getOrNull()).toBe(stringValue)
    }
}
