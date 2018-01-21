package test

import org.junit.After

abstract class TestBase {

    @After
    open fun tearDown() {
        mockMakerInlineEnabled = null
    }
}