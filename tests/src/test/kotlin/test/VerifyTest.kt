package test

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.mockito.exceptions.verification.TooLittleActualInvocations
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent

class VerifyTest : TestBase() {

    @Test
    fun verify0Calls() {
        val iface = mock<TestInterface>()

        verify(iface) {
            0 * { call(any()) }
        }
    }

    @Test
    fun verifyNCalls() {
        val iface = mock<TestInterface>()

        iface.call(42)
        iface.call(42)

        verify(iface) {
            2 * { call(42) }
        }
    }

    @Test(expected = TooLittleActualInvocations::class)
    fun verifyFailsWithWrongCount() {
        val iface = mock<TestInterface>()

        iface.call(0)

        verify(iface) {
            2 * { call(0) }
        }
    }

    @Test(expected = ArgumentsAreDifferent::class)
    fun verifyFailsWithWrongArg() {
        val iface = mock<TestInterface>()

        iface.call(3)

        verify(iface) {
            1 * { call(0) }
        }
    }

    interface TestInterface {
        fun call(arg: Int)
    }
}