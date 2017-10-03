import com.nhaarman.expect.expect
import com.nhaarman.mockito_kotlin.mock
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test

interface SuspendedInterface {
    suspend fun getNumber(): String
}

class CoroutinesTest {
    @Test
    fun testSuspended() {
        runBlocking {
            val mock = mock<SuspendedInterface> {
                on { getNumber() }.thenReturn("123")
            }

            expect(mock.getNumber()).toBe("123")
        }
    }
}