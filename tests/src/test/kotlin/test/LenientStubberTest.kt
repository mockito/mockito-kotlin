package test

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness


open class LenientStubberTest {
    @get:Rule
    val rule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Test
    fun unused_and_lenient_stubbings() {
        val mock = mock<MutableList<String>>()
        lenient().whenever(mock.add("one")).doReturn(true)
        whenever(mock[any()]).doReturn("hello")

        Assert.assertEquals("List should contain hello", "hello", mock[1])
    }

    @Test
    fun unused_and_lenient_stubbings_with_unit() {
        val mock = mock<MutableList<String>>()
        lenient().whenever { mock.add("one") }.doReturn(true)
        whenever(mock[any()]).doReturn("hello")

        Assert.assertEquals("List should contain hello", "hello", mock[1])
    }
}
