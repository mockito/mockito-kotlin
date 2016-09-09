# Mockito-Kotlin
[ ![Download](https://api.bintray.com/packages/nhaarman/maven/Mockito-Kotlin/images/download.svg) ](https://bintray.com/nhaarman/maven/Mockito-Kotlin/_latestVersion)

A small library that provides helper functions to work with [Mockito](https://github.com/mockito/mockito) in Kotlin.

## Install

Mockito-Kotlin is available on JCenter.
For Gradle users, add the following to your `build.gradle`:

```groovy
repositories {
    jcenter()
}
dependencies {
    testCompile "com.nhaarman:mockito-kotlin:x.x.x"
}
```

## Example

A test using Mockito-Kotlin typically looks like the following:

```kotlin
@Test
fun a(){ 
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
