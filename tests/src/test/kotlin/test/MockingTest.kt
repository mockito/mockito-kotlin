package test

import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import com.nhaarman.expect.fail
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.UseConstructor.Companion.parameterless
import org.mockito.kotlin.UseConstructor.Companion.withArguments
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any
import org.junit.Test
import org.mockito.Mockito
import org.mockito.MockitoSession
import org.mockito.exceptions.verification.WantedButNotInvoked
import org.mockito.invocation.DescribedInvocation
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mockConstruction
import org.mockito.kotlin.mockStatic
import org.mockito.listeners.InvocationListener
import org.mockito.mock.SerializableMode.BASIC
import org.mockito.quality.Strictness
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
        whenever { cal.time.time }.thenReturn(123L)
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
        val mock = mock<Open> {
            on { stringResult() } doReturn "A"
        }
        whenever { mock.stringResult() }.thenReturn("B")

        /* When */
        val result = mock.stringResult()

        /* Then */
        expect(result).toBe("B")
    }

    @Test
    fun mock_withCustomDefaultAnswer_parameterName() {
        /* Given */
        val mock = mock<SynchronousFunctions>(defaultAnswer = Mockito.RETURNS_SELF)

        /* When */
        val result = mock.builderMethod()

        /* Then */
        expect(result).toBe(mock)
    }

    @Test
    fun mock_withSettingsAPI_extraInterfaces() {
        /* Given */
        val mock = mock<SynchronousFunctions>(
              extraInterfaces = arrayOf(ExtraInterface::class)
        )

        /* Then */
        expect(mock).toBeInstanceOf<ExtraInterface>()
    }

    @Test
    fun mock_withSettingsAPI_name() {
        /* Given */
        val mock = mock<SynchronousFunctions>(name = "myName")

        /* When */
        expectErrorWithMessage("myName.stringResult()") on {
            verify(mock).stringResult()
        }
    }

    @Test
    fun mock_withSettingsAPI_defaultAnswer() {
        /* Given */
        val mock = mock<SynchronousFunctions>(defaultAnswer = Mockito.RETURNS_MOCKS)

        /* When */
        val result = mock.nonDefaultReturnType()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun mock_withSettingsAPI_serializable() {
        /* Given */
        val mock = mock<SynchronousFunctions>(serializable = true)

        /* Then */
        expect(mock).toBeInstanceOf<Serializable>()
    }

    @Test
    fun mock_withSettingsAPI_serializableMode() {
        /* Given */
        val mock = mock<SynchronousFunctions>(serializableMode = BASIC)

        /* Then */
        expect(mock).toBeInstanceOf<Serializable>()
    }

    @Test
    fun mock_withSettingsAPI_verboseLogging() {
        /* Given */
        val out = mock<PrintStream>()
        System.setOut(out)
        val mock = mock<SynchronousFunctions>(verboseLogging = true)


        /* When, Then */
        assertThrows<WantedButNotInvoked> {
            verify(mock).stringResult()
        }
        argumentCaptor<DescribedInvocation>().apply {
            verify(out).println(capture())
            expect(lastValue.toString()).toBe("synchronousFunctions.stringResult();")
        }
    }

    @Test
    fun mock_withSettingsAPI_invocationListeners() {
        /* Given */
        var bool = false
        val mock = mock<SynchronousFunctions>(invocationListeners = arrayOf(InvocationListener { bool = true }))

        /* When */
        mock.stringResult()

        /* Then */
        expect(bool).toHold()
    }

    @Test
    fun mock_withSettingsAPI_stubOnly() {
        /* Given */
        val mock = mock<SynchronousFunctions>(stubOnly = true)

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
    fun mock_strictness_default() {
        /* Given */
        val session = Mockito.mockitoSession().strictness(Strictness.STRICT_STUBS).startMocking()

        /* When */
        val result = mock<SynchronousFunctions>()
        whenever(result.intResult()).thenReturn(42)

        /* Then */
        expectErrorWithMessage("Unnecessary stubbings detected") on {
            session.finishMocking()
        }
    }

    @Test
    fun mock_withSettingsAPI_lenient() {
        /* Given */
        val session = Mockito.mockitoSession().strictness(Strictness.STRICT_STUBS).startMocking()

        /* When */
        val result = mock<SynchronousFunctions>(lenient = true)
        whenever(result.intResult()).thenReturn(42)

        /* Then */
        // Verify no "Unnecessary stubbings detected" exception
        session.finishMocking()
    }

    @Test
    fun mock_withSettingsAPI_strictness_lenient() {
        /* Given */
        val session = Mockito.mockitoSession().strictness(Strictness.STRICT_STUBS).startMocking()

        /* When */
        val result = mock<SynchronousFunctions>(strictness = Strictness.LENIENT)
        whenever(result.intResult()).thenReturn(42)

        /* Then */
        // Verify no "Unnecessary stubbings detected" exception
        session.finishMocking()
    }

    @Test
    fun mockStubbing_withSettingsAPI_extraInterfaces() {
        /* Given */
        val mock = mock<SynchronousFunctions>(extraInterfaces = arrayOf(ExtraInterface::class)) {}

        /* Then */
        expect(mock).toBeInstanceOf<ExtraInterface>()
    }

    @Test
    fun mockStubbing_withSettingsAPI_name() {
        /* Given */
        val mock = mock<SynchronousFunctions>(name = "myName") {}

        /* When */
        expectErrorWithMessage("myName.stringResult()") on {
            verify(mock).stringResult()
        }
    }

    @Test
    fun mockStubbing_withSettingsAPIAndStubbing_name() {
        /* Given */
        val mock = mock<SynchronousFunctions>(name = "myName") {
            on { nullableStringResult() } doReturn "foo"
        }

        /* When */
        val result = mock.nullableStringResult()

        /* Then */
        expect(result).toBe("foo")
    }

    @Test
    fun mockSuspendFunction_withClosedBooleanReturn_name() = runTest {
        /* Given */
        val mock = mock<SuspendFunctions>(name = "myName") {
            on { closedBooleanResult(any()) } doReturn true
        }

        /* When */
        val result = mock.closedBooleanResult(Closed())

        /* Then */
        expect(result).toBe(true)
    }

    @Test
    fun mockSuspendFunction_withClassClosedBooleanReturn_name() = runTest {
        /* Given */
        val mock = mock<SuspendFunctions>(name = "myName") {
            on { classClosedBooleanResult(any()) } doReturn true
        }

        /* When */
        val result = mock.classClosedBooleanResult(Closed::class.java)

        /* Then */
        expect(result).toBe(true)
    }

    @Test
    fun mockStubbing_withSettingsAPI_defaultAnswer() {
        /* Given */
        val mock = mock<SynchronousFunctions>(defaultAnswer = Mockito.RETURNS_MOCKS) {}

        /* When */
        val result = mock.nonDefaultReturnType()

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun mockStubbing_withSettingsAPI_serializable() {
        /* Given */
        val mock = mock<SynchronousFunctions>(serializable = true) {}

        /* Then */
        expect(mock).toBeInstanceOf<Serializable>()
    }

    @Test
    fun mockStubbing_withSettingsAPI_serializableMode() {
        /* Given */
        val mock = mock<SynchronousFunctions>(serializableMode = BASIC) {}

        /* Then */
        expect(mock).toBeInstanceOf<Serializable>()
    }

    @Test
    fun mockStubbing_withSettingsAPI_verboseLogging() {
        /* Given */
        val out = mock<PrintStream>()
        System.setOut(out)
        val mock = mock<SynchronousFunctions>(verboseLogging = true) {}

        try {
            /* When */
            verify(mock).stringResult()
            fail("Expected an exception")
        } catch (_: WantedButNotInvoked) {
            /* Then */
            argumentCaptor<DescribedInvocation>().apply {
                verify(out).println(capture())
                expect(lastValue.toString()).toBe("synchronousFunctions.stringResult();")
            }
        }
    }

    @Test
    fun mockStubbing_withSettingsAPI_invocationListeners() {
        /* Given */
        var bool = false
        val mock = mock<SynchronousFunctions>(invocationListeners = arrayOf(InvocationListener { bool = true })) {}

        /* When */
        mock.stringResult()

        /* Then */
        expect(bool).toHold()
    }

    @Test
    fun mockStubbing_withSettingsAPI_stubOnly() {
        /* Given */
        val mock = mock<SynchronousFunctions>(stubOnly = true) {}

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

    @Test
    fun mockStatic_stubbing() {
        mockStatic<SomeObject>().use { mockedStatic ->
            mockedStatic.whenever { SomeObject.aStaticMethodReturningString() }.thenReturn("Hello")

            expect(SomeObject.aStaticMethodReturningString()).toBe("Hello")

            mockedStatic.verify { SomeObject.aStaticMethodReturningString() }
        }
    }

    @Test
    fun mockStatic_defaultAnswer_stubbing() {
        mockStatic<SomeObject>(defaultAnswer = Mockito.CALLS_REAL_METHODS).use {
            expect(SomeObject.aStaticMethodReturningString()).toBe("Some Value")
        }
    }

    @Test
    fun mockConstruction_basic() {
        mockConstruction<Open>().use { mockedConstruction ->
            val open = Open()

            expect(mockedConstruction.constructed()).toHaveSize(1)
            expect(mockedConstruction.constructed().first()).toBeTheSameAs(open)
        }
    }

    @Test
    fun mockConstruction_withInitializer() {
        mockConstruction<Open> { mock, _ ->
            whenever { mock.stringResult() }.thenReturn("Hello")
        }.use {
            val open = Open()

            expect(open.stringResult()).toBe("Hello")
        }
    }


    private interface MyInterface
    private open class MyClass
}
