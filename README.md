# Mockito-Kotlin
[ ![Download](https://maven-badges.herokuapp.com/maven-central/com.nhaarman.mockitokotlin2/mockito-kotlin/badge.svg) ](https://maven-badges.herokuapp.com/maven-central/com.nhaarman.mockitokotlin2/mockito-kotlin)

A small library that provides helper functions to work with [Mockito](https://github.com/mockito/mockito) in Kotlin.

## Install

Mockito-Kotlin is available on Maven Central and JCenter.
For Gradle users, add the following to your `build.gradle`, replacing `x.x.x` with the latest version:

```groovy
testImplementation "com.nhaarman.mockitokotlin2:mockito-kotlin:x.x.x"
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

For more info and samples, see the [Wiki](https://github.com/nhaarman/mockito-kotlin/wiki).

## Building

Mockito-Kotlin is built with Gradle.

 - `./gradlew build` builds the project
 - `./gradlew publishToMavenLocal` installs the maven artifacts in your local repository
 - `./gradlew assemble && ./gradlew test` runs the test suite (See Testing below)

### Versioning

Mockito-Kotlin roughly follows SEMVER; version names are parsed from 
git tags using `git describe`.

### Testing

Mockito-Kotlin's test suite is located in a separate `tests` module,
to allow running the tests using several Kotlin versions whilst still
keeping the base module at a recent version.  

Testing thus must be done in two stages: one to build the base artifact
to test against, and the actual execution of the tests against the 
built artifact:

 - `./gradlew assemble` builds the base artifact
 - `./gradlew test` runs the tests against the built artifact.

Usually it is enough to test only using the default Kotlin versions; 
CI will test against multiple versions.
If you want to test using a different Kotlin version locally, set
an environment variable `KOTLIN_VERSION` and run the tests.
