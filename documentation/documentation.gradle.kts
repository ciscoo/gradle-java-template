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

val aggregateJavadoc by configurations.dependencyScope("aggregateJavadoc")

val javadoc by configurations.dependencyScope("javadoc") {
    extendsFrom(aggregateJavadoc)
}
val javadocClasspath by configurations.resolvable("javadocClasspath") {
    description = "Classpath for aggregated Javadoc generation."
    extendsFrom(javadoc)
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
    }
}

val javadocSource by configurations.dependencyScope("javadocSource") {
    extendsFrom(aggregateJavadoc)
}
val javadocSources by configurations.resolvable("javadocSources") {
    description = "Java sources for aggregated Javadoc generation."
    isTransitive = false
    extendsFrom(javadocSource)
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.VERIFICATION))
        attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        attribute(VerificationType.VERIFICATION_TYPE_ATTRIBUTE, objects.named(VerificationType.MAIN_SOURCES))
    }
}

dependencies {
    aggregateJavadoc(projects.exampleApi)
}

tasks {
    val javadocAggregate by registering(Javadoc::class) {
        description = "Aggregates Javadoc from projects."
        destinationDir = layout.projectDirectory.dir("public/javadoc").asFile
        classpath = javadocClasspath
        source(
            javadocSources.incoming
                .files
                .asFileTree
                .matching { include("**/*.java") },
        )
        options {
            source =
                javaToolchainExtension.releaseVersion
                    .get()
                    .asInt()
                    .toString()
            this as StandardJavadocDocletOptions
            links("https://jspecify.dev/docs/api")
        }
    }
    val vitePressDev by registering(NpmTask::class) {
        inputs.files(javadocAggregate, npmInstall)
        description = "Start VitePress dev server."
        npmCommand = listOf("run", "dev")
        outputs.file(file(".vitepress/cache"))
    }
    val vitePressBuild by registering(NpmTask::class) {
        inputs.files(javadocAggregate, npmInstall)
        description = "Build the VitePress site for production."
        npmCommand = listOf("run", "build")
        outputs.dir(layout.buildDirectory.dir("user-guide"))
        outputs.upToDateWhen { false }
    }
    val prepareDocsForUpload by registering(Sync::class) {
        from(javadocAggregate) {
            into("api")
        }
        from(vitePressBuild) {
            into("user-guide")
        }
        into(layout.buildDirectory.dir("docs-upload"))
    }
    val prettierWrite by registering(NpmTask::class) {
        inputs.files(npmInstall)
        description = "Format with Prettier."
        npmCommand = listOf("run", "format:write")
        outputs.upToDateWhen { false }
    }
    val prettierCheck by registering(NpmTask::class) {
        inputs.files(npmInstall)
        description = "Check formatting with Prettier."
        npmCommand = listOf("run", "format:check")
    }
    spotlessCheck {
        finalizedBy(prettierCheck)
    }
    spotlessApply {
        finalizedBy(prettierWrite)
    }
    clean {
        delete(prepareDocsForUpload)
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
