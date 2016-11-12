package test

import com.nhaarman.mockito_kotlin.createinstance.mockMakerInlineEnabled
import org.junit.After
import org.junit.Before

abstract class TestBase {

    @Before
    open fun setup() {
        mockMakerInlineEnabled = false
    }

    @After
    open fun tearDown() {
        mockMakerInlineEnabled = null
    }
}