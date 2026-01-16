import org.junit.Assert.assertThrows
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.stubbing

class RegressionTest {
    private val a = mock<A>()

    @Test
    fun `should throw when stubbing a synchronous mock in absence of the 'kotlinx-coroutines-core' library`() {
        assertThrows(NoClassDefFoundError::class.java) {
            stubbing(a) {
                on { doSomething() }
                    .thenReturn("a")
            }
        }
    }
}

interface A {
    fun doSomething(): String
}
