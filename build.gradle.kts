plugins {
    id("io.mateo.build.code-style-conventions")
}

description = "Gradle Java Build Template"

defaultTasks("build")

tasks.check {
    dependsOn(gradle.includedBuild("build-logic").task(":check"))
}
