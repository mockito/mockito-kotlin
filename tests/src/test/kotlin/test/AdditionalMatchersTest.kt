package test

import org.junit.Test
import org.mockito.kotlin.*

class AdditionalCaptorsTest : TestBase() {

    @Test
    fun testGeq() {
        mock<Methods>().apply {
            int(1)
            verify(this).int(geq(0))
            verify(this).int(geq(1))
            verify(this, never()).int(geq(2))
        }
    }

    @Test
    fun testLeq() {
        mock<Methods>().apply {
            int(1)
            verify(this).int(leq(2))
            verify(this).int(leq(1))
            verify(this, never()).int(leq(0))
        }
    }

    @Test
    fun testGt() {
        mock<Methods>().apply {
            int(1)
            verify(this).int(gt(0))
            verify(this, never()).int(gt(1))
        }
    }

    @Test
    fun testLt() {
        mock<Methods>().apply {
            int(1)
            verify(this).int(lt(2))
            verify(this, never()).int(lt(1))
        }
    }

    @Test
    fun testCmpEq() {
        mock<Methods>().apply {
            int(1)
            verify(this).int(cmpEq(1))
            verify(this, never()).int(cmpEq(2))
        }
    }

    @Test
    fun testAryEqPrimitive() {
        mock<Methods>().apply {
            intArray(intArrayOf(1, 2, 3))
            verify(this).intArray(aryEq(intArrayOf(1, 2, 3)))
            verify(this, never()).intArray(aryEq(intArrayOf(1, 2)))
        }
    }

    @Test
    fun testAryEq() {
        mock<Methods>().apply {
            stringArray(arrayOf("Hello", "there"))
            verify(this).stringArray(aryEq(arrayOf("Hello", "there")))
            verify(this, never()).stringArray(aryEq(arrayOf("Hello")))
        }
    }

    @Test
    fun testfind() {
        mock<Methods>().apply {
            string("Hello")
            verify(this).string(find("l+o$".toRegex()))
            verify(this, never()).string(find("l$".toRegex()))
        }
    }

    @Test
    fun testAnd() {
        mock<Methods>().apply {
            int(5)
            verify(this).int(and(geq(4), leq(6)))
            verify(this, never()).int(and(geq(4), leq(4)))
        }
    }

    @Test
    fun testOr() {
        mock<Methods>().apply {
            int(5)
            verify(this).int(and(gt(4), lt(6)))
            verify(this, never()).int(and(gt(4), lt(4)))
        }
    }

    @Test
    fun testNot() {
        mock<Methods>().apply {
            int(5)
            verify(this).int(not(eq(4)))
            verify(this, never()).int(not(eq(5)))
        }
    }
}
