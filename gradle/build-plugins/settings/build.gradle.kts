plugins {
    `kotlin-dsl`
    alias(libs.plugins.build.spotless)
}

dependencies {
    implementation(libs.gradle.foojarResolverConvention)
}

spotless {
    kotlin {
        endWithNewline()
        trimTrailingWhitespace()
        targetExclude("**/build/**")
        ktlint(libs.versions.ktlint.get())
    }
}

// TODO: Maybe remove when Gradle 9.3 is released
// https://github.com/gradle/gradle/issues/35204
// https://youtrack.jetbrains.com/issue/KT-80096/Strange-Inconsistent-JVM-Target-Compatibility-warning
kotlin.jvmToolchain(24)
