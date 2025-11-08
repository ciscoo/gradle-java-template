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
}
