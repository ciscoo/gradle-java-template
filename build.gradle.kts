plugins {
    id("com.diffplug.spotless")
}

description = "Gradle Java Build Template"

defaultTasks("build")

val versions: Versions by extra(Versions(project))

allprojects {
    group = "io.mateo"

    apply {
        plugin("com.diffplug.spotless")
    }

    repositories {
        mavenCentral()
    }

    configurations.configureEach {
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
