# Mockito-Kotlin

[![Maven Central Version](https://img.shields.io/maven-central/v/org.mockito.kotlin/mockito-kotlin)](https://central.sonatype.com/artifact/org.mockito.kotlin/mockito-kotlin)

A small library that provides helper functions to work with [Mockito](https://github.com/mockito/mockito) in Kotlin.

## Install

Mockito-Kotlin is available on Maven Central.
For Gradle users, add the following to your build file, replacing `x.x.x` with the latest version:

`build.gradle`:
```groovy
testImplementation "org.mockito.kotlin:mockito-kotlin:x.x.x"
```

`build.gradle.kts`:
```kotlin
testImplementation("org.mockito.kotlin:mockito-kotlin:x.x.x")
```

## Example

A test using Mockito-Kotlin typically looks like the following:

```kotlin
@Test
fun doAction_doesSomething(){ 
  /* Given */
  val mock = mock<MyClass> {
    on { getText() } doReturn "text"
  }
  val classUnderTest = ClassUnderTest(mock)
  
  /* When */
  classUnderTest.doAction()
  
  /* Then */
  verify(mock).doSomething(any())
}
```

For more info and samples, see the [Wiki](https://github.com/mockito/mockito-kotlin/wiki).

## Android Support

See the [Android Support wiki](https://github.com/mockito/mockito-kotlin/wiki/Android-Support).

## Building

Mockito-Kotlin is built with Gradle.

 - `./gradlew build` builds and tests the project
 - `./gradlew publishToMavenLocal` installs the maven artifacts in your local repository
 - `./gradlew check` runs the test suite (See Testing below)

### Versioning

Mockito-Kotlin roughly follows SEMVER

### Testing

Mockito-Kotlin's test suite is located in a separate `tests` module,
to allow running the tests using several Kotlin versions whilst still
keeping the base module at a recent version.

 - `./gradlew check` runs the checks including tests.

Usually it is enough to test only using the default Kotlin versions;
CI will test against multiple versions.
If you want to test using a different Kotlin version locally,
add the `-PtestKotlinVersion=1.2.3` argument to the Gradle invocation while running the tests.

### Acknowledgements

`mockito-kotlin` was created and developed by [nhaarman@](https://github.com/nhaarman) after which the repository was integrated into the official Mockito GitHub organization.
We would like to thank Niek for the original idea and extensive work plus support that went into `mockito-kotlin`.
