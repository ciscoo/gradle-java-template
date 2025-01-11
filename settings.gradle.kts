pluginManagement {
    includeBuild("gradle/plugins")
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("io.mateo.build.settings-conventions")
}

rootProject.name = "gradle-java-template"

include("documentation")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            name = "springMilestone"
            url = uri("https://repo.spring.io/milestone")
            content {
                includeGroupAndSubgroups("org.springframework")
                includeGroupAndSubgroups("io.projectreactor")
                includeGroupAndSubgroups("io.micrometer")
            }
        }
    }
}

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

startParameter.showStacktrace = ShowStacktrace.ALWAYS_FULL
startParameter.warningMode = WarningMode.All
