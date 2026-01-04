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
import org.mockito.kotlin.internal.CoroutineAwareAnswer.Companion.wrapAsCoroutineAwareAnswer
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
    fun `should not really wrap and answer synchronously to a sync method call`() {
        val function = Functions::syncString
        val stringValue = "test"
        val invocationOnMock: InvocationOnMock = mock {
            on { it.method } doReturn function.javaMethod!!
        }
        val answer = Returns(stringValue)

        val wrapped = answer.wrapAsCoroutineAwareAnswer()
        val result = wrapped.answer(invocationOnMock)

        assertEquals(stringValue, result)
    }

    @Test
    fun `should wrap and answer via the continuation to a suspend function call`() {
        val function = Functions::suspendString
        val stringValue = "test"
        val continuation: Continuation<String> = mock { on { context } doReturn mock() }
        val invocationOnMock: InterceptedInvocation = mock {
            on { it.method } doReturn function.javaMethod!!
            on { it.rawArguments } doReturn arrayOf(continuation)
        }
        val answer = Returns(stringValue)

        val wrapped = answer.wrapAsCoroutineAwareAnswer()
        wrapped.answer(invocationOnMock)

        verify(continuation).context
        val resultCaptor = argumentCaptor<Result<String>>()
        verify(continuation, timeout(20)).resumeWith(resultCaptor.capture())
        expect(resultCaptor.firstValue.getOrNull()).toBe(stringValue)
    }

    @Test
    fun `should cast the unboxed answer of a suspend function call to value class`() {
        val function = Functions::suspendValueClass
        val stringValue = "test"

        val continuation: Continuation<ValueClass> = mock { on { context } doReturn mock() }
        val invocationOnMock: InterceptedInvocation = mock {
            on { it.method } doReturn function.javaMethod!!
            on { it.rawArguments } doReturn arrayOf(continuation)
        }
        val answer = Returns(stringValue) // mimics Mockito core to return an unboxed String value

        val wrapped = answer.wrapAsCoroutineAwareAnswer()
        wrapped.answer(invocationOnMock)

        val resultCaptor = argumentCaptor<Result<ValueClass>>()
        verify(continuation).resumeWith(resultCaptor.capture())
        val result = resultCaptor.firstValue.getOrNull()
        expect(result).toBeInstanceOf<ValueClass>()
        expect(result?.value).toBe(stringValue)
    }
}
