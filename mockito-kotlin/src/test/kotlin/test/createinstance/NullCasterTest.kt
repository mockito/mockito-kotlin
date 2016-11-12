package test.createinstance

import com.nhaarman.expect.expect
import com.nhaarman.mockito_kotlin.createinstance.NullCaster
import org.junit.Test
import test.TestBase


class NullCasterTest : TestBase() {

    private val nullCaster = NullCaster()

    @Test
    fun createInstance() {
        /* When */
        val result = nullCaster.createInstance(String::class)

        /* Then */
        expect(result).toBeNull()
    }

    @Test
    fun kotlinAcceptsNullValue() {
        /* Given */
        val s: String = nullCaster.createInstance(String::class)

        /* When */
        acceptNonNullableString(s)
    }

    private fun acceptNonNullableString(s: String) {
    }
}
