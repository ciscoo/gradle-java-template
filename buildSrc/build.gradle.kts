plugins {
    `java-gradle-plugin`
    id("com.diffplug.gradle.spotless") version "3.30.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_14
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
    implementation("org.gradle:test-retry-gradle-plugin:1.1.8")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:5.1.1")
    implementation("commons-io:commons-io:2.4")
    implementation("org.apache.commons:commons-lang3:3.10")
}

gradlePlugin {
    val internalConfigurationPlugin by plugins.creating {
        id = "io.mateo.internal-configuration"
        implementationClass = "io.mateo.gradle.build.InternalConfigurationPlugin"
    }
    val javaBaseConventions by plugins.creating {
        id = "io.mateo.java-base-conventions"
        implementationClass = "io.mateo.gradle.build.JavaBaseConventionsPlugin"
    }
    val javaLibraryConventions by plugins.creating {
        id = "io.mateo.java-library-conventions"
        implementationClass = "io.mateo.gradle.build.JavaLibraryConventionsPlugin"
    }
    val kotlinConventions by plugins.creating {
        id = "io.mateo.kotlin-conventions"
        implementationClass = "io.mateo.gradle.build.KotlinConventionsPlugin"
    }
    val publishedPlugin by plugins.creating {
        id = "io.mateo.published-artifact"
        implementationClass = "io.mateo.gradle.build.PublishedPlugin"
    }
    val publishingConventions by plugins.creating {
        id = "io.mateo.publishing-conventions"
        implementationClass = "io.mateo.gradle.build.PublishingConventionsPlugin"
    }
}

spotless {
    java {
        eclipse()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        ktlint()
        trimTrailingWhitespace()
        endWithNewline()
    }
}
