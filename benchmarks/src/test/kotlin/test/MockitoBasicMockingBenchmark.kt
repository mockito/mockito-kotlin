package test

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class MockitoBasicMockingBenchmark {

  @Test
  fun basicMocking() {
    BenchmarkHarness.run("MOCKITO: basicMocking") { i ->
      val service: DataService = mock()

      whenever(service.fetchString(any())).thenReturn("result_$i")
      whenever(service.fetchInt(any())).thenReturn(i * 42)
      whenever(service.process(any(), any())).thenReturn(listOf("a", "b", "c"))
      whenever(service.validate(any())).thenReturn(i % 2 == 0)
      whenever(service.transform(any(), any())).thenReturn(listOf(1.0, 2.0, 3.0))

      val str = service.fetchString("key_$i")
      val num = service.fetchInt(i)
      val processed = service.process("input_$i", i)
      val valid = service.validate(mapOf("id" to i, "name" to "test_$i"))
      val transformed = service.transform(listOf(1, 2, 3), i.toDouble())

      verify(service).fetchString(eq("key_$i"))
      verify(service).fetchInt(eq(i))
      verify(service).process(eq("input_$i"), eq(i))
      verify(service).validate(any())
      verify(service).transform(eq(listOf(1, 2, 3)), eq(i.toDouble()))

      require(str == "result_$i")
      require(num == i * 42)
      require(processed.size == 3)
      require(valid == (i % 2 == 0))
      require(transformed.size == 3)
    }
  }
}
