plugins {
    `kotlin-dsl`
    alias(libs.plugins.build.spotless)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

spotless {
    java {
        licenseHeaderFile(layout.projectDirectory.file("../gradle/config/spotless/apache-license-2.0.java"), "(package|import|open|module) ")
        removeUnusedImports()
        endWithNewline()
        trimTrailingWhitespace()
        palantirJavaFormat()
    }
    kotlin {
        endWithNewline()
        trimTrailingWhitespace()
        targetExclude("**/build/**")
        ktlint(libs.versions.ktlint.get())
    }
}

dependencies {
    implementation(libs.gradle.spotless)
}
