package test

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MockKHeavyMockingBenchmark {

  @Test
  fun heavyMocking() {
    BenchmarkHarness.run("MOCKK: heavyMocking") { i ->
      val resultSet = mockk<ResultSet>()
      every { resultSet.next() } returnsMany listOf(true, true, false)
      every { resultSet.getString(any<Int>()) } returns "row_$i"
      every { resultSet.getInt(any<Int>()) } returns i

      val statement = mockk<PreparedStatement>()
      every { statement.executeQuery() } returns resultSet
      every { statement.setInt(any(), any()) } returns Unit

      val connection = mockk<Connection>()
      every { connection.prepareStatement(any()) } returns statement
      every { connection.isClosed } returns false

      val stmt = connection.prepareStatement("SELECT * FROM t WHERE id = ?")
      stmt.setInt(1, i)
      val rs = stmt.executeQuery()

      val rows = mutableListOf<String>()
      while (rs.next()) {
        rows.add(rs.getString(1))
        rs.getInt(2)
      }

      verify { connection.prepareStatement("SELECT * FROM t WHERE id = ?") }
      verify { statement.setInt(1, i) }
      verify { statement.executeQuery() }

      require(rows.size == 2)
      require(rows[0] == "row_$i")

      val list = spyk(ArrayList<String>())
      list.add("a_$i")
      list.add("b_$i")
      list.add("c_$i")
      list.removeAt(1)

      verify { list.add("a_$i") }
      verify { list.add("b_$i") }
      verify { list.add("c_$i") }
      verify { list.removeAt(1) }

      require(list.size == 2)
      require(list[0] == "a_$i")
      require(list[1] == "c_$i")

      val map = spyk(HashMap<String, Int>())
      map["key_$i"] = i
      map["other_$i"] = i * 2
      val value = map["key_$i"]

      verify { map["key_$i"] = i }
      verify { map["other_$i"] = i * 2 }

      require(value == i)
      require(map.size == 2)
    }
  }
}
