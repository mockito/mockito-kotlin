/*
 * Copyright 2016 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.nhaarman.expect.expect
import com.nhaarman.mockito_kotlin.spy
import org.junit.Test
import org.mockito.exceptions.base.MockitoException

class SpyTest {

    private val interfaceInstance: MyInterface = MyClass()
    private val openClassInstance: MyClass = MyClass()
    private val closedClassInstance: ClosedClass = ClosedClass()

    @Test
    fun spyInterfaceInstance() {
        /* When */
        val result = spy(interfaceInstance)

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test
    fun spyOpenClassInstance() {
        /* When */
        val result = spy(openClassInstance)

        /* Then */
        expect(result).toNotBeNull()
    }

    @Test(expected = MockitoException::class)
    fun spyClosedClassInstance() {
        /* When */
        spy(closedClassInstance)
    }

    private interface MyInterface
    private open class MyClass : MyInterface
    private class ClosedClass
}

