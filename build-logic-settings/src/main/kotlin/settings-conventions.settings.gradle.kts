import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.sequences.forEach

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootDir
    .toPath()
    .listDirectoryEntries()
    .asSequence()
    .filter { it.isDirectory() }
    .map { it.toFile() }
    .map { it to it.resolve("${it.name}.gradle.kts") }
    .filter { it.second.exists() }
    .forEach { (dir, buildScript) ->
        include(dir.name)
        project(dir).buildFileName = buildScript.name
    }
