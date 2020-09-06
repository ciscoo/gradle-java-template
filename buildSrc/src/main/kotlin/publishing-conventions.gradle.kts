pluginManager.withPlugin("maven-publish") {
	tasks {
		withType<PublishToMavenRepository>().configureEach {
			dependsOn(named(LifecycleBasePlugin.BUILD_TASK_NAME))
		}
		withType<PublishToMavenLocal>().configureEach {
			dependsOn(named(LifecycleBasePlugin.BUILD_TASK_NAME))
		}
	}
	configure<PublishingExtension> {
		publications {
			register<MavenPublication>("maven") {
				pom {
					name.set(project.provider{ project.name })
					description.set(project.provider { requireNotNull(project.description) })
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
