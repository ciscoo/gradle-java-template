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
    version = provider { layout.projectDirectory.file(".nvmrc").asFile.readText().drop(1).trim() }
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
    clean {
        delete(npmInstall)
    }
}
