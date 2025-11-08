plugins {
    id("java") apply false
}

val defaultLanguageVersion = JavaLanguageVersion.of(JavaToolchainExtension.DEFAULT_TARGET_VERSION)
val extension = extensions.create<JavaToolchainExtension>("javaToolchainExtension")

java {
    toolchain {
        languageVersion = extension.targetVersion
        implementation = extension.implementation
    }
}

tasks {
    withType<JavaExec>().configureEach {
        javaLauncher = javaToolchains.launcherFor(java.toolchain)
        if (extension.targetVersion.get() != defaultLanguageVersion) {
            inputs.property("javaRuntimeVersion", javaLauncher.get().metadata.javaRuntimeVersion)
        }
    }
    withType<JavaCompile>().configureEach {
        outputs.cacheIf("Configured Java language version and default language version match") {
            extension.targetVersion.get() == defaultLanguageVersion
        }
    }
    withType<Test>().configureEach {
        if (extension.targetVersion.get() != defaultLanguageVersion) {
            inputs.property("javaRuntimeVersion", javaLauncher.get().metadata.javaRuntimeVersion)
        }
    }
}
