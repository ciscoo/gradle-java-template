pluginManagement {
    plugins {
        id("com.diffplug.spotless") version "5.7.0"
    }
}

rootProject.name = "gradle-java-template"

// Enforce build file uses Kotlin DSL and name is the project name
rootProject.children.forEach {
    with(it) {
        buildFileName = "$name.gradle.kts"
        require(buildFile.isFile) { "$buildFile must exist" }
    }
}
