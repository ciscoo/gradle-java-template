import com.github.gradle.node.npm.task.NpmTask
import io.mateo.build.task.GenerateGradleProjectMetadata

plugins {
    base
    id("com.diffplug.spotless")
    alias(libs.plugins.node)
}

val snapshot = rootProject.version.toString().contains("SNAPSHOT")
val replaceCurrentDocs = project.hasProperty("replaceCurrentDocs")
val docsVersion = if (snapshot) {
    "snapshot"
} else if (replaceCurrentDocs) {
    "current"
} else {
    rootProject.version.toString()
}
val docsDir = layout.buildDirectory.dir("ghpages-docs")

node {
    version.set(provider {
        layout.projectDirectory.file(".nvmrc").asFile.readText().drop(1).trim()
    })
    download = true
}

tasks {
    val generateGradleProjectMetadata by registering(GenerateGradleProjectMetadata::class) {
        properties.put("docsVersion", docsVersion)
    }
    npmInstall {
        outputs.dir(layout.projectDirectory.dir("node_modules"))
    }
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
        outputs.upToDateWhen {
            // Force rerun since the base URL needs to contain 'current'
            // in order for navigation to work correctly. For snapshots,
            // the version is always 'snapshot'.
            !replaceCurrentDocs
        }
    }
    val vitePressPreview by registering(NpmTask::class) {
        dependsOn(npmInstall)
        inputs.files(generateGradleProjectMetadata)
        args = listOf("run", "docs:preview")
    }
    val prettierCheck by registering(NpmTask::class) {
        dependsOn(npmInstall)
        args = listOf("run", "prettier:check")
    }
    val prettierWrite by registering(NpmTask::class) {
        dependsOn(npmInstall)
        args = listOf("run", "prettier:write")
    }
    val prepareVersionedDocs by registering(Copy::class) {
        from(vitePressBuild)
        into(docsDir.map { it.dir(docsVersion) })
    }
    val createCurrentDocsFolder by registering(Copy::class) {
        from(vitePressBuild)
        into(docsDir.map { it.dir("current") })
        onlyIf("replaceCurrentDocs property specified") {
            replaceCurrentDocs
        }
    }
    clean {
        delete(npmInstall)
    }
    spotlessCheck {
        dependsOn(prettierCheck)
    }
    spotlessApply {
        finalizedBy(prettierWrite)
    }
}
