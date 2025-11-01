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
        licenseHeaderFile(
            layout.projectDirectory.file("../gradle/config/spotless/apache-license-2.0.java"),
            "(package|import|open|module) ",
        )
        removeUnusedImports()
        endWithNewline()
        trimTrailingWhitespace()
        palantirJavaFormat(libs.versions.palantirJavaFormat.get())
    }
    kotlin {
        licenseHeaderFile(
            layout.projectDirectory.file("../gradle/config/spotless/apache-license-2.0.java"),
            "(package|import|open|module) ",
        )
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
