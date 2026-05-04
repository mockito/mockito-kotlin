package test

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class MockitoHeavyMockingBenchmark {

  @Test
  fun heavyMocking() {
    BenchmarkHarness.run("MOCKITO: heavyMocking") { i ->
      val resultSet: ResultSet = mock()
      whenever(resultSet.next()).thenReturn(true, true, false)
      whenever(resultSet.getString(any<Int>())).thenReturn("row_$i")
      whenever(resultSet.getInt(any<Int>())).thenReturn(i)

      val statement: PreparedStatement = mock()
      whenever(statement.executeQuery()).thenReturn(resultSet)

      val connection: Connection = mock()
      whenever(connection.prepareStatement(any())).thenReturn(statement)
      whenever(connection.isClosed).thenReturn(false)

      val stmt = connection.prepareStatement("SELECT * FROM t WHERE id = ?")
      stmt.setInt(1, i)
      val rs = stmt.executeQuery()

      val rows = mutableListOf<String>()
      while (rs.next()) {
        rows.add(rs.getString(1))
        rs.getInt(2)
      }

      verify(connection).prepareStatement(eq("SELECT * FROM t WHERE id = ?"))
      verify(statement).setInt(eq(1), eq(i))
      verify(statement).executeQuery()

      require(rows.size == 2)
      require(rows[0] == "row_$i")

      val list = spy(ArrayList<String>())
      list.add("a_$i")
      list.add("b_$i")
      list.add("c_$i")
      list.removeAt(1)

      verify(list).add(eq("a_$i"))
      verify(list).add(eq("b_$i"))
      verify(list).add(eq("c_$i"))
      verify(list).removeAt(eq(1))

      require(list.size == 2)
      require(list[0] == "a_$i")
      require(list[1] == "c_$i")

      val map = spy(HashMap<String, Int>())
      map["key_$i"] = i
      map["other_$i"] = i * 2
      val value = map["key_$i"]

      verify(map)[eq("key_$i")] = eq(i)
      verify(map)[eq("other_$i")] = eq(i * 2)

      require(value == i)
      require(map.size == 2)
    }
  }
}
