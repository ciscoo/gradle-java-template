import io.mateo.JavaToolchainExtension
import java.nio.charset.StandardCharsets

plugins {
    java
    id("io.mateo.build.java-toolchain-conventions")
    id("io.mateo.build.code-style-conventions")
}

group = "io.mateo"

repositories {
    mavenCentral()
}

configurations.configureEach {
    resolutionStrategy {
        cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
        cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
}

val commonCompilerArgs =
    listOf(
        // Enables all warnings
        "-Xlint:all",
        // Disable warnings related to annotation processing
        "-Xlint:-processing",
        // Disable warning about issues related to classfile contents
        "-Xlint:-classfile",
        // Disable warning about serializable classes that do not provide a serial version ID
        "-Xlint:-serial",
        // Terminates compilation when warnings occur
        "-Werror",
    )

val extension = extensions.getByType<JavaToolchainExtension>()

tasks {
    withType<Jar>().configureEach {
        metaInf.from(layout.settingsDirectory.file("LICENSE.txt"))
    }
    withType<JavaCompile>().configureEach {
        options.encoding = StandardCharsets.UTF_8.name()
    }
    compileJava {
        with(options) {
            compilerArgs.addAll(commonCompilerArgs)
            release = extension.releaseVersion.map { it.asInt() }
            if (!compilerArgs.contains("-parameters")) {
                compilerArgs.add("-parameters")
            }
        }
    }
    javadoc {
        options {
            memberLevel = JavadocMemberLevel.PROTECTED
            header = project.name
            source = extension.releaseVersion.get().toString()
            encoding = StandardCharsets.UTF_8.name()
            locale = `java.util`.Locale.ENGLISH.language
            this as StandardJavadocDocletOptions
            // https://docs.oracle.com/en/java/javase/17/docs/specs/man/javadoc.html
            addBooleanOption("Xdoclint:all", true)
            use()
            noTimestamp()
        }
    }
}

val libs = versionCatalogs.named("libs")

testing.suites.configureEach {
    if (this is JvmTestSuite) {
        libs.findVersion("junit").ifPresent {
            // Automatically adds org.junit.jupiter:junit-jupiter to implementation configuration
            useJUnitJupiter(it.requiredVersion)
        }
        dependencies {
            libs.findLibrary("assertj-core").ifPresent {
                implementation(it)
            }
            libs.findLibrary("junit-platformLauncher").ifPresent {
                runtimeOnly(it)
            }
        }
        tasks.named<JavaCompile>(sources.compileJavaTaskName) {
            options.release = extension.targetVersion.map { it.asInt() }
            options.compilerArgs.addAll(commonCompilerArgs + "-parameters")
        }
    }
}
