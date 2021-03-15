package test.createinstance

import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.internal.createInstance
import org.junit.Test
import test.TestBase


class NullCasterTest : TestBase() {

    @Test
    fun createInstance() {
        /* When */
        val result = createInstance(Any::class)

        /* Then */
        expect(result).toBeNull()
    }

    @Test
    fun kotlinAcceptsNullValue() {
        /* Given */
        val mockObject: Any = createInstance(Any::class)

        /* When */
        acceptNonNullableObject(mockObject)
    }

    private fun acceptNonNullableObject(@Suppress("UNUSED_PARAMETER") mockObject: Any) {
    }
}
