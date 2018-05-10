package test

import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import com.nhaarman.mockitokotlin2.*
import org.junit.Test
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
}