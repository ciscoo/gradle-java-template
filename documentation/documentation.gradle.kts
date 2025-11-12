import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("io.mateo.build.java-conventions")
    alias(libs.plugins.node)
    alias(libs.plugins.spotless)
}

description = "Gradle Java Template Documentation"

node {
    if (providers.environmentVariable("CI").isPresent) {
        npmInstallCommand = "ci --silent --no-progress"
    }
}

tasks {
    val vitePressDev by registering(NpmTask::class) {
        inputs.files(npmInstall)
        description = "Start VitePress dev server."
        npmCommand = listOf("run", "dev")
        outputs.file(file(".vitepress/cache"))
    }
    val vitePressBuild by registering(NpmTask::class) {
        inputs.files(npmInstall)
        description = "Build the VitePress site for production."
        npmCommand = listOf("run", "build")
        outputs.file(layout.buildDirectory.dir("user-guide"))
    }
    clean {
        delete(vitePressDev)
        delete(vitePressBuild)
        delete(
            npmInstall.map {
                it.outputs.files.filter { file ->
                    !file.name.startsWith("package-lock")
                }
            },
        )
    }
    jar {
        enabled = false
    }
    javadoc {
        enabled = false
    }
}
