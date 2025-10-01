# Releasing

1. Every change on the main development branch is released as -SNAPSHOT version
to Sonatype snapshot repo at https://s01.oss.sonatype.org/content/repositories/snapshots/org/mockito/kotlin/mockito-kotlin.
2. In order to release a non-snapshot version to Maven Central push an annotated tag, for example:
```
git tag -a -m "Release 3.4.5" 3.4.5
git push origin 3.4.5
```
You can do so using the GitHub UI as well.
