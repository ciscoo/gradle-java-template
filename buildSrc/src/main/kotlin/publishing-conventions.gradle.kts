plugins {
    `maven-publish`
}

tasks {
    val buildTask by named(LifecycleBasePlugin.BUILD_TASK_NAME)
    withType<PublishToMavenRepository>().configureEach {
        dependsOn(buildTask)
    }
    withType<PublishToMavenLocal>().configureEach {
        dependsOn(buildTask)
    }
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            if (pluginManager.hasPlugin("java")) {
                from(components["java"])
            }
            if (pluginManager.hasPlugin("java-platform")) {
                from(components["javaPlatform"])
            }
            pom {
                name.set(project.provider{ project.name })
                description.set(project.provider {
                    requireNotNull(project.description) { "Project description is not defined for '${project.name}'" }
                })
                url.set("https://github.com/ciscoo/gradle-java-template")
                scm {
                    connection.set("scm:git:git://github.com/ciscoo/gradle-java-template.git")
                    developerConnection.set("scm:git:git://github.com/ciscoo/gradle-java-template.git")
                    url.set("https://github.com/ciscoo/gradle-java-template")
                }
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        name.set("Francisco Mateo")
                        email.set("cisco21c@gmail.com")
                    }
                }
            }
            if (pluginManager.hasPlugin("java")) {
                versionMapping {
                    usage(Usage.JAVA_API) {
                        fromResolutionOf(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)
                    }
                    usage(Usage.JAVA_RUNTIME) {
                        fromResolutionResult()
                    }
                }
            }
        }
    }
}
