plugins {
    `kotlin-dsl`
    id("com.diffplug.spotless") version "5.5.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.gradle:test-retry-gradle-plugin:1.1.9")
    implementation("org.apache.commons:commons-lang3:3.11")
}

spotless {
    kotlinGradle {
        ktlint()
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlin {
        indentWithTabs()
        endWithNewline()
        trimTrailingWhitespace()
        targetExclude("build/")
    }
}
