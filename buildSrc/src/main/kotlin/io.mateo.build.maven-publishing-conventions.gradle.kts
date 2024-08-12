plugins {
    id("java") apply false
    `maven-publish`
}

val publicationName = "mavenJava"

publishing {
    publications {
        register(publicationName, MavenPublication::class) {
            pom {
                name = provider { project.name }
                description =
                    provider {
                        requireNotNull(project.description) {
                            "Description must not be null for project '${project.name}'"
                        }
                    }
                url = "https://github.com/ciscoo/gradle-java-template"
                scm {
                    connection = "scm:git:git://github.com/ciscoo/gradle-java-template.git"
                    developerConnection = "scm:git:git://github.com/ciscoo/gradle-java-template.git"
                    url = "https://github.com/ciscoo/gradle-java-template"
                }
                licenses {
                    license {
                        name = "Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                    }
                }
                developers {
                    developer {
                        name = "Francisco Mateo"
                        email = "cisco21c@gmail.com"
                    }
                }
            }
        }
    }
}

pluginManager.withPlugin("java") {
    publishing.publications.named(publicationName, MavenPublication::class) {
        from(components["java"])
        versionMapping {
            usage(Usage.JAVA_API) {
                fromResolutionOf(sourceSets.main.get().runtimeClasspathConfigurationName)
            }
            usage(Usage.JAVA_RUNTIME) {
                fromResolutionResult()
            }
        }
    }
}

pluginManager.withPlugin("java-platform") {
    publishing.publications.named(publicationName, MavenPublication::class) {
        from(components["javaPlatform"])
    }
}

tasks {
    withType<PublishToMavenRepository>().configureEach {
        dependsOn(build)
    }
    withType<PublishToMavenLocal>().configureEach {
        dependsOn(build)
    }
}
