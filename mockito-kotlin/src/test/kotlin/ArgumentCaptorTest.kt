import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.expect.expect
import com.nhaarman.mockito_kotlin.capture
import org.junit.Test
import java.util.*

class ArgumentCaptorTest {

    @Test
    fun explicitCaptor() {
        val date: Date = mock()
        val time = argumentCaptor<Long>()

        date.time = 5L

        verify(date).time = capture(time)
        expect(time.value).toBe(5L)
    }

    @Test
    fun implicitCaptor() {
        val date: Date = mock()
        date.time = 5L

        verify(date).time = capture {
            expect(it).toBe(5L)
        }
    }
}