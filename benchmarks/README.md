# Benchmarks

Lightweight, comparative benchmarks between mockito-kotlin and MockK, measuring the cost of
common mocking use cases.

**Disclaimers:**
- These are simple wall clock benchmarks and they don't account for things like GC pauses. Results come from a single run with no statistical analysis.
- Performance differences are probably negligible for most users and shouldn't be the only consideration when choosing a mocking framework. The difference might be meaningful at scale, for example if your tests involve complicated mocking and you run them often.
- The included benchmarks are just examples and may not be representative of real tests.

If you are an expert in JVM benchmarking/profiling, please contribute and improve the methodology!

## Benchmark cases

- **basicMocking** - Create a mock of an interface with 5 methods, stub all
  methods, invoke them, verify all interactions, and assert return values.
- **heavyMocking** - Create 3 chained interface mocks, stub and invoke a multi-row query, then
  create 2 spies of concrete classes and invoke/verify mutating operations.
- **objectMocking** - Mock a Kotlin `object` singleton, stub 3 methods, invoke
  and verify them, then tear down the mock. Uses `mockObject`/`.use{}` for
  Mockito and `mockkObject`/`unmockkObject` for MockK.

Each benchmark measures:
- **Cold start** - The first iteration, which includes class loading and JIT
  compilation.
- **Warm average** - Average time per iteration after 100 warmup iterations
  (1000 measured iterations by default).

## Running

```
./gradlew :benchmarks:test --rerun
```

To customize iteration counts:

```
./gradlew :benchmarks:test --rerun \
  -Dbenchmark.iterations=5000 \
  -Dbenchmark.warmup=500
```

If unset, the defaults are 1000 measured iterations and 100 warmup iterations.

## Example results

Collected on 2026-04-22 on a MacBook Pro with the following setup:

| | |
|---|---|
| CPU | Apple M4 Max |
| OS | macOS 26.4.1 |
| JDK | OpenJDK Temurin 17.0.8+7 (aarch64) |
| Kotlin | 2.1.20 |
| Mockito-Kotlin | 6.3.0 |
| Mockito | 5.23.0 (via mockito-kotlin) |
| MockK | 1.14.9 |

### basicMocking

| | Mockito | MockK |
|---|---|---|
| Cold start | 500 ms | 572 ms |
| Warm avg/iteration | 0.139 ms | 0.305 ms |

### heavyMocking

| | Mockito | MockK |
|---|---|---|
| Cold start | 764 ms | 1240 ms |
| Warm avg/iteration | 0.389 ms | 1.064 ms |

### objectMocking

| | Mockito | MockK |
|---|---|---|
| Cold start | 457 ms | 476 ms |
| Warm avg/iteration | 0.154 ms | 35.863 ms |

