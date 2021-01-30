import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.Locale

plugins {
    id("spotless-conventions")
    id("internal-configuration")
    id("org.gradle.test-retry") apply false
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(Versions.jvmTarget.majorVersion.toInt()))
    }
    tasks {
        withType<JavaCompile>().configureEach {
            with(options) {
                encoding = StandardCharsets.UTF_8.toString()
                release.set(Versions.jvmReleaseTarget.majorVersion.toInt())
                if (!compilerArgs.contains("-parameters")) {
                    compilerArgs.add("-parameters")
                }
            }
        }
        // https://docs.oracle.com/en/java/javase/15/docs/specs/man/javadoc.html
        withType<Javadoc>().configureEach {
            options {
                memberLevel = JavadocMemberLevel.PROTECTED
                header = project.name
                encoding = StandardCharsets.UTF_8.toString()
                locale = Locale.ENGLISH.language
                this as StandardJavadocDocletOptions
                addBooleanOption("Xdoclint:html,syntax", true)
                addBooleanOption("html5", true)
                use()
                noTimestamp()
            }
        }
        withType<Test>().configureEach {
            useJUnitPlatform()
            testLogging {
                events = setOf(
                    TestLogEvent.STANDARD_ERROR,
                    TestLogEvent.FAILED,
                    TestLogEvent.SKIPPED
                )
            }
        }
        if (`java.lang`.Boolean.parseBoolean(System.getenv("CI"))) {
            project.pluginManager.apply("org.gradle.test-retry")
            withType<Test>().configureEach {
                retry {
                    failOnPassedAfterRetry.set(true)
                    maxRetries.set(3)
                }
            }
        }
        val copyLegalFiles by tasks.registering(Copy::class) {
            from(Path.of(rootProject.file("buildSrc/src/main/resources").toURI()))
            include("NOTICE.txt", "LICENSE.txt")
            into(project.layout.buildDirectory.dir("legal"))
        }
        withType<Jar>().configureEach {
            val jarTask = this
            metaInf.from(copyLegalFiles)
            manifest {
                attributes["Automatic-Module-Name"] = project.name.replace("-", ".")
                attributes["Created-By"] = "${System.getProperty("java.version")} (${System.getProperty("java.vendor")} ${System.getProperty("java.vm.version")})"
                attributes["Implementation-Title"] = when {
                    sourceSets.map { it.sourcesJarTaskName }.toSortedSet().contains(jarTask.name) -> {
                        "Sources for ${project.name}"
                    }
                    sourceSets.map { it.javadocJarTaskName }.toSortedSet().contains(jarTask.name) -> {
                        "Javadoc for ${project.name}"
                    }
                    else -> {
                        requireNotNull(project.description) { "Project description is not defined for '${project.name}'" }
                    }
                }
                attributes["Implementation-Version"] = project.version.toString()
            }
        }
    }
}
