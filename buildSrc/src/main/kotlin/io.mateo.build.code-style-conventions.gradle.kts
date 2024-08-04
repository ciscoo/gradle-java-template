plugins {
    id("com.diffplug.spotless")
}

spotless {
    format("documentation") {
        target("**/*.md", "**/*.adoc")
        targetExclude("**/node_modules/**", "**/.gradle/**")
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
            libs.findLibrary("gradle.plantirJavaFormat").ifPresent {
                palantirJavaFormat(it.get().version)
            }
        }
    }
}
