@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package test

import com.nhaarman.expect.expect
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.mockito.InOrder
import org.mockito.kotlin.*
import java.util.*

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
    fun stubbingSuspending_wheneverBlocking() {
        /* Given */
        val m: SomeInterface = mock()
        wheneverBlocking { m.suspending() }
            .doReturn(42)

        /* When */
        val result = runBlocking { m.suspending() }

        /* Then */
        expect(result).toBe(42)
    }

    @Test
    fun stubbingSuspending_doReturn() {
        /* Given */
        val m = spy(SomeClass())
        doReturn(10)
            .wheneverBlocking(m) {
                delaying()
            }

        /* When */
        val result = runBlocking { m.delaying() }

        /* Then */
        expect(result).toBe(10)
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
    fun verifyAtLeastOnceSuspendFunctionCalled_verifyBlocking() {
        val m = mock<SomeInterface>()

        runBlocking { m.suspending() }
        runBlocking { m.suspending() }

        verifyBlocking(m, atLeastOnce()) { suspending() }
    }

    @Test
    fun verifySuspendMethod() = runBlocking {
        val testSubject: SomeInterface = mock()

        testSubject.suspending()

        inOrder(testSubject) {
            verify(testSubject).suspending()
        }
    }

    @Test
    fun answerWithSuspendFunction() = runBlocking {
        val fixture: SomeInterface = mock()

        whenever(fixture.suspendingWithArg(any())).doSuspendableAnswer {
            withContext(Dispatchers.Default) { it.getArgument<Int>(0) }
        }

        assertEquals(5, fixture.suspendingWithArg(5))
    }

    @Test
    fun inplaceAnswerWithSuspendFunction() = runBlocking {
        val fixture: SomeInterface = mock {
            onBlocking { suspendingWithArg(any()) } doSuspendableAnswer {
                withContext(Dispatchers.Default) { it.getArgument<Int>(0) }
            }
        }

        assertEquals(5, fixture.suspendingWithArg(5))
    }

    @Test
    fun callFromSuspendFunction() = runBlocking {
        val fixture: SomeInterface = mock()

        whenever(fixture.suspendingWithArg(any())).doSuspendableAnswer {
            withContext(Dispatchers.Default) { it.getArgument<Int>(0) }
        }

        val result = async {
            val answer = fixture.suspendingWithArg(5)

            Result.success(answer)
        }

        assertEquals(5, result.await().getOrThrow())
    }

    @Test
    @OptIn(ObsoleteCoroutinesApi::class)
    fun callFromActor() = runBlocking {
        val fixture: SomeInterface = mock()

        whenever(fixture.suspendingWithArg(any())).doSuspendableAnswer {
            withContext(Dispatchers.Default) { it.getArgument<Int>(0) }
        }

        val actor = actor<Optional<Int>> {
            for (element in channel) {
                fixture.suspendingWithArg(element.get())
            }
        }

        actor.send(Optional.of(10))
        actor.close()

        verify(fixture).suspendingWithArg(10)

        Unit
    }

    @Test
    fun answerWithSuspendFunctionWithoutArgs() = runBlocking {
        val fixture: SomeInterface = mock()

        whenever(fixture.suspending()).doSuspendableAnswer {
            withContext(Dispatchers.Default) { 42 }
        }

        assertEquals(42, fixture.suspending())
    }

    @Test
    fun answerWithSuspendFunctionWithDestructuredArgs() = runBlocking {
        val fixture: SomeInterface = mock()

        whenever(fixture.suspendingWithArg(any())).doSuspendableAnswer { (i: Int) ->
            withContext(Dispatchers.Default) { i }
        }

        assertEquals(5, fixture.suspendingWithArg(5))
    }

    @Test
    fun willAnswerWithControlledSuspend() = runBlocking {
        val fixture: SomeInterface = mock()

        val job = Job()

        whenever(fixture.suspending()).doSuspendableAnswer {
            job.join()
            5
        }

        val asyncTask = async {
            fixture.suspending()
        }

        job.complete()

        withTimeout(100) {
            assertEquals(5, asyncTask.await())
        }
    }

    @Test
    fun stubberAnswerWithSuspendFunction() = runBlocking {
        val fixture: SomeInterface = mock()

        doSuspendableAnswer {
            withContext(Dispatchers.Default) { it.getArgument<Int>(0) }
        }.whenever(fixture).suspendingWithArg(any())

        assertEquals(5, fixture.suspendingWithArg(5))
    }

    @Test
    fun stubberCallFromSuspendFunction() = runBlocking {
        val fixture: SomeInterface = mock()

        doSuspendableAnswer {
            withContext(Dispatchers.Default) { it.getArgument<Int>(0) }
        }.whenever(fixture).suspendingWithArg(any())

        val result = async {
            val answer = fixture.suspendingWithArg(5)

            Result.success(answer)
        }

        assertEquals(5, result.await().getOrThrow())
    }

    @Test
    @OptIn(ObsoleteCoroutinesApi::class)
    fun stubberCallFromActor() = runBlocking {
        val fixture: SomeInterface = mock()

        doSuspendableAnswer {
            withContext(Dispatchers.Default) { it.getArgument<Int>(0) }
        }.whenever(fixture).suspendingWithArg(any())

        val actor = actor<Optional<Int>> {
            for (element in channel) {
                fixture.suspendingWithArg(element.get())
            }
        }

        actor.send(Optional.of(10))
        actor.close()

        verify(fixture).suspendingWithArg(10)

        Unit
    }

    @Test
    fun stubberAnswerWithSuspendFunctionWithoutArgs() = runBlocking {
        val fixture: SomeInterface = mock()

        doSuspendableAnswer {
            withContext(Dispatchers.Default) { 42 }
        }.whenever(fixture).suspending()

        assertEquals(42, fixture.suspending())
    }

    @Test
    fun stubberAnswerWithSuspendFunctionWithDestructuredArgs() = runBlocking {
        val fixture: SomeInterface = mock()

        doSuspendableAnswer { (i: Int) ->
            withContext(Dispatchers.Default) { i }
        }.whenever(fixture).suspendingWithArg(any())

        assertEquals(5, fixture.suspendingWithArg(5))
    }

    @Test
    fun stubberWillAnswerWithControlledSuspend() = runBlocking {
        val fixture: SomeInterface = mock()

        val job = Job()

        doSuspendableAnswer {
            job.join()
            5
        }.whenever(fixture).suspending()

        val asyncTask = async {
            fixture.suspending()
        }

        job.complete()

        withTimeout(100) {
            assertEquals(5, asyncTask.await())
        }
    }

    @Test
    fun inOrderRemainsCompatible() {
        /* Given */
        val fixture: SomeInterface = mock()

        /* When */
        val inOrder = inOrder(fixture)

        /* Then */
        expect(inOrder).toBeInstanceOf<InOrder>()
    }

    @Test
    fun inOrderSuspendingCalls() {
        /* Given */
        val fixtureOne: SomeInterface = mock()
        val fixtureTwo: SomeInterface = mock()

        /* When */
        runBlocking {
            fixtureOne.suspending()
            fixtureTwo.suspending()
        }

        /* Then */
        val inOrder = inOrder(fixtureOne, fixtureTwo)
        inOrder.verifyBlocking(fixtureOne) { suspending() }
        inOrder.verifyBlocking(fixtureTwo) { suspending() }
    }

    @Test
    fun inOrderSuspendingCallsFailure() {
        /* Given */
        val fixtureOne: SomeInterface = mock()
        val fixtureTwo: SomeInterface = mock()

        /* When */
        runBlocking {
            fixtureOne.suspending()
            fixtureTwo.suspending()
        }

        /* Then */
        val inOrder = inOrder(fixtureOne, fixtureTwo)
        inOrder.verifyBlocking(fixtureTwo) { suspending() }
        assertThrows(AssertionError::class.java) {
            inOrder.verifyBlocking(fixtureOne) { suspending() }
        }
    }

    @Test
    fun inOrderBlockSuspendingCalls() {
        /* Given */
        val fixtureOne: SomeInterface = mock()
        val fixtureTwo: SomeInterface = mock()

        /* When */
        runBlocking {
            fixtureOne.suspending()
            fixtureTwo.suspending()
        }

        /* Then */
        inOrder(fixtureOne, fixtureTwo) {
            verifyBlocking(fixtureOne) { suspending() }
            verifyBlocking(fixtureTwo) { suspending() }
        }
    }

    @Test
    fun inOrderBlockSuspendingCallsFailure() {
        /* Given */
        val fixtureOne: SomeInterface = mock()
        val fixtureTwo: SomeInterface = mock()

        /* When */
        runBlocking {
            fixtureOne.suspending()
            fixtureTwo.suspending()
        }

        /* Then */
        inOrder(fixtureOne, fixtureTwo) {
            verifyBlocking(fixtureTwo) { suspending() }
            assertThrows(AssertionError::class.java) {
                verifyBlocking(fixtureOne) { suspending() }
            }
        }
    }

    @Test
    fun inOrderOnObjectSuspendingCalls() {
        /* Given */
        val fixture: SomeInterface = mock()

        /* When */
        runBlocking {
            fixture.suspendingWithArg(1)
            fixture.suspendingWithArg(2)
        }

        /* Then */
        fixture.inOrder {
            verifyBlocking { suspendingWithArg(1) }
            verifyBlocking { suspendingWithArg(2) }
        }
    }

    @Test
    fun inOrderOnObjectSuspendingCallsFailure() {
        /* Given */
        val fixture: SomeInterface = mock()

        /* When */
        runBlocking {
            fixture.suspendingWithArg(1)
            fixture.suspendingWithArg(2)
        }

        /* Then */
        fixture.inOrder {
            verifyBlocking { suspendingWithArg(2) }
            assertThrows(AssertionError::class.java) {
                verifyBlocking { suspendingWithArg(1) }
            }
        }
    }
}

interface SomeInterface {

    suspend fun suspending(): Int
    suspend fun suspendingWithArg(arg: Int): Int
    fun nonsuspending(): Int
}

open class SomeClass {

    suspend fun result(r: Int) = withContext(Dispatchers.Default) { r }

    open suspend fun delaying() = withContext(Dispatchers.Default) {
        delay(100)
        42
    }
}
