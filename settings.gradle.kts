rootProject.name = "gradle-java-template"

include("dependencies")

// Enforce build file uses Kotlin DSL and name is the project name
rootProject.children.forEach {
    with(it) {
        buildFileName = "$name.gradle.kts"
        require(buildFile.isFile) { "$buildFile must exist" }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
