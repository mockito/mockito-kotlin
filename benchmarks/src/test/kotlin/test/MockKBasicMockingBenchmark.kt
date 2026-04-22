package test

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MockKBasicMockingBenchmark {

  @Test
  fun basicMocking() {
    BenchmarkHarness.run("MOCKK: basicMocking") { i ->
      val service: DataService = mockk()

      every { service.fetchString(any()) } returns "result_$i"
      every { service.fetchInt(any()) } returns (i * 42)
      every { service.process(any(), any()) } returns listOf("a", "b", "c")
      every { service.validate(any()) } returns (i % 2 == 0)
      every { service.transform(any(), any()) } returns listOf(1.0, 2.0, 3.0)

      val str = service.fetchString("key_$i")
      val num = service.fetchInt(i)
      val processed = service.process("input_$i", i)
      val valid = service.validate(mapOf("id" to i, "name" to "test_$i"))
      val transformed = service.transform(listOf(1, 2, 3), i.toDouble())

      verify { service.fetchString("key_$i") }
      verify { service.fetchInt(i) }
      verify { service.process("input_$i", i) }
      verify { service.validate(any()) }
      verify { service.transform(listOf(1, 2, 3), i.toDouble()) }

      require(str == "result_$i")
      require(num == i * 42)
      require(processed.size == 3)
      require(valid == (i % 2 == 0))
      require(transformed.size == 3)
    }
  }
}
