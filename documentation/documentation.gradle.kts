import com.github.gradle.node.npm.task.NpmTask
import io.mateo.build.task.GenerateGradleProjectMetadata

plugins {
    base
    id("com.diffplug.spotless")
    alias(libs.plugins.node)
    alias(libs.plugins.gradleGitPublish)
}

val snapshot = rootProject.version.toString().contains("SNAPSHOT")
val replaceCurrentDocs = project.hasProperty("replaceCurrentDocs")
val docsVersion = if (snapshot) {
    "current-SNAPSHOT"
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

gitPublish {
    repoUri = "https://github.com/ciscoo/gradle-java-template.git"
    branch = "gh-pages"
    contents {
        from(docsDir)
        into("docs")
    }
    preserve {
        include("**/*")
        exclude("docs/$docsVersion/**")
        if (replaceCurrentDocs) {
            exclude("docs/current/**")
        }
    }
}

tasks {
    val generateGradleProjectMetadata by registering(GenerateGradleProjectMetadata::class) {
        properties.put("docsVersion", docsVersion)
    }
    npmInstall {
        outputs.dir(layout.projectDirectory.dir("node_modules"))
    }
    val vitePressDev by registering(NpmTask::class) {
        description = "Start VitePress dev server."
        dependsOn(npmInstall)
        inputs.files(generateGradleProjectMetadata)
        args = listOf("run", "docs:dev")
    }
    val vitePressBuild by registering(NpmTask::class) {
        description = "Build the VitePress site for production."
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
        description = "Locally preview the production build."
        dependsOn(npmInstall)
        inputs.files(generateGradleProjectMetadata)
        args = listOf("run", "docs:preview")
    }
    val prettierCheck by registering(NpmTask::class) {
        description = "Check if files are formatted."
        dependsOn(npmInstall)
        args = listOf("run", "prettier:check")
    }
    val prettierWrite by registering(NpmTask::class) {
        description = "Rewrites all processed files in place."
        dependsOn(npmInstall)
        args = listOf("run", "prettier:write")
    }
    val prepareVersionedDocs by registering(Copy::class) {
        description = "Copies the built the VitePress site a versioned folder."
        from(vitePressBuild)
        into(docsDir.map { it.dir(docsVersion) })
    }
    val createCurrentDocsFolder by registering(Copy::class) {
        description = "Copies the built the VitePress site to the 'current' docs folder"
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
