package test

import com.nhaarman.expect.expect
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.mockito.InOrder
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.whenever

class CoroutinesTest {

    @Test
    fun stubbingSuspending() {
        /* Given */
        val m = mock<SuspendFunctions> { on { intResult() } doReturn 42 }

        /* When */
        val result = runBlocking { m.intResult() }

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun stubbingSuspending_usingSuspendingFunction() {
        /* Given */
        val m =
            mock<SuspendFunctions> {
                on { intResult() } doReturn runBlocking { Open().intResult(42) }
            }

        /* When */
        val result = runBlocking { m.intResult() }

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun stubbingSuspending_runBlocking() = runBlocking {
        /* Given */
        val mock = mock<SuspendFunctions> { on { intResult() } doReturn 42 }

        /* When */
        val result = mock.intResult()

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun stubbingSuspending_wheneverBlocking() {
        /* Given */
        val mock: SuspendFunctions = mock()
        whenever { mock.intResult() }.doReturn(42)

        /* When */
        val result = runBlocking { mock.intResult() }

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun stubbingSuspending_doReturn() {
        /* Given */
        val spy = spy(Open())
        doReturn(10).whenever(spy) { delayedIntResult() }

        /* When */
        val result = runBlocking { spy.delayedIntResult() }

        /* Then */
        expect(result).toBe(10)
    }

    @Test
    fun stubbingNonSuspending() {
        /* Given */
        val mock = mock<SynchronousFunctions> { on { intResult() } doReturn 42 }

        /* When */
        val result = mock.intResult()

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun stubbingNonSuspending_runBlocking() = runBlocking {
        /* Given */
        val mock = mock<SuspendFunctions> { on { intResult() } doReturn 42 }

        /* When */
        val result = mock.intResult()

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun delayingResult() {
        /* Given */
        val instance = Open()

        /* When */
        val result = runBlocking { instance.delayedIntResult() }

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun delayingResult_runBlocking() = runBlocking {
        /* Given */
        val instance = Open()

        /* When */
        val result = instance.delayedIntResult()

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun verifySuspendFunctionCalled() {
        /* Given */
        val mock = mock<SuspendFunctions>()

        /* When */
        runBlocking { mock.intResult() }

        /* Then */
        runBlocking { verify(mock).intResult() }
    }

    @Test
    fun verifySuspendFunctionCalled_runBlocking() =
        runBlocking<Unit> {
            val mock = mock<SuspendFunctions>()

            mock.intResult()

            verify(mock).intResult()
        }

    @Test
    fun verifySuspendFunctionCalled_verifyBlocking() {
        val mock = mock<SuspendFunctions>()

        runBlocking { mock.intResult() }

        verifyBlocking(mock) { intResult() }
    }

    @Test
    fun verifyAtLeastOnceSuspendFunctionCalled_verifyBlocking() {
        val mock = mock<SuspendFunctions>()

        runBlocking { mock.intResult() }
        runBlocking { mock.intResult() }

        verifyBlocking(mock, atLeastOnce()) { intResult() }
    }

    @Test
    fun verifySuspendMethod() = runBlocking {
        val mock: SuspendFunctions = mock()

        mock.intResult()

        inOrder(mock) { verify(mock).intResult() }
    }

    @Test
    fun answerWithSuspendFunction() = runBlocking {
        val mock: SuspendFunctions = mock()

        whenever { mock.intResult(any()) } doSuspendableAnswer
            {
                withContext(Dispatchers.Default) { it.getArgument(0) }
            }

        assertEquals(5, mock.intResult(5))
    }

    @Test
    fun inplaceAnswerWithSuspendFunction() = runBlocking {
        val mock: SuspendFunctions = mock {
            on { intResult(any()) } doSuspendableAnswer
                {
                    withContext(Dispatchers.Default) { it.getArgument(0) }
                }
        }

        assertEquals(5, mock.intResult(5))
    }

    @Test
    fun callFromSuspendFunction() = runBlocking {
        val mock: SuspendFunctions = mock()

        whenever { mock.intResult(any()) } doSuspendableAnswer
            {
                withContext(Dispatchers.Default) { it.getArgument(0) }
            }

        val result = async {
            val answer = mock.intResult(5)

            Result.success(answer)
        }

        assertEquals(5, result.await().getOrThrow())
    }

    @Test
    @OptIn(ObsoleteCoroutinesApi::class)
    fun callFromActor() = runBlocking {
        val mock: SuspendFunctions = mock()

        whenever { mock.intResult(any()) } doSuspendableAnswer
            {
                withContext(Dispatchers.Default) { it.getArgument(0) }
            }

        val actor =
            actor<Optional<Int>> {
                for (element in channel) {
                    mock.intResult(element.get())
                }
            }

        actor.send(Optional.of(10))
        actor.close()

        verify(mock).intResult(10)

        Unit
    }

    @Test
    fun answerWithSuspendFunctionWithoutArgs() = runBlocking {
        val mock: SuspendFunctions = mock()

        whenever { mock.intResult() } doSuspendableAnswer
            {
                withContext(Dispatchers.Default) { 42 }
            }

        assertEquals(42, mock.intResult())
    }

    @Test
    fun answerWithSuspendFunctionWithDestructuredArgs() = runBlocking {
        val mock: SuspendFunctions = mock()

        whenever { mock.intResult(any()) } doSuspendableAnswer
            { (i: Int) ->
                withContext(Dispatchers.Default) { i }
            }

        assertEquals(5, mock.intResult(5))
    }

    @Test
    fun willAnswerWithControlledSuspend() = runBlocking {
        val mock: SuspendFunctions = mock()

        val job = Job()

        whenever { mock.intResult() } doSuspendableAnswer
            {
                job.join()
                5
            }

        val asyncTask = async { mock.intResult() }

        job.complete()

        withTimeout(100) { assertEquals(5, asyncTask.await()) }
    }

    @Test
    fun stubberAnswerWithSuspendFunction() = runBlocking {
        val mock: SuspendFunctions = mock()

        doSuspendableAnswer { withContext(Dispatchers.Default) { it.getArgument<Int>(0) } }
            .whenever(mock)
            .intResult(any())

        assertEquals(5, mock.intResult(5))
    }

    @Test
    fun stubberCallFromSuspendFunction() = runBlocking {
        val mock: SuspendFunctions = mock()

        doSuspendableAnswer { withContext(Dispatchers.Default) { it.getArgument<Int>(0) } }
            .whenever(mock)
            .intResult(any())

        val result = async {
            val answer = mock.intResult(5)

            Result.success(answer)
        }

        assertEquals(5, result.await().getOrThrow())
    }

    @Test
    @OptIn(ObsoleteCoroutinesApi::class)
    fun stubberCallFromActor() = runBlocking {
        val mock: SuspendFunctions = mock()

        doSuspendableAnswer { withContext(Dispatchers.Default) { it.getArgument<Int>(0) } }
            .whenever(mock)
            .intResult(any())

        val actor =
            actor<Optional<Int>> {
                for (element in channel) {
                    mock.intResult(element.get())
                }
            }

        actor.send(Optional.of(10))
        actor.close()

        verify(mock).intResult(10)

        Unit
    }

    @Test
    fun stubberAnswerWithSuspendFunctionWithoutArgs() = runBlocking {
        val mock: SuspendFunctions = mock()

        doSuspendableAnswer { withContext(Dispatchers.Default) { 42 } }.whenever(mock).intResult()

        assertEquals(42, mock.intResult())
    }

    @Test
    fun stubberAnswerWithSuspendFunctionWithDestructuredArgs() = runBlocking {
        val mock: SuspendFunctions = mock()

        doSuspendableAnswer { (i: Int) -> withContext(Dispatchers.Default) { i } }
            .whenever(mock)
            .intResult(any())

        assertEquals(5, mock.intResult(5))
    }

    @Test
    fun stubberWillAnswerWithControlledSuspend() = runBlocking {
        val mock: SuspendFunctions = mock()

        val job = Job()

        doSuspendableAnswer {
                job.join()
                5
            }
            .whenever(mock)
            .intResult()

        val asyncTask = async { mock.intResult() }

        job.complete()

        withTimeout(100) { assertEquals(5, asyncTask.await()) }
    }

    @Test
    fun inOrderRemainsCompatible() {
        /* Given */
        val mock: SuspendFunctions = mock()

        /* When */
        val inOrder = inOrder(mock)

        /* Then */
        expect(inOrder).toBeInstanceOf<InOrder>()
    }

    @Test
    fun inOrderSuspendingCalls() {
        /* Given */
        val mockOne: SuspendFunctions = mock()
        val mockTwo: SuspendFunctions = mock()

        /* When */
        runBlocking {
            mockOne.intResult()
            mockTwo.intResult()
        }

        /* Then */
        val inOrder = inOrder(mockOne, mockTwo)
        inOrder.verifyBlocking(mockOne) { intResult() }
        inOrder.verifyBlocking(mockTwo) { intResult() }
    }

    @Test
    fun inOrderSuspendingCallsFailure() {
        /* Given */
        val mockOne: SuspendFunctions = mock()
        val mockTwo: SuspendFunctions = mock()

        /* When */
        runBlocking {
            mockOne.intResult()
            mockTwo.intResult()
        }

        /* Then */
        val inOrder = inOrder(mockOne, mockTwo)
        inOrder.verifyBlocking(mockTwo) { intResult() }
        assertThrows(AssertionError::class.java) { inOrder.verifyBlocking(mockOne) { intResult() } }
    }

    @Test
    fun inOrderBlockSuspendingCalls() {
        /* Given */
        val mockOne: SuspendFunctions = mock()
        val mockTwo: SuspendFunctions = mock()

        /* When */
        runBlocking {
            mockOne.intResult()
            mockTwo.intResult()
        }

        /* Then */
        inOrder(mockOne, mockTwo) {
            verifyBlocking(mockOne) { intResult() }
            verifyBlocking(mockTwo) { intResult() }
        }
    }

    @Test
    fun inOrderBlockSuspendingCallsFailure() {
        /* Given */
        val mockOne: SuspendFunctions = mock()
        val mockTwo: SuspendFunctions = mock()

        /* When */
        runBlocking {
            mockOne.intResult()
            mockTwo.intResult()
        }

        /* Then */
        inOrder(mockOne, mockTwo) {
            verifyBlocking(mockTwo) { intResult() }
            assertThrows(AssertionError::class.java) { verifyBlocking(mockOne) { intResult() } }
        }
    }

    @Test
    fun inOrderOnObjectSuspendingCalls() {
        /* Given */
        val mock: SuspendFunctions = mock()

        /* When */
        runBlocking {
            mock.intResult(1)
            mock.intResult(2)
        }

        /* Then */
        mock.inOrder {
            verifyBlocking { intResult(1) }
            verifyBlocking { intResult(2) }
        }
    }

    @Test
    fun inOrderOnObjectSuspendingCallsFailure() {
        /* Given */
        val mock: SuspendFunctions = mock()

        /* When */
        runBlocking {
            mock.intResult(1)
            mock.intResult(2)
        }

        /* Then */
        mock.inOrder {
            verifyBlocking { intResult(2) }
            assertThrows(AssertionError::class.java) { verifyBlocking { intResult(1) } }
        }
    }
}
