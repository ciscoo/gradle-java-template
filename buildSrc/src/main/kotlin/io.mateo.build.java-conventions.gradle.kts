/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.nio.charset.StandardCharsets

plugins {
    java
    id("io.mateo.build.java-toolchain-conventions")
    id("io.mateo.build.code-style-conventions")
}

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
        manifest {
            attributes(
                "Build-Jdk-Spec" to extension.releaseVersion.get().toString(),
                "Created-By" to "Gradle ${gradle.gradleVersion}",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version.toString(),
            )
        }
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

testing.suites.withType<JvmTestSuite>().configureEach {
    useJUnitJupiter(libs.findVersion("junit").orElseThrow().requiredVersion)
    dependencies {
        implementation(libs.findLibrary("assertj-core").orElseThrow())
        runtimeOnly(libs.findLibrary("junit-platformLauncher").orElseThrow())
    }
    tasks.named<JavaCompile>(sources.compileJavaTaskName) {
        options.release = extension.targetVersion.map { it.asInt() }
        options.compilerArgs.addAll(commonCompilerArgs + "-parameters")
    }
}
