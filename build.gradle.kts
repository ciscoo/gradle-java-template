plugins {
    `spotless-conventions`
}

description = "Gradle Java Build Template"

defaultTasks("build")

val versions: Versions by extra(Versions(project))

allprojects {
    group = "io.mateo"

    configurations.configureEach {
        resolutionStrategy.cacheChangingModulesFor(60, TimeUnit.MINUTES)
    }
}
