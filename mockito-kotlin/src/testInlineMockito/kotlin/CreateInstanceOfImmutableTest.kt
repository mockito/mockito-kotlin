/*
 * The MIT License
 *
 * Copyright (c) 2016 Ian J. De Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import com.nhaarman.expect.fail
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.math.BigInteger

/**
 * We are seeing the following:
 *
 *     java.lang.ClassCastException: java.math.BigInteger cannot be cast to org.mockito.internal.creation.bytebuddy.MockAccess
 *
 *     at com.nhaarman.mockito_kotlin.CreateInstanceKt.uncheckedMock(CreateInstance.kt:175)
 *     at com.nhaarman.mockito_kotlin.CreateInstanceKt.createInstance(CreateInstance.kt:58)
 *
 * when we use Mockito's new (incubating) feature that allows us to access final
 * classes and methods.  This feature is enabled by including the file
 * `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` with
 * the line `mock-maker-inline` inside (see
 * http://hadihariri.com/2016/10/04/Mocking-Kotlin-With-Mockito/).  Use of this
 * feature (or something like PowerMock) is vital when working with kotlin as it
 * prevents us from having to mark every class we wish to mock and every
 * function we wish to stub as open.
 *
 * This ClassCastException is similarly observed using PowerMock (according to
 * issue #37).
 *
 * The root of the bug is likely in the different implementations of the
 * MockMaker.  The default MockMaker subclasses and therefore always produces a
 * MockAccess object (see
 * [org.mockito.internal.creation.bytebuddy.SubclassByteBuddyMockMaker]).  When
 * we use the inline MockMaker we may not get a MockAccess object (I'd wager we
 * never get one for a non-open Kotlin object using this method since
 * subclassing is not permitted--see
 * [org.mockito.internal.creation.bytebuddy.InlineByteBuddyMockMaker])
 */
class CreateInstanceOfImmutableTest
{
    // ------------------------------------------------------------------------
    //  Infrastructure Needed For Tests
    // ------------------------------------------------------------------------
    /**
     * The class to be mocked in our test.
     */
    class ClassToBeMocked
    {
        @Suppress("UNUSED_PARAMETER")
        fun doSomethingElse(value: BigInteger): BigInteger
        {
            throw IllegalStateException("This should not be called!")
        }
    }

    /**
     * In our tests, this will be the object that we invoke (our proxy object
     * under test).
     *
     * @param dependentObject  the object that will be mocked.
     */
    class DemonstrationClass(val dependentObject: ClassToBeMocked)
    {
        fun doSomethingCool(): BigInteger
        {
            val initialValue = BigInteger.TEN
            return dependentObject.doSomethingElse(initialValue)
        }
    }

    // ------------------------------------------------------------------------
    //  Test Cases
    // ------------------------------------------------------------------------
    @Test
    fun doSomethingCoolTestWithBuilderMock()
    {
        doSomethingCoolTestHelper {
            expectedResult: BigInteger ->
                mock<ClassToBeMocked> {
                    on { doSomethingElse(any()) } doReturn (expectedResult)
                }
        }
    }

    @Test
    fun doSomethingCoolTestWithWheneverMock()
    {
        doSomethingCoolTestHelper {
            val dependentInstance = mock<ClassToBeMocked>()
            whenever(dependentInstance.doSomethingElse(any())).doReturn( it )
            dependentInstance
        }
    }

    @Test
    fun doSomethingCoolTestWithMickitoWhen()
    {
        doSomethingCoolTestHelper {
            val dependentInstance = mock<ClassToBeMocked>()
            Mockito.`when`(dependentInstance.doSomethingElse(any())).doReturn( it )
            dependentInstance
        }
    }


    // ------------------------------------------------------------------------
    //  Helper Methods
    // ------------------------------------------------------------------------
    /**
     * The actual body of the test.  This takes in a mock object generator and
     * uses that to make the mock we are testing with.
     *
     * @param mockGenerator  the lambda that generates the mocked object
     */
    private fun doSomethingCoolTestHelper( mockGenerator: (BigInteger) -> ClassToBeMocked )
    {
        validateTestSetup()

        val expectedResult = BigInteger.ONE
        val dependentInstance = mockGenerator.invoke(expectedResult)

        val objectUnderTest = DemonstrationClass(dependentInstance)
        val result = objectUnderTest.doSomethingCool()

        Assert.assertEquals(expectedResult, result)
    }


    /**
     * Checks to make sure that the tests are still valid.
     */
    private fun validateTestSetup()
    {
        // Ensure the test is still valid:
        try
        {
            val dependentInstance = ClassToBeMocked()
            dependentInstance.doSomethingElse(BigInteger.ZERO)
        }
        catch (e: IllegalStateException)
        {
            return
        }

        fail("Someone changed our set-up and the object to be mocked no longer throws an exception.  Fix this to ensure that our tests are valid.")
    }
}
