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
