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

run {
    val publishedProjectPaths = sortedMapOf<String, String>()

    fun includeProject(projectPath: String, published: Boolean = false) {
        include(projectPath)
        project(":$projectPath").apply {
            buildFileName = "$name.gradle.kts"
            if (published) {
                publishedProjectPaths[name] = path
            }
        }
    }

    includeProject("documentation")
    includeProject("example", published = true)

    gradle.lifecycle.beforeProject {
        extra["publishedProjectPaths"] = publishedProjectPaths.values.toList()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("NO_IMPLICIT_LOOKUP_IN_PARENT_PROJECTS")
enableFeaturePreview("ENHANCED_GRAPH_ORDERING")
