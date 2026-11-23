pluginManager.withPlugin("java") {
    val defaultLanguageVersion = JavaLanguageVersion.of(JavaToolchainExtension.DEFAULT_TARGET_VERSION)
    val extension = extensions.create<JavaToolchainExtension>("javaToolchainExtension")
    val java = the<JavaPluginExtension>()
    val javaToolchainService = the<JavaToolchainService>()

    java.toolchain {
        languageVersion = extension.targetVersion
        implementation = extension.implementation
    }

    tasks {
        withType<JavaExec>().configureEach {
            val javaToolchains = the<JavaToolchainService>()
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

    // https://github.com/gradle/gradle/issues/39837
    pluginManager.withPlugin("checkstyle") {
        tasks.withType<Checkstyle>().configureEach {
            javaLauncher =
                javaToolchainService.launcherFor {
                    languageVersion = defaultLanguageVersion
                }
        }
    }
}
