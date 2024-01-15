import org.gradle.api.internal.FeaturePreviews

pluginManagement {
    includeBuild("gradle/build-logic")
}

rootProject.name = "gradle-java-template"

include("documentation")

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
        maven {
            name = "springMilestone"
            url = uri("https://repo.spring.io/milestone")
        }
    }
}

FeaturePreviews.Feature.values().forEach { feature ->
    if (feature.isActive) {
        enableFeaturePreview(feature.name)
    }
}
