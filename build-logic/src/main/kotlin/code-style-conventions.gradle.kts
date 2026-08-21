plugins {
    id("com.diffplug.spotless")
}

val libs = versionCatalogs.named("libs")

spotless {
    kotlinGradle {
        endWithNewline()
        trimTrailingWhitespace()
        ktlint(
            libs
                .findLibrary("ktlint")
                .orElseThrow()
                .get()
                .version,
        )
    }
    pluginManager.withPlugin("java") {
        val licenseHeaderFile = isolated.rootProject.projectDirectory.file("build-logic/config/spotless/apache-license-2.0.java")
        java {
            targetExclude("**/module-info.java", "**/package-info.java")
            licenseHeaderFile(licenseHeaderFile, "(package|import) ")
            removeUnusedImports()
            endWithNewline()
            trimTrailingWhitespace()
            leadingTabsToSpaces()
            palantirJavaFormat(
                libs
                    .findLibrary("palantirJavaFormat")
                    .orElseThrow()
                    .get()
                    .version,
            )
        }
        format("javaMisc") {
            target(
                fileTree(isolated.projectDirectory.dir("src/main/java")) {
                    include("module-info.java", "**/package-info.java")
                },
            )
            licenseHeaderFile(licenseHeaderFile, "((/(//|\\*\\*))|((open )?module )|package|@.+)")
            trimTrailingWhitespace()
            endWithNewline()
            leadingTabsToSpaces()
        }
    }
}
