package test

import org.junit.Test
import org.mockito.exceptions.base.MockitoAssertionError
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mockStatic
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class MockedStaticTest : TestBase() {

    @Test
    fun testVerifyExtensionFun() {
        mockStatic<SomeObject>().use { mocked ->
            SomeObject.aStaticMethod()
            SomeObject.aStaticMethod()

            mocked.verify(times(2)) { SomeObject.aStaticMethod() }
        }
    }

    @Test
    fun testInOrderVerifyStatic() {
        mockStatic<SomeObject>().use { mocked ->
            SomeObject.aStaticMethod()
            SomeObject.aStaticMethodReturningString()

            val inOrder = inOrder(SomeObject::class.java)
            inOrder.verify(mocked) { SomeObject.aStaticMethod() }
            inOrder.verify(mocked) { SomeObject.aStaticMethodReturningString() }
        }
    }

    @Test
    fun testInOrderVerifyStaticWithMode() {
        mockStatic<SomeObject>().use { mocked ->
            SomeObject.aStaticMethod()
            SomeObject.aStaticMethod()
            SomeObject.aStaticMethodReturningString()

            val inOrder = inOrder(SomeObject::class.java)
            inOrder.verify(mocked, times(2)) { SomeObject.aStaticMethod() }
            inOrder.verify(mocked, times(1)) { SomeObject.aStaticMethodReturningString() }
        }
    }

    @Test(expected = MockitoAssertionError::class)
    fun testInOrderVerifyStaticOutOfOrderFails() {
        mockStatic<SomeObject>().use { mocked ->
            SomeObject.aStaticMethod()
            SomeObject.aStaticMethodReturningString()

            val inOrder = inOrder(SomeObject::class.java)
            inOrder.verify(mocked) { SomeObject.aStaticMethodReturningString() }
            inOrder.verify(mocked) { SomeObject.aStaticMethod() }
        }
    }

    @Test(expected = MockitoAssertionError::class)
    fun testInOrderVerifyStaticDefaultIsExactlyOnce() {
        mockStatic<SomeObject>().use { mocked ->
            SomeObject.aStaticMethod()
            SomeObject.aStaticMethod()

            val inOrder = inOrder(SomeObject::class.java)
            inOrder.verify(mocked) { SomeObject.aStaticMethod() }
        }
    }
}
