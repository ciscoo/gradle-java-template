plugins {
    id("com.diffplug.spotless")
}

spotless {
    format("documentation") {
        target("**/*.md", "**/*.adoc")
        targetExclude("**/node_modules/**", "**/.gradle/**")
    }
}

pluginManager.withPlugin("java") {
    spotless {
        java {
            licenseHeaderFile(
                layout.projectDirectory.file("gradle/config/spotless/apache-license-2.0.java"),
                "(package|import|open|module)",
            )
            removeUnusedImports()
            endWithNewline()
            trimTrailingWhitespace()
            palantirJavaFormat()
        }
    }
}
