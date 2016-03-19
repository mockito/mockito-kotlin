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

## Examples

### Creating mock instances

Due to Kotlin's [reified type parameters](https://kotlinlang.org/docs/reference/inline-functions.html#reified-type-parameters), if the type can be inferred, you don't have to specify it explicitly:

**Java**:
```java
MyClass c = mock(Myclass.class);
c.doSomething(mock(MyOtherClass.class));
```

**Kotlin**:
```kotlin
val c : MyClass = mock()
c.doSomething(mock())
```

If the type can't be inferred, you can pass it like so:

```kotlin
val d = mock<MyClass>()
```


### Expecting any value

Mockito's `any(Class<T>)` often returns `null` for non-primitive classes.
In Kotlin, this can be a problem due to its [null-safety](https://kotlinlang.org/docs/reference/null-safety.html) feature.
This library creates non-null instances when necessary.
Again, if the type can be inferred, you don't have to specify it explicitely:

**Java**:
```java
verify(myClass).doSomething(any(String.class));
```

**Kotlin**:
```kotlin
verify(myClass).doSomething(any()); // Non-nullable parameter type is inferred
```

For generic arrays, use the `anyArray()` method:

```kotlin
verify(myClass).setItems(anyArray())
```

## Custom instance creators

There are some cases where Mockito-Kotlin cannot create an instance of a class. 
This can for instance be when a constructor has some specific preconditions
for its parameters.
You can _register_ `instance creators` to overcome this:

```kotlin
MockitoKotlin.registerInstanceCreator<MyClass> { MyClass(5) }
```

Whenever MockitoKotlin needs to create an instance of `MyClass`, this function is called,
giving you ultimate control over how these instances are created.

### Argument Matchers

Using higher-order functions, you can write very clear expectations about expected values.
For example:

**Kotlin**:
```kotlin
verify(myClass).setItems(argThat{ size == 2 })
```

### Convenience functions

Most of Mockito's static functions are available as top-level functions.
That means, IDE's like IntelliJ can easily import and autocomplete them, saving you the hassle of manually importing them.


