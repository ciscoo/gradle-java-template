dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../libs.versions.toml"))
        }
    }
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "plugins"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("settings")

startParameter.showStacktrace = ShowStacktrace.ALWAYS_FULL
startParameter.warningMode = WarningMode.All
