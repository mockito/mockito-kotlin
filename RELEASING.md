# Releasing

1. Every change on the main development branch is released as -SNAPSHOT version
to Sonatype snapshot repo at https://s01.oss.sonatype.org/content/repositories/snapshots/org/mockito/kotlin/mockito-kotlin.
2. In order to release a non-snapshot version to Maven Central push an annotated tag, for example:
```
git tag -a -m "Release 3.4.5" 3.4.5
git push origin 3.4.5
```
3. At the moment, you **may not create releases from GitHub Web UI**.
Doing so will make the CI build fail because the CI creates the changelog and posts to GitHub releases.
In the future supporting this would be nice but currently please make releases by pushing from CLI.
