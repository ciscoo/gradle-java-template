import io.mateo.build.Versions

plugins {
    id("io.mateo.build.code-style-conventions")
}

description = "Gradle Java Build Template"

defaultTasks("build")

val versions: Versions by extra(Versions(project))
