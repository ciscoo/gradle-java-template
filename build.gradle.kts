import com.diffplug.gradle.spotless.SpotlessPlugin
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("nebula.release")
    id("com.diffplug.gradle.spotless")
}

description = "Gradle Java Build Template"

defaultTasks("build")

val now: OffsetDateTime by extra(OffsetDateTime.now())
val buildDate: String by extra(DateTimeFormatter.ISO_LOCAL_DATE.format(now))
val buildTime: String by extra(DateTimeFormatter.ofPattern("HH:mm:ss.SSSZ").format(now))

allprojects {
    group = "io.mateo"

    apply {
        plugin<SpotlessPlugin>()
    }

    repositories {
        mavenCentral()
    }

    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(60, TimeUnit.MINUTES)
    }
}

subprojects {
    spotless {
        java {
            licenseHeaderFile(rootProject.file("src/spotless/apache-license-2.0.java"), "(package|import|open|module)")
            importOrderFile(rootProject.file("src/eclipse/eclipse.importorder"))
            eclipse().configFile(rootProject.file("src/eclipse/eclipse-formatter-settings.xml"))
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
        }
        kotlinGradle {
            ktlint()
            endWithNewline()
            trimTrailingWhitespace()
        }
    }
    if (pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")) {
        spotless {
            kotlin {
                ktfmt()
                endWithNewline()
                trimTrailingWhitespace()
            }
        }
    }
}

spotless {
    kotlinGradle {
        ktlint()
        endWithNewline()
        trimTrailingWhitespace()
    }
    format("documentation") {
        target("**/*.adoc", "**/*.md")
        trimTrailingWhitespace()
        endWithNewline()
    }
}
