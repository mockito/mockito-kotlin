@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package test

import com.nhaarman.mockitokotlin2.*
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.experimental.*
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing


class CouroutinesTest {

    interface InterfaceWithSuspendFunction {
        suspend fun foo(): Boolean
    }

    interface InterfaceWithNormalFuntion {
        fun foo(): Boolean
    }

    interface Callback {
        fun onTrue()
        fun onFalse()
    }

    class SomeClass(val interfaceWithSuspendingFunction: InterfaceWithSuspendFunction,
                    val interfaceWithNormalFuntion: InterfaceWithNormalFuntion) {


        suspend fun suspendingFunction(callback: Callback) {
            log("Launching suspending function")
            launch {
                val result = async(CommonPool) {
                    delay(2000)
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
            }.join()
            log("Called join to signal completion")
        }

        fun nonSuspendingFunction(callback: Callback) : Job {
            return launch {
                log("Launching non-suspending function")
                val result = async(CommonPool) {
                    delay(2000)
                    log("Asynchronously  calling suspending function and awaiting result")
                    interfaceWithNormalFuntion.foo()
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
    }

    val callback = mock<Callback>()
    val suspendingInterface = mock<InterfaceWithSuspendFunction> {
        onBlocking { foo() } doReturn true
    }
    val nonSuspendingInterface = mock<InterfaceWithNormalFuntion> {
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
        val job = subject.nonSuspendingFunction(callback)
        assertThat(job.isCompleted, `is`(false))
        job.join()
        assertThat(job.isCompleted, `is`(true))
        verify(callback).onTrue()
    }
}

fun <T : Any, R> KStubbing<T>.onBlocking(
      m: suspend T.() -> R
): OngoingStubbing<R> {
    return runBlocking { Mockito.`when`(mock.m()) }
}

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")