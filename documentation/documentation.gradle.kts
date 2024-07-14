plugins {
    base
    alias(libs.plugins.spotless)
    alias(libs.plugins.anotra)
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
}
