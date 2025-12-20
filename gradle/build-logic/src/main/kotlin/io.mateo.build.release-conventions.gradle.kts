import org.jreleaser.model.Active
import org.jreleaser.model.api.deploy.maven.MavenCentralMavenDeployer.Stage
import java.util.Properties

plugins {
    `lifecycle-base`
    id("org.jreleaser")
}

val stagingRepoDir = layout.buildDirectory.dir("staging-repo")
val publishAllModulesToStagingRepository by tasks.registering {
    description = "Publishes all modules to the staging repository."
}

tasks {
    clean {
        delete(stagingRepoDir)
    }
    jreleaserDeploy {
        dependsOn(publishAllModulesToStagingRepository)
        outputs.upToDateWhen { false }
        doLast {
            Properties()
                .apply {
                    layout.buildDirectory.file("jreleaser/output.properties").get().asFile.inputStream().use { input ->
                        load(input)
                    }
                }.getProperty("deploymentId")
                ?.let {
                    println("Deployment ID: $it")
                    println("Staging Repo URL: https://central.sonatype.com/api/v1/publisher/deployment/$it/download")
                }
        }
    }
}

jreleaser {
    deploy {
        maven {
            mavenCentral {
                register("release") {
                    active = Active.RELEASE
                    url = "https://central.sonatype.com/api/v1/publisher"
                    username = providers.gradleProperty("mavenCentralUsername")
                    password = providers.gradleProperty("mavenCentralPassword")
                    stagingRepository(stagingRepoDir.get().asFile.absolutePath)
                    applyMavenCentralRules = false
                    sourceJar = false
                    javadocJar = false
                    sign = false
                    checksums = false
                    verifyPom = false
                    namespace = "io.mateo"
                    stage =
                        providers
                            .environmentVariable("JRELEASER_MAVENCENTRAL_STAGE")
                            .map(Stage::of)
                            .orElse(Stage.UPLOAD)
                }
            }
        }
    }
}

subprojects {
    pluginManager.withPlugin("maven-publish") {
        configure<PublishingExtension> {
            repositories {
                maven {
                    name = "staging"
                    url = uri(stagingRepoDir)
                }
            }
        }
        val publishingTasks =
            tasks
                .withType<PublishToMavenRepository>()
                .named { it.endsWith("ToStagingRepository") }
        publishAllModulesToStagingRepository {
            dependsOn(publishingTasks)
        }
    }
}
