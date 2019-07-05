package test

import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import com.nhaarman.expect.fail
import com.nhaarman.mockitokotlin2.UseConstructor.Companion.parameterless
import com.nhaarman.mockitokotlin2.UseConstructor.Companion.withArguments
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.mockito.Mockito
import org.mockito.exceptions.verification.WantedButNotInvoked
import org.mockito.listeners.InvocationListener
import org.mockito.mock.SerializableMode.BASIC
import java.io.PrintStream
import java.io.Serializable
import java.util.*

class MockingTest : TestBase() {

    private lateinit var propertyInterfaceVariable: MyInterface
    private lateinit var propertyClassVariable: MyClass

    @Test
    fun localInterfaceValue() {
        /* When */
        val instance: MyInterface = mock()

        /* Then */
        expect(instance).toNotBeNull()
    }

    @Test
    fun propertyInterfaceVariable() {
        /* When */
        propertyInterfaceVariable = mock()

        /* Then */
        expect(propertyInterfaceVariable).toNotBeNull()
    }

    @Test
    fun localClassValue() {
        /* When */
        val instance: MyClass = mock()

        /* Then */
        expect(instance).toNotBeNull()
    }

    @Test
    fun propertyClassVariable() {
        /* When */
        propertyClassVariable = mock()

        /* Then */
        expect(propertyClassVariable).toNotBeNull()
    }

    @Test
    fun untypedVariable() {
        /* When */
        val instance = mock<MyClass>()

        expect(instance).toNotBeNull()
    }

    @Test
    fun deepStubs() {
        val cal: Calendar = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
        whenever(cal.time.time).thenReturn(123L)
        expect(cal.time.time).toBe(123L)
    }


    @Test
    fun testMockStubbing_lambda() {
        /* Given */
        val mock = mock<Open>() {
            on { stringResult() } doReturn "A"
        }

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("A")
    }

    @Test
    fun testMockStubbing_normalOverridesLambda() {
        /* Given */
        val mock = mock<Open>() {
            on { stringResult() }.doReturn("A")
        }
        whenever(mock.stringResult()).thenReturn("B")

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("B")
    }


    @Test
    fun mock_withCustomDefaultAnswer_parameterName() {
        /* Given */
        val mock = mock<Methods>(defaultAnswer = Mockito.RETURNS_SELF)

        /* When */
        val result = mock.builderMethod()

        /* Then */
        expect(result).toBe(mock)
    }

    @Test
    fun mock_withSettingsAPI_extraInterfaces() {
        /* Given */
        val mock = mock<Methods>(
              extraInterfaces = arrayOf(ExtraInterface::class)
        )

        /* Then */
        expect(mock).toBeInstanceOf<ExtraInterface>()
    }

    @Test
    fun mock_withSettingsAPI_name() {
        /* Given */
        val mock = mock<Methods>(name = "myName")

        /* When */
        expectErrorWithMessage("myName.stringResult()") on {
            verify(mock).stringResult()
        }
    }

    @Test
    fun mock_withSettingsAPI_defaultAnswer() {
        /* Given */
        val mock = mock<Methods>(defaultAnswer = Mockito.RETURNS_MOCKS)

        /* When */
        val result = mock.nonDefaultReturnType()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun mock_withSettingsAPI_serializable() {
        /* Given */
        val mock = mock<Methods>(serializable = true)

        /* Then */
        expect(mock).toBeInstanceOf<Serializable>()
    }

    @Test
    fun mock_withSettingsAPI_serializableMode() {
        /* Given */
        val mock = mock<Methods>(serializableMode = BASIC)

        /* Then */
        expect(mock).toBeInstanceOf<Serializable>()
    }

    @Test
    fun mock_withSettingsAPI_verboseLogging() {
        /* Given */
        val out = mock<PrintStream>()
        System.setOut(out)
        val mock = mock<Methods>(verboseLogging = true)

        try {
            /* When */
            verify(mock).stringResult()
            fail("Expected an exception")
        } catch (e: WantedButNotInvoked) {
            /* Then */
            verify(out).println("methods.stringResult();")
        }
    }

    @Test
    fun mock_withSettingsAPI_invocationListeners() {
        /* Given */
        var bool = false
        val mock = mock<Methods>(invocationListeners = arrayOf(InvocationListener { bool = true }))

        /* When */
        mock.stringResult()

        /* Then */
        expect(bool).toHold()
    }

    @Test
    fun mock_withSettingsAPI_stubOnly() {
        /* Given */
        val mock = mock<Methods>(stubOnly = true)

        /* Expect */
        expectErrorWithMessage("is a stubOnly() mock") on {

            /* When */
            verify(mock).stringResult()
        }
    }

    @Test
    fun mock_withSettingsAPI_useConstructor() {
        /* Given */
        expectErrorWithMessage("Unable to create mock instance of type ") on {
            mock<ThrowingConstructor>(useConstructor = parameterless()) {}
        }
    }

    @Test
    fun mock_withSettingsAPI_useConstructorWithArguments_failing() {
        /* Given */
        expectErrorWithMessage("Unable to create mock instance of type ") on {
            mock<ThrowingConstructorWithArgument>(useConstructor = withArguments("Test")) {}
        }
    }

    @Test
    fun mock_withSettingsAPI_useConstructorWithArguments() {
        /* When */
        val result = mock<NonThrowingConstructorWithArgument>(useConstructor = withArguments("Test")) {}

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun mockStubbing_withSettingsAPI_extraInterfaces() {
        /* Given */
        val mock = mock<Methods>(extraInterfaces = arrayOf(ExtraInterface::class)) {}

        /* Then */
        expect(mock).toBeInstanceOf<ExtraInterface>()
    }

    @Test
    fun mockStubbing_withSettingsAPI_name() {
        /* Given */
        val mock = mock<Methods>(name = "myName") {}

        /* When */
        expectErrorWithMessage("myName.stringResult()") on {
            verify(mock).stringResult()
        }
    }

    @Test
    fun mockStubbing_withSettingsAPIAndStubbing_name() {
        /* Given */
        val mock = mock<Methods>(name = "myName") {
            on { nullableStringResult() } doReturn "foo"
        }

        /* When */
        val result = mock.nullableStringResult()

        /* Then */
        expect(result).toBe("foo")
    }

    @Test
    fun mockStubbing_withSettingsAPI_defaultAnswer() {
        /* Given */
        val mock = mock<Methods>(defaultAnswer = Mockito.RETURNS_MOCKS) {}

        /* When */
        val result = mock.nonDefaultReturnType()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun mockStubbing_withSettingsAPI_serializable() {
        /* Given */
        val mock = mock<Methods>(serializable = true) {}

        /* Then */
        expect(mock).toBeInstanceOf<Serializable>()
    }

    @Test
    fun mockStubbing_withSettingsAPI_serializableMode() {
        /* Given */
        val mock = mock<Methods>(serializableMode = BASIC) {}

        /* Then */
        expect(mock).toBeInstanceOf<Serializable>()
    }

    @Test
    fun mockStubbing_withSettingsAPI_verboseLogging() {
        /* Given */
        val out = mock<PrintStream>()
        System.setOut(out)
        val mock = mock<Methods>(verboseLogging = true) {}

        try {
            /* When */
            verify(mock).stringResult()
            fail("Expected an exception")
        } catch (e: WantedButNotInvoked) {
            /* Then */
            verify(out).println("methods.stringResult();")
        }
    }

    @Test
    fun mockStubbing_withSettingsAPI_invocationListeners() {
        /* Given */
        var bool = false
        val mock = mock<Methods>(invocationListeners = arrayOf(InvocationListener { bool = true })) {}

        /* When */
        mock.stringResult()

        /* Then */
        expect(bool).toHold()
    }

    @Test
    fun mockStubbing_withSettingsAPI_stubOnly() {
        /* Given */
        val mock = mock<Methods>(stubOnly = true) {}

        /* Expect */
        expectErrorWithMessage("is a stubOnly() mock") on {

            /* When */
            verify(mock).stringResult()
        }
    }

    @Test
    fun mockStubbing_withSettingsAPI_useConstructor() {
        /* Given */
        expectErrorWithMessage("Unable to create mock instance of type ") on {
            mock<ThrowingConstructor>(useConstructor = parameterless()) {}
        }
    }

    private interface MyInterface
    private open class MyClass
}