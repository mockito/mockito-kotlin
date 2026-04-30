package test

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MockKObjectMockingBenchmark {

  @Test
  fun objectMocking() {
    BenchmarkHarness.run("MOCKK: objectMocking") { i ->
      mockkObject(ServiceRegistry)
      try {
        every { ServiceRegistry.lookup(any()) } returns "mocked_$i"
        every { ServiceRegistry.isRegistered(any()) } returns (i % 2 == 0)
        every { ServiceRegistry.count() } returns i

        val result = ServiceRegistry.lookup("svc_$i")
        val registered = ServiceRegistry.isRegistered("svc_$i")
        val count = ServiceRegistry.count()

        verify { ServiceRegistry.lookup("svc_$i") }
        verify { ServiceRegistry.isRegistered("svc_$i") }
        verify { ServiceRegistry.count() }

        require(result == "mocked_$i")
        require(registered == (i % 2 == 0))
        require(count == i)
      } finally {
        unmockkObject(ServiceRegistry)
      }
    }
  }
}
