plugins {
    id("java-library-conventions")
    id("javadoc-aggregation-conventions")
}

description = "Gradle Java Template Documentation"

dependencies {
    javadoc(projects.example)
    javadoc(projects.exampleApi)
}

tasks {
    jar {
        enabled = false
    }
    javadoc {
        enabled = false
    }
    javadocJar {
        enabled = false
        dependsOn(javadocAggregate)
    }
    sourcesJar {
        enabled = false
    }
}
