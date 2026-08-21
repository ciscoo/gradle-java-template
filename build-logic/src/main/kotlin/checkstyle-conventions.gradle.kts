plugins {
    checkstyle
}

checkstyle {
    toolVersion =
        versionCatalogs
            .named("libs")
            .findLibrary("checkstyle")
            .orElseThrow()
            .get()
            .version as String
}

checkstyle {
    configDirectory = isolated.rootProject.projectDirectory.dir("build-logic/config/checkstyle")
}

pluginManager.withPlugin("lifecycle-base") {
    tasks {
        named(LifecycleBasePlugin.CHECK_TASK_NAME) {
            dependsOn(withType<Checkstyle>())
        }
    }
}
