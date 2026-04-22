package test

object BenchmarkHarness {

  fun run(name: String, iteration: (Int) -> Unit) {
    val warmupIterations = System.getProperty("benchmark.warmup", "100").toInt()
    val measuredIterations = System.getProperty("benchmark.iterations", "1000").toInt()

    val coldStartTime = System.nanoTime()
    iteration(0)
    val coldStartMs = (System.nanoTime() - coldStartTime) / 1_000_000.0

    for (i in 1..warmupIterations) {
      iteration(i)
    }

    val warmStartTime = System.nanoTime()
    for (i in 0 until measuredIterations) {
      iteration(warmupIterations + 1 + i)
    }
    val warmTotalMs = (System.nanoTime() - warmStartTime) / 1_000_000.0

    val header = "========== $name =========="
    println(header)
    println("Cold start (1st iteration): %.2f ms".format(coldStartMs))
    println("Warm iterations: $measuredIterations")
    println("Warm total time: %.2f ms".format(warmTotalMs))
    println("Warm avg per iteration: %.4f ms".format(warmTotalMs / measuredIterations))
    println("=".repeat(header.length))
  }
}
