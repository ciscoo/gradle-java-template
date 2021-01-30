plugins {
    id("com.diffplug.spotless")
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

pluginManager.withPlugin("java") {
    spotless {
        java {
            licenseHeaderFile(rootProject.file("src/spotless/apache-license-2.0.java"), "(package|import|open|module)")
            importOrderFile(rootProject.file("src/eclipse/eclipse.importorder"))
            eclipse().configFile(rootProject.file("src/eclipse/eclipse-formatter-settings.xml"))
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}
