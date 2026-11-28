package test

import com.nhaarman.expect.expect
import java.util.*
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.stubbing.OngoingStubbing

class GenericsTest {
    private open class A(val value: Int) {
        override fun equals(other: Any?): Boolean = Objects.equals(value, (other as? A)?.value)

        override fun hashCode(): Int = value

        override fun toString(): String = "A($value)"
    }

    private class B(value: Int) : A(value) {
        override fun toString(): String = "B($value)"
    }

    @Test
    fun `whenever should stub no-arg function`() {
        val mock: GenericMethods<A> = mock()
        whenever(mock.genericMethod()).thenReturn(A(2))

        val result = mock.genericMethod()

        expect(result).toBe(A(2))
    }

    @Test
    fun `whenever should stub function with arg`() {
        val mock: GenericMethods<A> = mock()
        whenever(mock.genericArg(any())).thenReturn(A(2))

        val result = mock.genericArg(A(1))

        expect(result).toBe(A(2))
    }

    @Test
    fun `whenever should stub function with sub-class arg (with typehint)`() {
        val mock: GenericMethods<A> = mock()

        // compiler error, as this call resolves to whenever(methodCall: suspend () -> T)
        // whenever(mock.subclassArgMethod(any())).thenReturn(B(2))
        whenever(mock.genericSubclassArg(any<B>())).thenReturn(B(2))

        val result = mock.genericSubclassArg(B(1))

        expect(result).toBe(B(2))
    }

    @Test
    fun `whenever should stub function with sub-class arg (no overload)`() {
        val mock: GenericMethods<A> = mock()

        wheneverWithoutOverload(mock.genericSubclassArg(any())).thenReturn(B(2))

        val result = mock.genericSubclassArg(B(1))

        expect(result).toBe(B(2))
    }

    fun <T> wheneverWithoutOverload(methodCall: T): OngoingStubbing<T> {
        return whenever(methodCall)
    }
}
