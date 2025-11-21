package test

import com.nhaarman.expect.expect
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.mockito.InOrder
import org.mockito.kotlin.*
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.inOrder
import java.util.*

class CoroutinesTest {

    @Test
    fun stubbingSuspending() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            onBlocking { stringResult() } doReturn "Value"
        }

        /* When */
        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("Value")
    }

    @Test
    fun stubbingSuspending_usingSuspendingFunction() {
        /* Given */
        val mock = mock<SuspendFunctions> {
            onBlocking { stringResult() } doReturn runBlocking { SomeClass().result("Value") }
        }

        /* When */
        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("Value")
    }

    @Test
    fun stubbingSuspending_runBlocking() = runBlocking {
        /* Given */
        val mock = mock<SuspendFunctions> {
            onBlocking { stringResult() } doReturn "Value"
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("Value")
    }

    @Test
    fun stubbingSuspending_wheneverBlocking() {
        /* Given */
        val mock: SuspendFunctions = mock()
        wheneverBlocking { mock.stringResult() }
            .doReturn("Value")

        /* When */
        val result = runBlocking { mock.stringResult() }

        /* Then */
        expect(result).toBe("Value")
    }

    @Test
    fun stubbingSuspending_doReturn() {
        /* Given */
        val mock = spy(SomeClass())
        doReturn("Value").wheneverBlocking(mock) { delaying() }

        /* When */
        val result = runBlocking { mock.delaying() }

        /* Then */
        expect(result).toBe("Value")
    }

    @Test
    fun stubbingNonSuspending() {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            onBlocking { stringResult() } doReturn "Value"
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("Value")
    }

    @Test
    fun stubbingNonSuspending_runBlocking() = runBlocking {
        /* Given */
        val mock = mock<SynchronousFunctions> {
            onBlocking { stringResult() } doReturn "Value"
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("Value")
    }

    @Test
    fun delayingResult() {
        /* Given */
        val instance = SomeClass()

        /* When */
        val result = runBlocking { instance.delaying() }

        /* Then */
        expect(result).toBe("Value")
    }

    @Test
    fun delayingResult_runBlocking() = runBlocking {
        /* Given */
        val instance = SomeClass()

        /* When */
        val result = instance.delaying()

        /* Then */
        expect(result).toBe("Value")
    }

    @Test
    fun verifySuspendFunctionCalled() {
        /* Given */
        val mock = mock<SuspendFunctions>()

        /* When */
        runBlocking { mock.stringResult() }

        /* Then */
        runBlocking { verify(mock).stringResult() }
    }

    @Test
    fun verifySuspendFunctionCalled_runBlocking() = runBlocking<Unit> {
        val mock = mock<SuspendFunctions>()

        mock.stringResult()

        verify(mock).stringResult()
    }

    @Test
    fun verifySuspendFunctionCalled_verifyBlocking() {
        val mock = mock<SuspendFunctions>()

        runBlocking { mock.stringResult() }

        verifyBlocking(mock) { stringResult() }
    }

    @Test
    fun verifyAtLeastOnceSuspendFunctionCalled_verifyBlocking() {
        val mock = mock<SuspendFunctions>()

        runBlocking { mock.stringResult() }
        runBlocking { mock.stringResult() }

        verifyBlocking(mock, atLeastOnce()) { stringResult() }
    }

    @Test
    fun verifySuspendMethod() = runBlocking {
        val mock: SuspendFunctions = mock()

        mock.stringResult()

        inOrder(mock) {
            verify(mock).stringResult()
        }
    }

    @Test
    fun answerWithSuspendFunction() = runBlocking {
        val mock: SuspendFunctions = mock()

        wheneverBlocking {mock.stringResult(any()) } doAnswer { it.single() }

        assertEquals("Value", mock.stringResult("Value"))
    }

    @Test
    fun inplaceAnswerWithSuspendFunction() = runBlocking {
        val mock: SuspendFunctions = mock {
            onBlocking { stringResult(any()) } doAnswer { it.single() }
        }

        assertEquals("Value", mock.stringResult("Value"))
    }

    @Test
    fun callFromSuspendFunction() = runBlocking {
        val mock: SuspendFunctions = mock()

        wheneverBlocking {mock.stringResult(any()) } doAnswer { it.single() }

        val result = async {
            val answer = mock.stringResult("Value")

            Result.success(answer)
        }

        assertEquals("Value", result.await().getOrThrow())
    }

    @Test
    @OptIn(ObsoleteCoroutinesApi::class)
    fun callFromActor() = runBlocking {
        val mock: SuspendFunctions = mock()

        wheneverBlocking { mock.stringResult(any()) } doAnswer { it.single() }

        val actor = actor<Optional<String>> {
            for (element in channel) {
                mock.stringResult(element.get())
            }
        }

        actor.send(Optional.of("Value"))
        actor.close()

        verify(mock).stringResult("Value")

        Unit
    }

    @Test
    fun answerWithSuspendFunctionWithoutArgs() = runBlocking {
        val mock: SuspendFunctions = mock()

        wheneverBlocking { mock.stringResult() } doReturn "Value"

        assertEquals("Value", mock.stringResult())
    }

    @Test
    fun answerWithSuspendFunctionWithDestructuredArgs() = runBlocking {
        val mock: SuspendFunctions = mock()

        wheneverBlocking { mock.stringResult(any()) } doAnswer { (s: String) -> s }

        assertEquals("Value", mock.stringResult("Value"))
    }

    @Test
    fun willAnswerWithControlledSuspend() = runBlocking {
        val mock: SuspendFunctions = mock()

        val job = Job()

        wheneverBlocking { mock.stringResult() } doSuspendableAnswer {
            job.join()
            "Value"
        }

        val asyncTask = async {
            mock.stringResult()
        }

        job.complete()

        withTimeout(100) {
            assertEquals("Value", asyncTask.await())
        }
    }

    @Test
    fun stubberAnswerWithSuspendFunction() = runBlocking {
        val mock: SuspendFunctions = mock()

        doSuspendableAnswer { it.single<Any>() }.whenever(mock).stringResult(any())

        assertEquals("Value", mock.stringResult("Value"))
    }

    @Test
    fun stubberCallFromSuspendFunction() = runBlocking {
        val mock: SuspendFunctions = mock()

        doSuspendableAnswer { it.single<Any>() }.whenever(mock).stringResult(any())

        val result = async {
            val answer = mock.stringResult("Value")

            Result.success(answer)
        }

        assertEquals("Value", result.await().getOrThrow())
    }

    @Test
    @OptIn(ObsoleteCoroutinesApi::class)
    fun stubberCallFromActor() {
        val mock: SuspendFunctions = mock()

        doSuspendableAnswer {
            withContext(Dispatchers.Default) { it.single<Any>() }
        }.wheneverBlocking(mock) { stringResult(any()) }

        runBlocking {
            val actor = actor<Optional<String>> {
                for (element in channel) {
                    mock.stringResult(element.get())
                }
            }

            actor.send(Optional.of("Value"))
            actor.close()
        }

        verifyBlocking(mock) {stringResult("Value") }
    }

    @Test
    fun stubberAnswerWithSuspendFunctionWithoutArgs() {
        val mock: SuspendFunctions = mock()

        doSuspendableAnswer {
            withContext(Dispatchers.Default) { "Value" }
        }.wheneverBlocking(mock) { stringResult() }

        assertEquals("Value", runBlocking { mock.stringResult() })
    }

    @Test
    fun stubberAnswerWithSuspendFunctionWithDestructuredArgs() {
        val mock: SuspendFunctions = mock()

        doSuspendableAnswer { (s: String) ->
            withContext(Dispatchers.Default) { s }
        }.wheneverBlocking(mock) { stringResult(any()) }

        val actual = runBlocking { mock.stringResult("Value") }
        assertEquals("Value", actual)
    }

    @Test
    fun stubberWillAnswerWithControlledSuspend() {
        val mock: SuspendFunctions = mock()

        val job = Job()

        doSuspendableAnswer {
            job.join()
            "Value"
        }.wheneverBlocking(mock) { stringResult() }

        runBlocking {
            val asyncTask = async {
                mock.stringResult()
            }

            job.complete()

            withTimeout(100) {
                assertEquals("Value", asyncTask.await())
            }
        }
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
        val fixtureOne: SuspendFunctions = mock()
        val fixtureTwo: SuspendFunctions = mock()

        /* When */
        runBlocking {
            fixtureOne.stringResult()
            fixtureTwo.stringResult()
        }

        /* Then */
        val inOrder = inOrder(fixtureOne, fixtureTwo)
        inOrder.verifyBlocking(fixtureOne) { stringResult() }
        inOrder.verifyBlocking(fixtureTwo) { stringResult() }
    }

    @Test
    fun inOrderSuspendingCallsFailure() {
        /* Given */
        val fixtureOne: SuspendFunctions = mock()
        val fixtureTwo: SuspendFunctions = mock()

        /* When */
        runBlocking {
            fixtureOne.stringResult()
            fixtureTwo.stringResult()
        }

        /* Then */
        val inOrder = inOrder(fixtureOne, fixtureTwo)
        inOrder.verifyBlocking(fixtureTwo) { stringResult() }
        assertThrows(AssertionError::class.java) {
            inOrder.verifyBlocking(fixtureOne) { stringResult() }
        }
    }

    @Test
    fun inOrderBlockSuspendingCalls() {
        /* Given */
        val fixtureOne: SuspendFunctions = mock()
        val fixtureTwo: SuspendFunctions = mock()

        /* When */
        runBlocking {
            fixtureOne.stringResult()
            fixtureTwo.stringResult()
        }

        /* Then */
        inOrder(fixtureOne, fixtureTwo) {
            verifyBlocking(fixtureOne) { stringResult() }
            verifyBlocking(fixtureTwo) { stringResult() }
        }
    }

    @Test
    fun inOrderBlockSuspendingCallsFailure() {
        /* Given */
        val fixtureOne: SuspendFunctions = mock()
        val fixtureTwo: SuspendFunctions = mock()

        /* When */
        runBlocking {
            fixtureOne.stringResult()
            fixtureTwo.stringResult()
        }

        /* Then */
        inOrder(fixtureOne, fixtureTwo) {
            verifyBlocking(fixtureTwo) { stringResult() }
            assertThrows(AssertionError::class.java) {
                verifyBlocking(fixtureOne) { stringResult() }
            }
        }
    }

    @Test
    fun inOrderOnObjectSuspendingCalls() {
        /* Given */
        val mock: SuspendFunctions = mock()

        /* When */
        runBlocking {
            mock.stringResult("Value")
            mock.stringResult("Other Value")
        }

        /* Then */
        mock.inOrder {
            verifyBlocking { stringResult("Value") }
            verifyBlocking { stringResult("Other Value") }
        }
    }

    @Test
    fun inOrderOnObjectSuspendingCallsFailure() {
        /* Given */
        val mock: SuspendFunctions = mock()

        /* When */
        runBlocking {
            mock.stringResult("Value")
            mock.stringResult("Other Value")
        }

        /* Then */
        mock.inOrder {
            verifyBlocking { stringResult("Other Value") }
            assertThrows(AssertionError::class.java) {
                verifyBlocking { stringResult("Value") }
            }
        }
    }
}

open class SomeClass {
    suspend fun result(r: String) = withContext(Dispatchers.Default) { r }
    open suspend fun delaying() = withContext(Dispatchers.Default) {
        delay(100)
        "Value"
    }
}
