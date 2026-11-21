plugins {
    id("java-library-conventions")
    id("javadoc-aggregation-conventions")
}

description = "Gradle Java Template Documentation"

val publishedProjects = extra["publishedProjectPaths"] as List<Any?>

dependencies {
    publishedProjects.forEach {
        javadoc(project(it as String))
    }
}

tasks {
    val docsBuild =
        register<Exec>("docsBuild") {
            description = "Builds the Zensical documentation site."
            inputs.files(javadocAggregate)
            inputs.dir("docs")
            inputs.file("zensical.toml")
            outputs.dir(layout.buildDirectory.dir("site"))
            executable = "uv"
            args = listOf("run", "zensical", "build", "--clean")
        }
    register<Sync>("prepareDocs") {
        description = "Prepares the documentation site for GitHub Pages upload."
        into(layout.buildDirectory.dir("gh-pages"))
        from(docsBuild)
        from(javadocAggregate) {
            into("api")
        }
    }
    jar {
        enabled = false
    }
    javadoc {
        enabled = false
    }
    javadocJar {
        enabled = false
    }
    sourcesJar {
        enabled = false
    }
}
