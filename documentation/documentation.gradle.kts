plugins {
    id("io.mateo.build.java-conventions")
    alias(libs.plugins.node)
    alias(libs.plugins.spotless)
}

description = "Gradle Java Template Documentation"

node {
    if (providers.environmentVariable("CI").isPresent) {
        npmInstallCommand = "ci --silent --no-progress"
    }
}

tasks {
    jar {
        enabled = false
    }
    javadoc {
        enabled = false
    }
}
