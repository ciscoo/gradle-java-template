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
    implementation("org.gradle:test-retry-gradle-plugin:1.1.9")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:${rootProperties["spotless.version"]}")
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
