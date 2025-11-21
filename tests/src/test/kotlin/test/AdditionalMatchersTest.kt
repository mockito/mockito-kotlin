package test

import org.junit.Test
import org.mockito.kotlin.*

class AdditionalCaptorsTest : TestBase() {

    @Test
    fun testGeq() {
        mock<SynchronousFunctions>().apply {
            int(1)
            verify(this).int(geq(0))
            verify(this).int(geq(1))
            verify(this, never()).int(geq(2))
        }
    }

    @Test
    fun testLeq() {
        mock<SynchronousFunctions>().apply {
            int(1)
            verify(this).int(leq(2))
            verify(this).int(leq(1))
            verify(this, never()).int(leq(0))
        }
    }

    @Test
    fun testGt() {
        mock<SynchronousFunctions>().apply {
            int(1)
            verify(this).int(gt(0))
            verify(this, never()).int(gt(1))
        }
    }

    @Test
    fun testLt() {
        mock<SynchronousFunctions>().apply {
            int(1)
            verify(this).int(lt(2))
            verify(this, never()).int(lt(1))
        }
    }

    @Test
    fun testCmpEq() {
        mock<SynchronousFunctions>().apply {
            int(1)
            verify(this).int(cmpEq(1))
            verify(this, never()).int(cmpEq(2))
        }
    }

    @Test
    fun testAryEqBoolean() {
        mock<SynchronousFunctions>().apply {
            booleanArray(booleanArrayOf(true, false, true))
            verify(this).booleanArray(aryEq(booleanArrayOf(true, false, true)))
            verify(this, never()).booleanArray(aryEq(booleanArrayOf(true, false)))
        }
    }

    @Test
    fun testAryEqByte() {
        mock<SynchronousFunctions>().apply {
            byteArray(byteArrayOf(1, 2, 3))
            verify(this).byteArray(aryEq(byteArrayOf(1, 2, 3)))
            verify(this, never()).byteArray(aryEq(byteArrayOf(1, 2)))
        }
    }

    @Test
    fun testAryEqShort() {
        mock<SynchronousFunctions>().apply {
            shortArray(shortArrayOf(1, 2, 3))
            verify(this).shortArray(aryEq(shortArrayOf(1, 2, 3)))
            verify(this, never()).shortArray(aryEq(shortArrayOf(1, 2)))
        }
    }

    @Test
    fun testAryEqInt() {
        mock<SynchronousFunctions>().apply {
            intArray(intArrayOf(1, 2, 3))
            verify(this).intArray(aryEq(intArrayOf(1, 2, 3)))
            verify(this, never()).intArray(aryEq(intArrayOf(1, 2)))
        }
    }

    @Test
    fun testAryEqLong() {
        mock<SynchronousFunctions>().apply {
            longArray(longArrayOf(1, 2, 3))
            verify(this).longArray(aryEq(longArrayOf(1, 2, 3)))
            verify(this, never()).longArray(aryEq(longArrayOf(1, 2)))
        }
    }

    @Test
    fun testAryEqChar() {
        mock<SynchronousFunctions>().apply {
            charArray(charArrayOf('1', '2', '3'))
            verify(this).charArray(aryEq(charArrayOf('1', '2', '3')))
            verify(this, never()).charArray(aryEq(charArrayOf('1', '2')))
        }
    }

    @Test
    fun testAryEqFloat() {
        mock<SynchronousFunctions>().apply {
            floatArray(floatArrayOf(1f, 2f, 3.4f))
            verify(this).floatArray(aryEq(floatArrayOf(1f, 2f, 3.4f)))
            verify(this, never()).floatArray(aryEq(floatArrayOf(1f, 2f)))
        }
    }

    @Test
    fun testAryEqDouble() {
        mock<SynchronousFunctions>().apply {
            doubleArray(doubleArrayOf(1.0, 2.0, 3.4))
            verify(this).doubleArray(aryEq(doubleArrayOf(1.0, 2.0, 3.4)))
            verify(this, never()).doubleArray(aryEq(doubleArrayOf(1.0, 2.0)))
        }
    }

    @Test
    fun testAryEq() {
        mock<SynchronousFunctions>().apply {
            stringArray(arrayOf("Hello", "there"))
            verify(this).stringArray(aryEq(arrayOf("Hello", "there")))
            verify(this, never()).stringArray(aryEq(arrayOf("Hello")))
        }
    }

    @Test
    fun testFind() {
        mock<SynchronousFunctions>().apply {
            string("Hello")
            verify(this).string(find("l+o$".toRegex()))
            verify(this, never()).string(find("l$".toRegex()))
        }
    }

    @Test
    fun testAnd() {
        mock<SynchronousFunctions>().apply {
            int(5)
            verify(this).int(and(geq(4), leq(6)))
            verify(this, never()).int(and(geq(4), leq(4)))
        }
    }

    @Test
    fun testOr() {
        mock<SynchronousFunctions>().apply {
            int(5)
            verify(this).int(and(gt(4), lt(6)))
            verify(this, never()).int(and(gt(4), lt(4)))
        }
    }

    @Test
    fun testNot() {
        mock<SynchronousFunctions>().apply {
            int(5)
            verify(this).int(not(eq(4)))
            verify(this, never()).int(not(eq(5)))
        }
    }
}
