pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("build-logic-settings")
}

includeBuild("build-logic")

plugins {
    id("settings-conventions")
    id("central-publishing-conventions")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "gradle-java-template"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("NO_IMPLICIT_LOOKUP_IN_PARENT_PROJECTS")
enableFeaturePreview("ENHANCED_GRAPH_ORDERING")
