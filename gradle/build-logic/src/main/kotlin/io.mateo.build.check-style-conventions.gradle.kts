plugins {
    base
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
    configDirectory = rootProject.layout.projectDirectory.dir("gradle/config/checkstyle")
}

tasks {
    check {
        dependsOn(withType<Checkstyle>())
    }
}
