package test

import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.mockito.ArgumentMatcher
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.*
import org.mockito.stubbing.Answer
import java.io.IOException

@RunWith(Enclosed::class)
class MatchersTest : TestBase() {
    class AnyMatchersTest {
        @Test
        fun anyString() {
            mock<SynchronousFunctions>().apply {
                string("")
                verify(this).string(any())
            }
        }

        @Test
        fun anyNullableString() {
            mock<SynchronousFunctions>().apply {
                nullableString("")
                verify(this).nullableString(any())
            }
        }

        @Test
        fun anyBoolean() {
            mock<SynchronousFunctions>().apply {
                boolean(true)
                verify(this).boolean(any())
            }
        }

        @Test
        fun anyBooleanArray() {
            mock<SynchronousFunctions>().apply {
                booleanArray(booleanArrayOf(true, false, false))
                verify(this).booleanArray(any())
            }
        }

        @Test
        fun anyChar() {
            mock<SynchronousFunctions>().apply {
                char('3')
                verify(this).char(any())
            }
        }

        @Test
        fun anyCharArray() {
            mock<SynchronousFunctions>().apply {
                charArray(charArrayOf('3', '4', '5'))
                verify(this).charArray(any())
            }
        }

        @Test
        fun anyByte() {
            mock<SynchronousFunctions>().apply {
                byte(3)
                verify(this).byte(any())
            }
        }

        @Test
        fun anyByteArray() {
            mock<SynchronousFunctions>().apply {
                byteArray(byteArrayOf(3, 4, 5))
                verify(this).byteArray(any())
            }
        }

        @Test
        fun anyShort() {
            mock<SynchronousFunctions>().apply {
                short(3)
                verify(this).short(any())
            }
        }

        @Test
        fun anyShortArray() {
            mock<SynchronousFunctions>().apply {
                shortArray(shortArrayOf(3, 4, 5))
                verify(this).shortArray(any())
            }
        }

        @Test
        fun anyInt() {
            mock<SynchronousFunctions>().apply {
                int(3)
                verify(this).int(any())
            }
        }

        @Test
        fun anyIntArray() {
            mock<SynchronousFunctions>().apply {
                intArray(intArrayOf(3, 4, 5))
                verify(this).intArray(any())
            }
        }

        @Test
        fun anyLong() {
            mock<SynchronousFunctions>().apply {
                long(3)
                verify(this).long(any())
            }
        }

        @Test
        fun anyLongArray() {
            mock<SynchronousFunctions>().apply {
                longArray(longArrayOf(3L, 4L, 5L))
                verify(this).longArray(any())
            }
        }

        @Test
        fun anyFloat() {
            mock<SynchronousFunctions>().apply {
                float(3f)
                verify(this).float(any())
            }
        }

        @Test
        fun anyFloatArray() {
            mock<SynchronousFunctions>().apply {
                floatArray(floatArrayOf(3f, 4f, 5f))
                verify(this).floatArray(any())
            }
        }

        @Test
        fun anyDouble() {
            mock<SynchronousFunctions>().apply {
                double(3.0)
                verify(this).double(any())
            }
        }

        @Test
        fun anyDoubleArray() {
            mock<SynchronousFunctions>().apply {
                doubleArray(doubleArrayOf(3.0, 4.0, 5.0))
                verify(this).doubleArray(any())
            }
        }

        @Test
        fun anyClosedClass() {
            mock<SynchronousFunctions>().apply {
                closed(Closed())
                verify(this).closed(any())
            }
        }

        @Test
        fun anyClassClosedClass() {
            mock<SynchronousFunctions>().apply {
                classClosed(Closed::class.java)
                verify(this).classClosed(any())
            }
        }

        @Test
        fun anySuspendMethodClosedClass() = runTest {
            val mock = mock<SuspendFunctions>()

            mock.closed(Closed())

            verify(mock).closed(any())
        }

        /** https://github.com/nhaarman/mockito-kotlin/issues/27 */
        @Test
        fun anyThrowableWithSingleThrowableConstructor() {
            mock<SynchronousFunctions>().apply {
                throwableClass(ThrowableClass(IOException()))
                verify(this).throwableClass(any())
            }
        }

        @Test
        fun anyValueClass() {
            mock<SynchronousFunctions>().apply {
                valueClass(ValueClass("Content"))
                verify(this).valueClass(any())
            }
        }

        @Test
        fun anyNeverVerifiesForNullValue() {
            mock<SynchronousFunctions>().apply {
                nullableString(null)
                verify(this, never()).nullableString(any())
            }
        }
    }

    class SpecialAnyMatchersTest {
        @Test
        fun anyClassArray() {
            mock<SynchronousFunctions>().apply {
                closedArray(arrayOf(Closed()))
                verify(this).closedArray(anyArray())
            }
        }

        @Test
        fun anyNullableClassArray() {
            mock<SynchronousFunctions>().apply {
                closedNullableArray(arrayOf(Closed(), null))
                verify(this).closedNullableArray(anyArray())
            }
        }

        @Test
        fun anyStringVararg() {
            mock<SynchronousFunctions>().apply {
                closedVararg(Closed(), Closed())
                verify(this).closedVararg(anyVararg())
            }
        }

        @Test
        fun anyVarargMatching() {
            mock<SynchronousFunctions>().apply {
                whenever(varargBooleanResult(anyVararg())).thenReturn(true)
                expect(varargBooleanResult()).toBe(true)
            }
        }

        @Test
        fun anyValueClass_withValueClass() {
            mock<SynchronousFunctions>().apply {
                valueClass(ValueClass("Content"))
                verify(this).valueClass(anyValueClass())
            }
        }

        @Test
        fun anyValueClass_withNonValueClass() {
            expectErrorWithMessage("kotlin.Float is not a value class.") on {
                mock<SynchronousFunctions>().apply {
                    float(10f)
                    // Should throw an error because Float is not a value class
                    float(anyValueClass())
                }
            }
        }

        @Test
        fun anyValueClass_withNestedValueClass() {
            mock<SynchronousFunctions>().apply {
                nestedValueClass(NestedValueClass(ValueClass("Content")))
                verify(this).nestedValueClass(anyValueClass())
            }
        }
    }

    class AnyOrNullMatchersTest {
        @Test
        fun anyOrNullString() {
            mock<SynchronousFunctions>().apply {
                string("")
                verify(this).string(anyOrNull())
            }
        }

        @Test
        fun anyOrNullNullableString() {
            mock<SynchronousFunctions>().apply {
                nullableString("")
                verify(this).nullableString(anyOrNull())
            }
        }

        @Test
        fun anyOrNullNullableStringNullValue() {
            mock<SynchronousFunctions>().apply {
                nullableString(null)
                verify(this).nullableString(anyOrNull())
            }
        }

        @Test
        fun anyOrNullBoolean() {
            mock<SynchronousFunctions>().apply {
                boolean(false)
                verify(this).boolean(anyOrNull())
            }
        }

        @Test
        fun anyOrNullByte() {
            mock<SynchronousFunctions>().apply {
                byte(3)
                verify(this).byte(anyOrNull())
            }
        }

        @Test
        fun anyOrNullChar() {
            mock<SynchronousFunctions>().apply {
                char('a')
                verify(this).char(anyOrNull())
            }
        }

        @Test
        fun anyOrNullShort() {
            mock<SynchronousFunctions>().apply {
                short(3)
                verify(this).short(anyOrNull())
            }
        }

        @Test
        fun anyOrNullInt() {
            mock<SynchronousFunctions>().apply {
                int(3)
                verify(this).int(anyOrNull())
            }
        }

        @Test
        fun anyOrNullLong() {
            mock<SynchronousFunctions>().apply {
                long(3)
                verify(this).long(anyOrNull())
            }
        }

        @Test
        fun anyOrNullFloat() {
            mock<SynchronousFunctions>().apply {
                float(3f)
                verify(this).float(anyOrNull())
            }
        }

        @Test
        fun anyOrNullDouble() {
            mock<SynchronousFunctions>().apply {
                double(3.0)
                verify(this).double(anyOrNull())
            }
        }

        @Test
        fun anyOrNullValueClass() {
            mock<SynchronousFunctions>().apply {
                valueClass(ValueClass("Content"))
                verify(this).valueClass(anyOrNull())
            }
        }

        @Test
        fun anyOrNullNullableValueClass() {
            mock<SynchronousFunctions>().apply {
                nullableValueClass(ValueClass("Content"))
                verify(this).nullableValueClass(anyOrNull())
            }
        }

        @Test
        fun anyOrNullNullableValueClassNullValue() {
            mock<SynchronousFunctions>().apply {
                nullableValueClass(null)
                verify(this).nullableValueClass(anyOrNull())
            }
        }
    }

    class EqMatchersTest {
        @Test
        fun eqString() {
            val value = "Value"
            mock<SynchronousFunctions>().apply {
                string(value)
                verify(this).string(eq(value))
            }
        }

        @Test
        fun eqBoolean() {
            val value = true
            mock<SynchronousFunctions>().apply {
                boolean(value)
                verify(this).boolean(eq(value))
            }
        }

        @Test
        fun eqBooleanArray() {
            val value = booleanArrayOf(true, false, false)
            mock<SynchronousFunctions>().apply {
                booleanArray(value)
                verify(this).booleanArray(eq(value))
            }
        }

        @Test
        fun eqChar() {
            val value = '3'
            mock<SynchronousFunctions>().apply {
                char(value)
                verify(this).char(eq(value))
            }
        }

        @Test
        fun eqCharArray() {
            val value = charArrayOf('3', '4', '5')
            mock<SynchronousFunctions>().apply {
                charArray(value)
                verify(this).charArray(eq(value))
            }
        }

        @Test
        fun eqByte() {
            val value: Byte = 3
            mock<SynchronousFunctions>().apply {
                byte(value)
                verify(this).byte(eq(value))
            }
        }

        @Test
        fun eqByteArray() {
            val value = byteArrayOf(3, 4, 5)
            mock<SynchronousFunctions>().apply {
                byteArray(value)
                verify(this).byteArray(eq(value))
            }
        }

        @Test
        fun eqShort() {
            val value: Short = 3
            mock<SynchronousFunctions>().apply {
                short(value)
                verify(this).short(eq(value))
            }
        }

        @Test
        fun eqShortArray() {
            val value = shortArrayOf(3, 4, 5)
            mock<SynchronousFunctions>().apply {
                shortArray(value)
                verify(this).shortArray(eq(value))
            }
        }

        @Test
        fun eqInt() {
            val value = 3
            mock<SynchronousFunctions>().apply {
                int(value)
                verify(this).int(eq(value))
            }
        }

        @Test
        fun eqIntArray() {
            val value = intArrayOf(3, 4, 5)
            mock<SynchronousFunctions>().apply {
                intArray(value)
                verify(this).intArray(eq(value))
            }
        }

        @Test
        fun eqLong() {
            val value = 3L
            mock<SynchronousFunctions>().apply {
                long(value)
                verify(this).long(eq(value))
            }
        }

        @Test
        fun eqLongArray() {
            val value = longArrayOf(3L, 4L, 5L)
            mock<SynchronousFunctions>().apply {
                longArray(value)
                verify(this).longArray(eq(value))
            }
        }

        @Test
        fun eqFloat() {
            val value = 3f
            mock<SynchronousFunctions>().apply {
                float(value)
                verify(this).float(eq(value))
            }
        }

        @Test
        fun eqFloatArray() {
            val value = floatArrayOf(3f, 4f, 5f)
            mock<SynchronousFunctions>().apply {
                floatArray(value)
                verify(this).floatArray(eq(value))
            }
        }

        @Test
        fun eqDouble() {
            val value = 3.0
            mock<SynchronousFunctions>().apply {
                double(value)
                verify(this).double(eq(value))
            }
        }

        @Test
        fun eqDoubleArray() {
            val value = doubleArrayOf(3.0, 4.0, 5.0)
            mock<SynchronousFunctions>().apply {
                doubleArray(value)
                verify(this).doubleArray(eq(value))
            }
        }

        @Test
        fun eqClosedClass() {
            val value = Closed()
            mock<SynchronousFunctions>().apply {
                closed(value)
                verify(this).closed(eq(value))
            }
        }

        @Test
        fun eqClassClosedClass() {
            val clazz = Closed::class.java
            mock<SynchronousFunctions>().apply {
                classClosed(clazz)
                verify(this).classClosed(eq(clazz))
            }
        }

        @Test
        fun eqSuspendMethodClosedClass() = runTest {
            val mock = mock<SuspendFunctions>()
            val value = Closed()

            mock.closed(value)

            verify(mock).closed(eq(value))
        }

        @Test
        fun eqClassArray() {
            val value = arrayOf(Closed())
            mock<SynchronousFunctions>().apply {
                closedArray(value)
                verify(this).closedArray(eq(value))
            }
        }

        @Test
        fun eqNullableClassArray() {
            val value = arrayOf(Closed(), null)
            mock<SynchronousFunctions>().apply {
                closedNullableArray(value)
                verify(this).closedNullableArray(eq(value))
            }
        }

        @Test
        fun eqValueClass() {
            val valueClass = ValueClass("Content")
            mock<SynchronousFunctions>().apply {
                valueClass(valueClass)
                verify(this).valueClass(eq(valueClass))
            }
        }

        @Test
        fun eqNestedValueClass() {
            val nestedValueClass = NestedValueClass(ValueClass("Content"))
            mock<SynchronousFunctions>().apply {
                nestedValueClass(nestedValueClass)
                verify(this).nestedValueClass(eq(nestedValueClass))
            }
        }
    }

    class OtherMatchersTest {
        @Test
        fun listArgThat() {
            mock<SynchronousFunctions>().apply {
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
            mock<SynchronousFunctions>().apply {
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
            mock<SynchronousFunctions>().apply {
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
            mock<SynchronousFunctions>().apply {
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
            mock<SynchronousFunctions>().apply {
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
            mock<SynchronousFunctions>().apply {
                nullableString(null)

                expectErrorWithMessage("null").on {
                    verify(this).nullableString(check {})
                }
            }
        }

        @Test
        fun isA_withNonNullableString() {
            mock<SynchronousFunctions>().apply {
                string("")
                verify(this).string(isA<String>())
            }
        }

        @Test
        fun isA_withNullableString() {
            mock<SynchronousFunctions>().apply {
                nullableString("")
                verify(this).nullableString(isA<String>())
            }
        }

        @Test
        fun same_withNonNullArgument() {
            mock<SynchronousFunctions>().apply {
                string("")
                verify(this).string(same(""))
            }
        }

        @Test
        fun same_withNullableNonNullArgument() {
            mock<SynchronousFunctions>().apply {
                nullableString("")
                verify(this).nullableString(same(""))
            }
        }

        @Test
        fun same_withNullArgument() {
            mock<SynchronousFunctions>().apply {
                nullableString(null)
                verify(this).nullableString(same(null))
            }
        }

        @Test
        fun testVarargAnySuccess() {
            /* Given */
            val t = mock<SynchronousFunctions>()
            // a matcher to check if any of the varargs was equals to "b"
            val matcher = VarargAnyMatcher({ "b" == it }, String::class.java, true, false)

            /* When */
            whenever(t.varargBooleanResult(argThat(matcher))).thenAnswer(matcher)

            /* Then */
            expect(t.varargBooleanResult("a", "b", "c")).toBe(true)
        }

        @Test
        fun testVarargAnyFail() {
            /* Given */
            val t = mock<SynchronousFunctions>()
            // a matcher to check if any of the varargs was equals to "d"
            val matcher = VarargAnyMatcher({ "d" == it }, String::class.java, true, false)

            /* When */
            whenever(t.varargBooleanResult(argThat(matcher))).thenAnswer(matcher)

            /* Then */
            expect(t.varargBooleanResult("a", "b", "c")).toBe(false)
        }

        /** https://github.com/nhaarman/mockito-kotlin/issues/328 */
        @Test
        fun testRefEqForNonNullableParameter() {
            mock<SynchronousFunctions>().apply {
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
            private val clazz: Class<T>,
            private val success: R,
            private val failure: R
        ) : ArgumentMatcher<T>, Answer<R> {
            private var anyMatched = false

            override fun matches(t: T): Boolean {
                @Suppress("UNCHECKED_CAST") // No idea how to solve this better
                anyMatched = (t as Array<T>).any(match)
                return anyMatched
            }

            override fun answer(i: InvocationOnMock) = if (anyMatched) success else failure

            override fun type(): Class<*> = java.lang.reflect.Array.newInstance(clazz, 0).javaClass
        }
    }
}
