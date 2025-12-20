import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("io.mateo.build.java-conventions")
    alias(libs.plugins.node)
    alias(libs.plugins.spotless)
}

description = "Gradle Java Template Documentation"

repositories {
    // Redefined here because the Node.js plugin adds a repo
    // and by convention, project repositories are preferred
    // when dependencyResolutionManagement is used in settings.
    mavenCentral()
}

node {
    download = !providers.environmentVariable("CI").isPresent
    version =
        providers.fileContents(layout.projectDirectory.file(".nvmrc")).asText.map {
            it.drop(1).trim()
        }
}

java {
    docsDir = layout.projectDirectory.dir("public")
}

val javadoc =
    configurations.dependencyScope("javadoc") {
        description = "Dependencies for Javadoc aggregation."
    }

val aggregateJavadoc =
    configurations.consumable("aggregateJavadoc") {
        description = "Shared dependencies for aggregated Javadoc generation."
        extendsFrom(javadoc.get())
        attributes {
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EMBEDDED))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType.JAVADOC))
        }
    }

val javadocSources =
    configurations.resolvable("javadocSources") {
        description = "Java sources for aggregated Javadoc generation."
        isTransitive = false
        extendsFrom(aggregateJavadoc.get())
        attributes {
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.VERIFICATION))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType.SOURCES))
            attribute(VerificationType.VERIFICATION_TYPE_ATTRIBUTE, objects.named(VerificationType.MAIN_SOURCES))
        }
    }

val javadocClasspath =
    configurations.resolvable("javadocClasspath") {
        description = "Classpath for aggregated Javadoc generation to resolve type references in the source code."
        extendsFrom(aggregateJavadoc.get())
        attributes {
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
        }
    }

dependencies {
    javadoc(projects.exampleApi)
}

tasks {
    jar {
        enabled = false
    }
    javadoc {
        description = "Generates aggregated Javadoc API documentation for the project."
        classpath = javadocClasspath.get()
        setSource(javadocSources)
        include("**/*.java")
        options {
            source =
                javaToolchainExtension
                    .targetVersion
                    .get()
                    .asInt()
                    .toString()
            this as StandardJavadocDocletOptions
            links("https://jspecify.dev/docs/api")
        }
    }
    npmInstall {
        args.addAll("--no-audit", "--no-package-lock", "--no-fund")
    }
    register<NpmTask>("prettierWrite") {
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
    val astroDev by registering(NpmTask::class) {
        inputs.files(npmInstall, javadoc)
        description = "Runs Astro's development server"
        npmCommand = listOf("run", "dev")
        outputs.dir(".astro")
    }
    val astroBuild by registering(NpmTask::class) {
        dependsOn(prettierCheck)
        inputs.files(npmInstall)
        description = "Build the Astro site."
        npmCommand = listOf("run", "build")
        outputs.dir(layout.buildDirectory.dir("dist"))
        outputs.upToDateWhen { false }
    }
    register<NpmTask>("astroPreview") {
        inputs.files(astroBuild)
        description = "Preview the Astro site."
        npmCommand = listOf("run", "preview")
        outputs.upToDateWhen { false }
    }
    clean {
        delete(npmInstall)
        delete(astroDev)
        delete(astroBuild)
    }
}
