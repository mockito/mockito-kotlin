package test

interface DataService {
  fun fetchString(key: String): String
  fun fetchInt(id: Int): Int
  fun process(input: String, count: Int): List<String>
  fun validate(data: Map<String, Any>): Boolean
  fun transform(items: List<Int>, factor: Double): List<Double>
}
