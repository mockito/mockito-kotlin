package test

object ServiceRegistry {
  fun lookup(name: String): String = "real_$name"
  fun isRegistered(name: String): Boolean = false
  fun count(): Int = 0
}
