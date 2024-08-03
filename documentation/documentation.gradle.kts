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
    npmInstall {
        outputs.dir(layout.projectDirectory.dir("node_modules"))
    }
    clean {
        delete(npmInstall)
    }
    val prettierCheck by registering(NpmTask::class) {
        inputs.files(npmSetup)
        dependsOn(npmInstall)
        args = listOf("run", "prettier:check")
    }
    val prettierWrite by registering(NpmTask::class) {
        inputs.files(npmSetup)
        args = listOf("run", "prettier:write")
    }
    spotlessCheck {
        finalizedBy(prettierCheck)
    }
    spotlessApply {
        finalizedBy(prettierWrite)
    }
}
