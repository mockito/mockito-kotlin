@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package test

import com.nhaarman.mockitokotlin2.KStubbing
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.experimental.*
import org.junit.Test
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing


class CouroutinesTest {

    interface InterfaceWithSuspendFunction {
        suspend fun foo(): Boolean
    }

    interface InterfaceWithNormalFunction {
        fun foo(): Boolean
    }

    interface Callback {
        fun onTrue()
        fun onFalse()
    }

    class SomeClass(val interfaceWithSuspendingFunction: InterfaceWithSuspendFunction,
                    val interfaceWithNormalFunction: InterfaceWithNormalFunction) {

        suspend fun suspendingFunction(callback: Callback) = withContext(CommonPool) {
            log("#suspendingFunctionWithNestedSuspend(): Launching suspending function")
            val result = async(CommonPool) {
                log("Asynchronously  calling suspending function and awaiting result")
                interfaceWithSuspendingFunction.foo()
            }.await()
            log("Awaiting result...")
            log("Result is: $result")
            if (result) {
                log("Calling callback method onTrue()")
                callback.onTrue()
            } else {
                log("Calling callback method onFalse()")
                callback.onFalse()
            }
        }


        /**
         * Take a look at the test [testNonSuspendingFunctionWithNestedSuspend] and notice the job.join(). We must
         * use join() to signal completion otherwise the Callback will not be invoked.
         */
        fun nonSuspendingFunction(callback: Callback): Job = launch {
            log("#nonSuspendingFunctionWithNestedSuspend(): Launching non-suspending function")
            val result = async(coroutineContext) {
                log("Asynchronously  calling suspending function and awaiting result")
                interfaceWithNormalFunction.foo()
            }.await()
            log("Awaiting result...")
            log("Result is: $result")
            if (result) {
                log("Calling callback method onTrue()")
                callback.onTrue()
            } else {
                log("Calling callback method onFalse()")
                callback.onFalse()
            }
        }

    }

    val callback = mock<Callback>()
    val suspendingInterface = mock<InterfaceWithSuspendFunction> {
        onBlocking {
            delay(500)
            foo() } doReturn true
    }
    val nonSuspendingInterface = mock<InterfaceWithNormalFunction> {
        on { foo() } doReturn true
    }

    val subject = SomeClass(suspendingInterface, nonSuspendingInterface)

    @Test
    fun testSuspendingFunction() = runBlocking {
        subject.suspendingFunction(callback)

        verify(callback).onTrue()
    }

    @Test
    fun testNonSuspendingFunction() = runBlocking {
        subject.nonSuspendingFunction(callback).join()

        verify(callback).onTrue()
    }
}

fun <T : Any, R> KStubbing<T>.onBlocking(
        m: suspend T.() -> R
): OngoingStubbing<R> {
    return runBlocking { Mockito.`when`(mock.m()) }
}

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")
