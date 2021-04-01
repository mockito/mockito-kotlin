package test

import com.nhaarman.expect.expect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.mockito.stubbing.Answer

class BDDMockitoTest {

    @Test
    fun given_will_properlyStubs() {
        /* Given */
        val mock = mock<Methods>()

        /* When */
        given(mock.stringResult()) will Answer<String> { "Test" }

        /* Then */
        expect(mock.stringResult()).toBe("Test")
    }

    @Test
    fun given_willReturn_properlyStubs() {
        /* Given */
        val mock = mock<Methods>()

        /* When */
        given(mock.stringResult()).willReturn("Test")

        /* Then */
        expect(mock.stringResult()).toBe("Test")
    }

    @Test
    fun givenLambda_willReturn_properlyStubs() {
        /* Given */
        val mock = mock<Methods>()

        /* When */
        given { mock.stringResult() }.willReturn("Test")

        /* Then */
        expect(mock.stringResult()).toBe("Test")
    }

    @Test
    fun given_willReturnLambda_properlyStubs() {
        /* Given */
        val mock = mock<Methods>()

        /* When */
        given(mock.stringResult()).willReturn { "Test" }

        /* Then */
        expect(mock.stringResult()).toBe("Test")
    }

    @Test
    fun givenLambda_willReturnLambda_properlyStubs() {
        /* Given */
        val mock = mock<Methods>()

        /* When */
        given { mock.stringResult() } willReturn { "Test" }

        /* Then */
        expect(mock.stringResult()).toBe("Test")
    }

    @Test
    fun given_willAnswer_properlyStubs() {
        /* Given */
        val mock = mock<Methods>()

        /* When */
        given(mock.stringResult()).willAnswer { "Test" }

        /* Then */
        expect(mock.stringResult()).toBe("Test")
    }

    @Test
    fun given_willAnswerInfix_properlyStubs() {
        /* Given */
        val mock = mock<Methods>()

        /* When */
        given(mock.stringResult()) willAnswer { "Test" }

        /* Then */
        expect(mock.stringResult()).toBe("Test")
    }

    @Test
    fun given_willAnswerInfix_withInvocationInfo_properlyStubs() {
        /* Given */
        val mock = mock<Methods>()

        /* When */
        given(mock.stringResult(any())) willAnswer { invocation ->
            (invocation.arguments[0] as String)
                .reversed()
        }

        /* Then */
        expect(mock.stringResult("Test")).toBe("tseT")
    }

    @Test
    fun given_willThrowInfix_properlyStubs() {
        /* Given */
        val mock = mock<Methods>()

        /* When */
        Assertions.assertThrows(IllegalStateException::class.java) {
            given(mock.stringResult()) willThrow { IllegalStateException() }
            mock.stringResult()
        }
    }

    @Test
    fun then() {
        /* Given */
        val mock = mock<Methods>()
        whenever(mock.stringResult()).thenReturn("Test")

        /* When */
        mock.stringResult()

        /* Then */
        org.mockito.kotlin.then(mock).should().stringResult()
    }
}
