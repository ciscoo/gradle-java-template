plugins {
    `kotlin-dsl`
    id("com.diffplug.spotless") version "5.7.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val rootProperties = `java.util`.Properties().apply {
    layout.projectDirectory.file("../gradle.properties").asFile.inputStream().use { load(it) }
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.gradle:test-retry-gradle-plugin:${rootProperties["gradle-test-retry.version"]}")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:${rootProperties["spotless.version"]}")
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
