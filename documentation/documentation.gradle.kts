import com.github.gradle.node.npm.task.NpmTask

plugins {
    base
    alias(libs.plugins.spotless)
    alias(libs.plugins.node)
}

node {
    version = provider { layout.projectDirectory.file(".nvmrc").asFile.readText().drop(1).trim() }
    download = true
}

tasks {
    clean {
        if (providers.gradleProperty("cleanNode").isPresent) {
            delete(layout.buildDirectory.dir("node_modules"))
            delete(nodeSetup)
            delete(npmSetup)
        }
    }
    val prettierCheck by registering(NpmTask::class) {
        args = listOf("run", "prettier:check")
    }
    val prettierWrite by registering(NpmTask::class) {
        args = listOf("run", "prettier:write")
    }
    withType(NpmTask::class.java).configureEach {
        if (name == npmInstall.name) {
            return@configureEach
        }
        inputs.files(npmInstall)
        inputs.files(npmSetup)
    }
    spotlessCheck {
        dependsOn(prettierCheck)
    }
    spotlessApply {
        dependsOn(prettierWrite)
    }
}
