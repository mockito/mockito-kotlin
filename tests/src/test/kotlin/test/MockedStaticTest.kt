package test

import org.junit.Test
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class MockedStaticTest : TestBase() {

    @Test
    fun testVerifyExtensionFun() {
        mockStatic(SomeObject::class.java).use { mocked ->
            SomeObject.aStaticMethod()
            SomeObject.aStaticMethod()

            mocked.verify(times(2)) { SomeObject.aStaticMethod() }
        }
    }
}
