plugins {
    id("io.mateo.build.java-conventions")
    alias(libs.plugins.node)
    alias(libs.plugins.spotless)
}

description = "Gradle Java Template Documentation"

node {
    workDir = layout.buildDirectory.dir("node/installation")
    npmWorkDir = layout.buildDirectory.dir("node/npm")
    nodeProjectDir = layout.buildDirectory.dir("node/project")
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
