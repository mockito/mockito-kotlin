package test

import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.mockito.ArgumentMatcher
import org.mockito.internal.matchers.VarargMatcher
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

/**
 *
 */
class VarArgMatcherTest : TestBase() {

    @Test
    fun testVarargAnySuccess() {
        val t = mock<Tested>()

        // a matcher to check if any of the varargs was equals to "b"
        val matcher = VarargAnyMatcher<String, Boolean>({ "b" == it }, true, false)

        whenever(t.something(argThat(matcher))).thenAnswer(matcher)

        assert(t.something("a", "b", "c"))
    }

    @Test
    fun testVarargAnyFail() {
        val t = mock<Tested>()

        // a matcher to check if any of the varargs was equals to "d"
        val matcher = VarargAnyMatcher<String, Boolean>({ "d" == it }, true, false)

        whenever(t.something(argThat(matcher))).thenAnswer(matcher)

        assert(!t.something("a", "b", "c"))
    }

    /**
     * a test class with a vararg function to test
     */
    interface Tested {
        fun something(vararg values: String): Boolean
    }

    /**
     * a VarargMatcher implementation for varargs of type [T] that will answer with type [R] if any of the var args
     * matched. Needs to keep state between matching invocations.
     */
    private class VarargAnyMatcher<T, R>(
        private val match: ((T) -> Boolean),
        private val success: R,
        private val failure: R
    ) : ArgumentMatcher<T>, VarargMatcher, Answer<R> {
        private var anyMatched = false

        override fun matches(t: T): Boolean {
            anyMatched = anyMatched or match(t)
            return true
        }

        override fun answer(i: InvocationOnMock) = if (anyMatched) success else failure
    }

}
