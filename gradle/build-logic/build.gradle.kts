// https://youtrack.jetbrains.com/issue/KT-63165
@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

import org.jetbrains.kotlin.gradle.plugin.diagnostics.CheckKotlinGradlePluginConfigurationErrors
import org.jetbrains.kotlin.gradle.plugin.diagnostics.kotlinToolingDiagnosticsCollectorProvider

plugins {
    `kotlin-dsl`
    alias(libs.plugins.spotless)
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
        licenseHeaderFile(layout.projectDirectory.file("../config/spotless/apache-license-2.0.java"))
        removeUnusedImports()
        endWithNewline()
        trimTrailingWhitespace()
        palantirJavaFormat()
    }
}

dependencies {
    implementation(libs.gradle.spotless)
}

tasks.withType<CheckKotlinGradlePluginConfigurationErrors>().configureEach {
    usesService(project.kotlinToolingDiagnosticsCollectorProvider)
}
