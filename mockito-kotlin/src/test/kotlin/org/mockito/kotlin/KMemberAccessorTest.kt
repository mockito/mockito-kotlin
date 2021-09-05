package org.mockito.kotlin

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class KMemberAccessorTest {
    @Mock
    private lateinit var mockedDependency: MyDependency
    @Spy
    private val spiedDependency: MyOtherDependency = MyOtherDependency()
    @InjectMocks
    private lateinit var underTest: MyClass

    @Test
    fun `Should inject dependency`() {
        underTest.run()

        verify(mockedDependency).doSomething("Somebody")
        verify(spiedDependency).doNothing()
    }
}

open class MyDependency {
    open fun doSomething(name: String = "jpalacios") = println("$name - I did something")
}

open class MyOtherDependency {
    open fun doNothing(name: String = "ponyloky") = println("$name - I do nothing")
}

class NotMockedDependency {
    fun doNotMocked() = println("This is not mocked")
}

open class MyClass(
    private val myDependency: MyDependency = MyDependency(),
    private val myOtherDependency: MyOtherDependency = MyOtherDependency(),
    private val myNotMockedDependency: NotMockedDependency = NotMockedDependency()
) {
    fun run() {
        myDependency.doSomething("Somebody")

        myOtherDependency.doNothing()

        myNotMockedDependency.doNotMocked()
    }
}