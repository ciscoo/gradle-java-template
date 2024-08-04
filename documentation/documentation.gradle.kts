import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("io.mateo.build.java-conventions")
    alias(libs.plugins.spotless)
    alias(libs.plugins.node)
}

node {
    version = provider { layout.projectDirectory.file(".nvmrc").asFile.readText().drop(1).trim() }
    download = true
}

tasks {
    val generateGradleProjectMetadata by registering(io.mateo.build.task.GenerateGradleProjectMetadata::class)
    val vitePressDev by registering(NpmTask::class) {
        description = "Start VitePress dev server."
        args = listOf("run", "docs:dev")
    }
    val vitePressBuild by registering(NpmTask::class) {
        description = "Build the VitePress site for production."
        args = listOf("run", "docs:build")
        outputs.dir(layout.buildDirectory.dir("vitepress-dist"))
    }
    val vitePressPreview by registering(NpmTask::class) {
        description = "Locally preview the production build."
        args = listOf("run", "docs:preview")
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
    withType(NpmTask::class.java).configureEach {
        if (!name.startsWith("vite")) {
            return@configureEach
        }
        inputs.files(generateGradleProjectMetadata)
        inputs.dir(layout.projectDirectory.dir(".vitepress"))
        outputs.dir(layout.projectDirectory.dir(".vitepress/cache"))
    }
    spotlessCheck {
        dependsOn(prettierCheck)
    }
    spotlessApply {
        dependsOn(prettierWrite)
    }
    clean {
        delete(vitePressDev)
        delete(vitePressBuild)
        delete(vitePressPreview)
        delete(layout.buildDirectory.dir("node_modules"))
        if (providers.gradleProperty("cleanNode").isPresent) {
            delete(nodeSetup)
            delete(npmSetup)
        }
    }
}
