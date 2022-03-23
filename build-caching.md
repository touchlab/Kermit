## Gradle Build Cache

Configuration for Gradle's build caching feature has been added to the project but it won't be used unless the 
`--build-cache` command line flag is applied to a given build, or it's turned on in `gradle.properties`.  

This is intended for *internal development only*.  

*Note:* The `ci-test-samples.sh` script supplies this argument to all the sample project builds as it loops through and runs them.