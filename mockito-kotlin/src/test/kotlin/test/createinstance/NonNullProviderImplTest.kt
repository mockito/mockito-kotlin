package test.createinstance

import com.nhaarman.mockito_kotlin.*
import com.nhaarman.mockito_kotlin.createinstance.NonNullProvider
import com.nhaarman.mockito_kotlin.createinstance.NonNullProviderImpl
import org.junit.Test
import test.TestBase

class NonNullProviderImplTest : TestBase() {

    @Test(expected = IllegalStateException::class)
    fun createInstance_withoutDelegates_throwsException() {
        /* Given */
        val nonNullProvider = NonNullProviderImpl(emptyList())

        /* When */
        nonNullProvider.createInstance(String::class)
    }

    @Test
    fun createInstance_withSingleDelegate_callsThatDelegate() {
        /* Given */
        val delegate = mock<NonNullProvider>()
        val nonNullProvider = NonNullProviderImpl(listOf(delegate))

        /* When */
        nonNullProvider.createInstance(String::class)

        /* Then */
        verify(delegate).createInstance(String::class)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun createInstance_withSingleDelegateThatFails_throwsException() {
        /* Given */
        val delegate = mock<NonNullProvider> {
            on { createInstance<String>(any()) } doThrow UnsupportedOperationException()
        }
        val nonNullProvider = NonNullProviderImpl(listOf(delegate))

        /* When */
        nonNullProvider.createInstance(String::class)
    }

    @Test
    fun createInstance_withTwoDelegates_doesNotCallSecondDelegate() {
        /* Given */
        val delegate1 = mock<NonNullProvider>()
        val delegate2 = mock<NonNullProvider>()
        val nonNullProvider = NonNullProviderImpl(listOf(delegate1, delegate2))

        /* When */
        nonNullProvider.createInstance(String::class)

        /* Then */
        verify(delegate1).createInstance(String::class)
        verify(delegate2, never()).createInstance(String::class)
    }

    @Test
    fun createInstance_withTwoDelegates_firstDelegateFails_callsSecondDelegate() {
        /* Given */
        val delegate1 = mock<NonNullProvider> {
            on { createInstance<String>(any()) } doThrow UnsupportedOperationException()
        }
        val delegate2 = mock<NonNullProvider>()
        val nonNullProvider = NonNullProviderImpl(listOf(delegate1, delegate2))

        /* When */
        nonNullProvider.createInstance(String::class)

        /* Then */
        verify(delegate1).createInstance(String::class)
        verify(delegate2).createInstance(String::class)
    }
}

