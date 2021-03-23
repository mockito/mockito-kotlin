package test

import org.junit.jupiter.api.AfterEach

abstract class TestBase {

    @AfterEach
    open fun tearDown() {
        mockMakerInlineEnabled = null
    }
}
