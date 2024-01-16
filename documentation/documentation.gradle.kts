import com.github.gradle.node.npm.task.NpmTask
import io.mateo.build.task.GenerateGradleProjectMetadata

plugins {
    base
    alias(libs.plugins.node)
}

node {
    version.set(provider {
        layout.projectDirectory.file(".nvmrc").asFile.readText().drop(1)
    })
    download.set(providers.environmentVariable("CI").orElse("false").map { it.toBoolean() })
}

tasks {
    val generateGradleProjectMetadata by registering(GenerateGradleProjectMetadata::class)
    val vitePressDev by registering(NpmTask::class) {
        dependsOn(npmInstall)
        inputs.files(generateGradleProjectMetadata)
        args = listOf("run", "docs:dev")
    }
    val vitePressBuild by registering(NpmTask::class) {
        dependsOn(npmInstall)
        inputs.files(generateGradleProjectMetadata)
        args = listOf("run", "docs:build")
        outputs.dir(layout.buildDirectory.dir("vitepress-dist"))
    }
    val vitePressPreview by registering(NpmTask::class) {
        dependsOn(npmInstall)
        inputs.files(generateGradleProjectMetadata)
        args = listOf("run", "docs:preview")
    }
}
