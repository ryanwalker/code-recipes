```bash

test {
    useJUnitPlatform()

    jacoco {
        excludes = jacocoExclude
    }

    finalizedBy service: jacocoAggregatedReport

    environment "DATASTORE_EMULATOR_HOST", "http://localhost:18081"
    environment "GOOGLE_APPLICATION_CREDENTIALS", new File(project.rootDir, 'credential.json').path
    environment "PUBSUB_EMULATOR_HOST", "localhost:18681"
    environment "PUBSUB_PROJECT_ID", "is-subscription-mgmt-dev"

    boolean withIntegration = project.hasProperty('withIntegration')

    if (withIntegration) {
        println 'With integration tests'
        environment "You", "knowit"
    } else {
        println 'exclude integration tests'
        environment "You", "dont"
        filter {
            excludeTestsMatching "com.kubra.prepay.integration.*"
        }
    }
}
```