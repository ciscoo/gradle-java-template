plugins {
    base
    checkstyle
}

versionCatalogs.named("libs").findLibrary("checkstyle").ifPresent {
    checkstyle {
        toolVersion = it.get().version as String
    }
}

checkstyle {
    configDirectory = rootProject.layout.projectDirectory.dir("gradle/config/checkstyle")
}

tasks {
    check {
        dependsOn(withType<Checkstyle>())
    }
}
