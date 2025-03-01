plugins {
    `kotlin-dsl`
    alias(libs.plugins.build.spotless)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

spotless {
    java {
        licenseHeaderFile(
            layout.projectDirectory.file("../gradle/config/spotless/apache-license-2.0.java"),
            "(package|import|open|module) ",
        )
        removeUnusedImports()
        endWithNewline()
        trimTrailingWhitespace()
        palantirJavaFormat(libs.versions.palantirJavaFormat.get())
    }
}

dependencies {
    implementation(libs.gradle.spotless)
}
