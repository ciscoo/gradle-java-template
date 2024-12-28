import io.mateo.build.JavaToolchainExtension

plugins {
    id("java") apply false
}

val extension = extensions.create("javaToolchainExtension", JavaToolchainExtension::class.java)
val defaultLanguageVersion: Property<JavaLanguageVersion> = extension.targetVersion
val javaLanguageVersion: Provider<JavaLanguageVersion> =
    providers
        .gradleProperty("javaLanguageVersion")
        .map { JavaLanguageVersion.of(it) }
        .orElse(defaultLanguageVersion)
val jvmImplementation: JvmImplementation =
    providers
        .gradleProperty("jvmImplementation")
        .map { it.uppercase(`java.util`.Locale.US) }
        .map {
            when (it) {
                "J9" -> JvmImplementation.J9
                else -> throw InvalidUserDataException("Unsupported JVM implementation: $it")
            }
        }.getOrElse(JvmImplementation.VENDOR_SPECIFIC)

java {
    toolchain {
        languageVersion = javaLanguageVersion
        implementation = jvmImplementation
    }
    manifest {
        attributes(
            "Build-Jdk-Spec" to extension.releaseVersion.get().toString(),
            "Created-By" to "Gradle ${gradle.gradleVersion}",
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version.toString(),
        )
    }
}

tasks {
    withType<JavaExec>().configureEach {
        javaLauncher = javaToolchains.launcherFor(java.toolchain)
    }
    withType<JavaCompile>().configureEach {
        outputs.cacheIf("Configured Java language version and default language version match") {
            javaLanguageVersion.get() == defaultLanguageVersion.get()
        }
    }
}
