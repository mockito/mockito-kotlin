package org.mockito.kotlin.internal

import com.nhaarman.expect.expect
import kotlin.coroutines.Continuation
import kotlin.reflect.jvm.javaMethod
import kotlin.test.assertEquals
import org.junit.Test
import org.mockito.internal.invocation.InterceptedInvocation
import org.mockito.internal.stubbing.answers.Returns
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.internal.CoroutineAwareAnswer.Companion.wrapAsCoroutineAwareAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

@JvmInline private value class ValueClass(val value: String)

@JvmInline private value class PrimitiveValueClass(val value: Long)

private interface Functions {
    fun syncString(): String

    suspend fun suspendString(): String

    suspend fun suspendValueClass(): ValueClass

    suspend fun suspendPrimitiveValueClass(): PrimitiveValueClass
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
    fun `should wrap and wire the answer the suspend function call without suspensions`() {
        val function = Functions::suspendString
        val stringValue = "test"
        val continuation: Continuation<String> = mock { on { context } doReturn mock() }
        val invocationOnMock: InterceptedInvocation = mock {
            on { it.method } doReturn function.javaMethod!!
            on { it.rawArguments } doReturn arrayOf(continuation)
        }
        val answer = Returns(stringValue)

        val wrapped = answer.wrapAsCoroutineAwareAnswer()
        val result = wrapped.answer(invocationOnMock)

        verify(continuation).context
        verifyNoMoreInteractions(continuation)

        assertEquals(stringValue, result)
    }

    @Test
    fun `should unboxed answer of a suspend function call for non-primitives`() {
        val function = Functions::suspendValueClass
        val value = ValueClass("test")

        val continuation: Continuation<ValueClass> = mock { on { context } doReturn mock() }
        val invocationOnMock: InterceptedInvocation = mock {
            on { it.method } doReturn function.javaMethod!!
            on { it.rawArguments } doReturn arrayOf(continuation)
        }
        val answer = Returns(value)

        val wrapped = answer.wrapAsCoroutineAwareAnswer()
        val result = wrapped.answer(invocationOnMock)

        expect(result).toBeInstanceOf<String>()
        expect(result).toBe(value.value)
    }

    @Test
    fun `should keep boxed answer of a suspend function call for primitives`() {
        val function = Functions::suspendPrimitiveValueClass
        val value = PrimitiveValueClass(42)

        val continuation: Continuation<ValueClass> = mock { on { context } doReturn mock() }
        val invocationOnMock: InterceptedInvocation = mock {
            on { it.method } doReturn function.javaMethod!!
            on { it.rawArguments } doReturn arrayOf(continuation)
        }
        val answer = Returns(value)

        val wrapped = answer.wrapAsCoroutineAwareAnswer()
        val result = wrapped.answer(invocationOnMock)

        expect(result).toBeInstanceOf<PrimitiveValueClass>()
        expect(result).toBe(value)
    }
}
