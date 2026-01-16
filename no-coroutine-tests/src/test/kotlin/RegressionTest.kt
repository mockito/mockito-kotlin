import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.stubbing

class RegressionTest {
    private val a = mock<A>()

    @Test
    fun `should stub synchronous mock in absence of the 'kotlinx-coroutines-core' library`() {
        stubbing(a) {
            on { doSomething() }
                .thenReturn("a")
        }

        assertEquals("a", a.doSomething())
    }
}

interface A {
    fun doSomething(): String
}
