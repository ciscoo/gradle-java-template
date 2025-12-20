plugins {
    id("io.mateo.build.release-conventions")
    id("io.mateo.build.code-style-conventions")
}

group = "io.mateo"
description = "Gradle Java Build Template"

defaultTasks("build")
