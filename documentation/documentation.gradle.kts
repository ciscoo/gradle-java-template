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
    val generateGradleProjectMetadata by registering(io.mateo.build.task.GenerateGradleProjectMetadata::class)
    val vitePressDev by registering(NpmTask::class) {
        description = "Start VitePress dev server."
        inputs.files(generateGradleProjectMetadata)
        args = listOf("run", "docs:dev")
    }
    val vitePressBuild by registering(NpmTask::class) {
        description = "Build the VitePress site for production."
        inputs.files(generateGradleProjectMetadata)
        args = listOf("run", "docs:build")
        outputs.dir(layout.buildDirectory.dir("vitepress-dist"))
    }
    val vitePressPreview by registering(NpmTask::class) {
        description = "Locally preview the production build."
        dependsOn(npmInstall)
        inputs.files(generateGradleProjectMetadata)
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
    spotlessCheck {
        dependsOn(prettierCheck)
    }
    spotlessApply {
        dependsOn(prettierWrite)
    }
}
