package test

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mockObject
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class MockitoObjectMockingBenchmark {

  @Test
  fun objectMocking() {
    BenchmarkHarness.run("MOCKITO: objectMocking") { i ->
      mockObject(ServiceRegistry).use {
        whenever(ServiceRegistry.lookup(any())).thenReturn("mocked_$i")
        whenever(ServiceRegistry.isRegistered(any())).thenReturn(i % 2 == 0)
        whenever(ServiceRegistry.count()).thenReturn(i)

        val result = ServiceRegistry.lookup("svc_$i")
        val registered = ServiceRegistry.isRegistered("svc_$i")
        val count = ServiceRegistry.count()

        verify(ServiceRegistry).lookup(eq("svc_$i"))
        verify(ServiceRegistry).isRegistered(eq("svc_$i"))
        verify(ServiceRegistry).count()

        require(result == "mocked_$i")
        require(registered == (i % 2 == 0))
        require(count == i)
      }
    }
  }
}
