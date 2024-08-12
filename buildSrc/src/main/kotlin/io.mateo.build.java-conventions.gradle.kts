import io.mateo.build.JavaToolchainExtension
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

val extension = extensions.getByType(JavaToolchainExtension::class.java)

tasks {
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
    withType<Javadoc>().configureEach {
        with(options as StandardJavadocDocletOptions) {
            memberLevel = JavadocMemberLevel.PROTECTED
            header = project.name
            source = extension.releaseVersion.get().toString()
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
            libs.findLibrary("assertjCore").ifPresent {
                implementation(it)
            }
            libs.findLibrary("junitPlatformLauncher").ifPresent {
                runtimeOnly(it)
            }
        }
        tasks.named(sources.compileJavaTaskName, JavaCompile::class) {
            options.release = extension.targetVersion.map { it.asInt() }
            options.compilerArgs.addAll(commonCompilerArgs + "-parameters")
        }
    }
}
