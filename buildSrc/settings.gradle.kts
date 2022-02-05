pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy.eachPlugin {
        if (requested.id.id == "io.spring.javaformat") {
            useModule("io.spring.javaformat:spring-javaformat-gradle-plugin:0.0.31") // Keep version in sync with version catalog.
        }
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
