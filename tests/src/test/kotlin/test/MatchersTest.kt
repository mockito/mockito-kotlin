package test

import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import com.nhaarman.mockitokotlin2.*
import org.junit.Test
import org.mockito.ArgumentMatcher
import org.mockito.internal.matchers.VarargMatcher
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import java.io.IOException

class MatchersTest : TestBase() {

    @Test
    fun anyString() {
        mock<Methods>().apply {
            string("")
            verify(this).string(any())
        }
    }

    @Test
    fun anyInt() {
        mock<Methods>().apply {
            int(3)
            verify(this).int(any())
        }
    }

    @Test
    fun anyClosedClass() {
        mock<Methods>().apply {
            closed(Closed())
            verify(this).closed(any())
        }
    }

    @Test
    fun anyIntArray() {
        mock<Methods>().apply {
            intArray(intArrayOf())
            verify(this).intArray(any())
        }
    }

    @Test
    fun anyClassArray() {
        mock<Methods>().apply {
            closedArray(arrayOf(Closed()))
            verify(this).closedArray(anyArray())
        }
    }

    @Test
    fun anyNullableClassArray() {
        mock<Methods>().apply {
            closedNullableArray(arrayOf(Closed(), null))
            verify(this).closedNullableArray(anyArray())
        }
    }

    @Test
    fun anyStringVararg() {
        mock<Methods>().apply {
            closedVararg(Closed(), Closed())
            verify(this).closedVararg(anyVararg())
        }
    }

    @Test
    fun anyNull_neverVerifiesAny() {
        mock<Methods>().apply {
            nullableString(null)
            verify(this, never()).nullableString(any())
        }
    }

    @Test
    fun anyNull_verifiesAnyOrNull() {
        mock<Methods>().apply {
            nullableString(null)
            verify(this).nullableString(anyOrNull())
        }
    }

    @Test
    fun anyNull_forPrimitiveBoolean() {
        mock<Methods>().apply {
            boolean(false)
            verify(this).boolean(anyOrNull())
        }
    }
    @Test
    fun anyNull_forPrimitiveByte() {
        mock<Methods>().apply {
            byte(3)
            verify(this).byte(anyOrNull())
        }
    }

    @Test
    fun anyNull_forPrimitiveChar() {
        mock<Methods>().apply {
            char('a')
            verify(this).char(anyOrNull())
        }
    }

    @Test
    fun anyNull_forPrimitiveShort() {
        mock<Methods>().apply {
            short(3)
            verify(this).short(anyOrNull())
        }
    }

    @Test
    fun anyNull_forPrimitiveInt() {
        mock<Methods>().apply {
            int(3)
            verify(this).int(anyOrNull())
        }
    }

    @Test
    fun anyNull_forPrimitiveLong() {
        mock<Methods>().apply {
            long(3)
            verify(this).long(anyOrNull())
        }
    }

    @Test
    fun anyNull_forPrimitiveFloat() {
        mock<Methods>().apply {
            float(3f)
            verify(this).float(anyOrNull())
        }
    }

    @Test
    fun anyNull_forPrimitiveDouble() {
        mock<Methods>().apply {
            double(3.0)
            verify(this).double(anyOrNull())
        }
    }

    /** https://github.com/nhaarman/mockito-kotlin/issues/27 */
    @Test
    fun anyThrowableWithSingleThrowableConstructor() {
        mock<Methods>().apply {
            throwableClass(ThrowableClass(IOException()))
            verify(this).throwableClass(any())
        }
    }

    @Test
    fun listArgThat() {
        mock<Methods>().apply {
            closedList(listOf(Closed(), Closed()))
            verify(this).closedList(
                  argThat {
                      size == 2
                  }
            )
        }
    }

    @Test
    fun listArgForWhich() {
        mock<Methods>().apply {
            closedList(listOf(Closed(), Closed()))
            verify(this).closedList(
                  argForWhich {
                      size == 2
                  }
            )
        }
    }

    @Test
    fun listArgWhere() {
        mock<Methods>().apply {
            closedList(listOf(Closed(), Closed()))
            verify(this).closedList(
                  argWhere {
                      it.size == 2
                  }
            )
        }
    }

    @Test
    fun listArgCheck() {
        mock<Methods>().apply {
            closedList(listOf(Closed(), Closed()))
            verify(this).closedList(
                  check {
                      expect(it.size).toBe(2)
                  }
            )
        }
    }

    @Test
    fun checkProperlyFails() {
        mock<Methods>().apply {
            closedList(listOf(Closed(), Closed()))

            expectErrorWithMessage("Argument(s) are different!") on {
                verify(this).closedList(
                      check {
                          expect(it.size).toBe(1)
                      }
                )
            }
        }
    }

    @Test
    fun checkWithNullArgument_throwsError() {
        mock<Methods>().apply {
            nullableString(null)

            expectErrorWithMessage("null").on {
                verify(this).nullableString(check {})
            }
        }
    }


    @Test
    fun isA_withNonNullableString() {
        mock<Methods>().apply {
            string("")
            verify(this).string(isA<String>())
        }
    }

    @Test
    fun isA_withNullableString() {
        mock<Methods>().apply {
            nullableString("")
            verify(this).nullableString(isA<String>())
        }
    }

    @Test
    fun same_withNonNullArgument() {
        mock<Methods>().apply {
            string("")
            verify(this).string(same(""))
        }
    }

    @Test
    fun same_withNullableNonNullArgument() {
        mock<Methods>().apply {
            nullableString("")
            verify(this).nullableString(same(""))
        }
    }

    @Test
    fun same_withNullArgument() {
        mock<Methods>().apply {
            nullableString(null)
            verify(this).nullableString(same(null))
        }
    }

    @Test
    fun testVarargAnySuccess() {
        /* Given */
        val t = mock<Methods>()
        // a matcher to check if any of the varargs was equals to "b"
        val matcher = VarargAnyMatcher<String, Boolean>({ "b" == it }, true, false)

        /* When */
        whenever(t.varargBooleanResult(argThat(matcher))).thenAnswer(matcher)

        /* Then */
        expect(t.varargBooleanResult("a", "b", "c")).toBe(true)
    }

    @Test
    fun testVarargAnyFail() {
        /* Given */
        val t = mock<Methods>()
        // a matcher to check if any of the varargs was equals to "d"
        val matcher = VarargAnyMatcher<String, Boolean>({ "d" == it }, true, false)

        /* When */
        whenever(t.varargBooleanResult(argThat(matcher))).thenAnswer(matcher)

        /* Then */
        expect(t.varargBooleanResult("a", "b", "c")).toBe(false)
    }

    /** https://github.com/nhaarman/mockito-kotlin/issues/328 */
    @Test
    fun testRefEqForNonNullableParameter() {
       mock<Methods>().apply {
           /* When */
           val array = intArrayOf(2, 3)
           intArray(array)
           
           /* Then */
           verify(this).intArray(refEq(array))
       }
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