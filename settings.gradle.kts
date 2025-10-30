pluginManagement {
    includeBuild("gradle/build-plugins")
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("io.mateo.build.settings-conventions")
}

includeBuild("gradle/build-logic")

rootProject.name = "gradle-java-template"

include("documentation")
include("example-api")

fun ProjectDescriptor.ensureBuildFileExists() {
    buildFileName = "$name.gradle.kts"
    require(buildFile.isFile) { "$buildFile must exist" }
}

fun requireBuildFileName(projectDescriptor: ProjectDescriptor) {
    projectDescriptor.ensureBuildFileExists()
    projectDescriptor.children.forEach {
        requireBuildFileName(it)
    }
}

rootProject.children.forEach {
    it.ensureBuildFileExists()
    requireBuildFileName(it)
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

startParameter.warningMode = WarningMode.All
