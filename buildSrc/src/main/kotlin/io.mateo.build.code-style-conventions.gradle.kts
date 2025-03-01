plugins {
    id("com.diffplug.spotless")
}

spotless {
    format("documentation") {
        target("**/*.md", "**/*.adoc")
        targetExclude("**/.gradle/**")
    }
    kotlinGradle {
        endWithNewline()
        trimTrailingWhitespace()
        libs.findVersion("kitlint").ifPresent {
            ktlint(it.requiredVersion)
        }
    }
}

val libs = versionCatalogs.named("libs")

pluginManager.withPlugin("java") {
    spotless {
        java {
            licenseHeaderFile(
                rootProject.layout.projectDirectory.file("gradle/config/spotless/apache-license-2.0.java"),
                "(package|import|open|module)",
            )
            removeUnusedImports()
            endWithNewline()
            trimTrailingWhitespace()
            libs.findVersion("plantirJavaFormat").ifPresent {
                palantirJavaFormat(it.requiredVersion)
            }
        }
    }
}

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    spotless {
        kotlin {
            endWithNewline()
            trimTrailingWhitespace()
            libs.findVersion("kitlint").ifPresent {
                ktlint(it.requiredVersion)
            }
        }
    }
}
