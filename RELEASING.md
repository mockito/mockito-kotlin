To publish a release:

 - Tag the commit on master: `git tag -a x.x.x -m x.x.x && git push --tags`
 - Execute the release process: `./gradlew clean test uploadArchives -PisRelease=true`
 - Head to https://oss.sonatype.org/#stagingRepositories to close and release the deployment.
 - Don't forget to publish the tag on Github with release notes :)