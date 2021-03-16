package test.createinstance

import com.nhaarman.expect.expect
import org.mockito.kotlin.internal.createInstance
import org.junit.Test
import test.TestBase


class NullCasterTest : TestBase() {

    @Test
    fun createInstance() {
        /* When */
        val result = createInstance(String::class)

        /* Then */
        expect(result).toBeNull()
    }

    @Test
    fun kotlinAcceptsNullValue() {
        /* Given */
        val s: String = createInstance(String::class)

        /* When */
        acceptNonNullableString(s)
    }

    private fun acceptNonNullableString(@Suppress("UNUSED_PARAMETER") s: String) {
    }
}
