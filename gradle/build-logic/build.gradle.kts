plugins {
    `kotlin-dsl`
    alias(libs.plugins.build.spotless)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

spotless {
    java {
        removeUnusedImports()
        endWithNewline()
        trimTrailingWhitespace()
        palantirJavaFormat(libs.versions.palantirJavaFormat.get())
    }
    kotlin {
        endWithNewline()
        trimTrailingWhitespace()
        target("**/*.kt")
        targetExclude("**/build/**")
        ktlint(libs.versions.ktlint.get())
    }
    kotlinGradle {
        endWithNewline()
        trimTrailingWhitespace()
        targetExclude("**/build/**")
        target("**/*.gradle.kts")
        ktlint(libs.versions.ktlint.get())
    }
}

dependencies {
    implementation(libs.gradle.spotless)
    implementation(libs.gradle.jreleaser)
}

// TODO: Maybe remove when Gradle 9.3 is released
// https://github.com/gradle/gradle/issues/35204
// https://youtrack.jetbrains.com/issue/KT-80096/Strange-Inconsistent-JVM-Target-Compatibility-warning
kotlin.jvmToolchain(24)
