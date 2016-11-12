import com.nhaarman.mockito_kotlin.mockMakerInlineEnabled
import org.junit.After
import org.junit.Before

abstract class BaseTest {

    @Before
    open fun setup() {
        mockMakerInlineEnabled = false
    }

    @After
    open fun tearDown() {
        mockMakerInlineEnabled = null
    }
}