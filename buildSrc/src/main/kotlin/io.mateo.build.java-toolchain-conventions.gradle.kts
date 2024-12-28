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
