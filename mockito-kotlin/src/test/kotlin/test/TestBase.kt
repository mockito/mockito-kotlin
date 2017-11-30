package test

import com.nhaarman.mockitokotlin2.createinstance.mockMakerInlineEnabled
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