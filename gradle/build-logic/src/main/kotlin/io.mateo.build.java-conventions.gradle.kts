import io.mateo.build.JavaToolchainExtension
import java.nio.charset.StandardCharsets

plugins {
    java
    id("io.mateo.build.code-style-conventions")
}

configurations.configureEach {
    resolutionStrategy {
        cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
        cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
}

val javaToolchain = extensions.create("javaToolchain", JavaToolchainExtension::class)

java {
    toolchain {
        languageVersion = javaToolchain.targetVersion
    }
}

val commonCompilerArgs = listOf(
        "-Xlint:all", // Enables all warnings
        "-Xlint:-processing", // Disable warnings related to annotation processing
        "-Xlint:-classfile", // Disable warning about issues related to classfile contents
        "-Xlint:-serial", // Disable warning about serializable classes that do not provide a serial version ID
        "-Werror", // Terminates compilation when warnings occur
)

tasks {
    withType(JavaExec::class).configureEach {
        javaLauncher = javaToolchains.launcherFor(java.toolchain)
    }
    withType(JavaCompile::class).configureEach {
        options.encoding = StandardCharsets.UTF_8.name()
    }
    named { it == sourceSets.main.get().compileJavaTaskName }.configureEach {
        this as JavaCompile
        with(options) {
            compilerArgs.addAll(commonCompilerArgs)
            release = javaToolchain.releaseVersion.map { it.asInt() }
            if (!compilerArgs.contains("-parameters")) {
                compilerArgs.add("-parameters")
            }
        }
    }
    withType(Javadoc::class).configureEach {
        with(options as StandardJavadocDocletOptions) {
            memberLevel = JavadocMemberLevel.PROTECTED
            header = project.name
            source = javaToolchain.releaseVersion.get().toString()
            encoding = StandardCharsets.UTF_8.name()
            // https://docs.oracle.com/en/java/javase/21/docs/specs/man/javadoc.html
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
            libs.findLibrary("junitPlatformLauncher").ifPresent {
                runtimeOnly(it)
            }
        }
        tasks.named(sources.compileJavaTaskName, JavaCompile::class) {
            options.compilerArgs.addAll(commonCompilerArgs + "-parameters")
        }
    }
}
