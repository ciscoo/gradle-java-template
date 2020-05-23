package io.mateo.gradle.build;

import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.VariantVersionMappingStrategy;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.publish.maven.tasks.PublishToMavenLocal;
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.plugins.signing.Sign;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;

public class PublishingConventionsPlugin implements Plugin<Project> {

	static final String MAVEN_PUBLICATION_NAME = "maven";

	@Override
	public void apply(Project project) {
		PluginManager pluginManager = project.getPluginManager();
		project.getPluginManager().withPlugin("maven-publish", (plugin) -> {
			ensureBuildBeforePublish(project);
			configurePublications(project);
			configurePublishedArtifacts(project);
		});
		project.getPluginManager().withPlugin("signing", (plugin) -> {
			configureSigningTasks(project);
		});
		pluginManager.apply(MavenPublishPlugin.class);
		pluginManager.apply(SigningPlugin.class);
	}

	private void configurePublishedArtifacts(Project project) {
		project.getExtensions().configure(PublishingExtension.class, (publishing) -> {
			publishing.getPublications().named(MAVEN_PUBLICATION_NAME, MavenPublication.class, (maven) -> {
				maven.versionMapping((vm) -> vm.allVariants(VariantVersionMappingStrategy::fromResolutionResult));
				maven.pom((pom) -> pom.getDescription().set(project.provider(() -> "Module " + project.getName())));
			});
		});
	}

	private void configureSigningTasks(Project project) {
		boolean isSnapshot = project.getVersion().toString().contains("SNAPSHOT");
		boolean isCI = Boolean.parseBoolean(System.getenv("CI"));
		PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
		project.getExtensions().configure(SigningExtension.class, (signing) -> {
			signing.sign(publishing.getPublications());
			signing.setRequired(!(isSnapshot || isCI));
		});
		// Gradle Module Metadata currently does not support signing snapshots.
		project.getTasks().withType(Sign.class).configureEach((sign) -> sign.onlyIf((spec) -> !isSnapshot));
	}

	private void configurePublications(Project project) {
		project.getExtensions().configure(PublishingExtension.class, (publishing) -> {
			publishing.publications((publications) -> {
				publications.create(MAVEN_PUBLICATION_NAME, MavenPublication.class, (maven) -> {
					maven.pom((pom) -> {
						pom.getName().set(project.provider(() -> {
							if (StringUtils.isNotEmpty(project.getDescription())) {
								return project.getDescription();
							}
							return project.getGroup() + ":" + project.getName();
						}));
						pom.getUrl().set("https://github.com/ciscoo/gradle-java-template");
						pom.scm((scm) -> {
							scm.getConnection().set("scm:git:git://github.com/ciscoo/gradle-java-template.git");
							scm.getDeveloperConnection()
									.set("scm:git:git://github.com/ciscoo/gradle-java-template.git");
							scm.getUrl().set("https://github.com/ciscoo/gradle-java-template");
						});
						pom.licenses((licenses) -> {
							licenses.license((license) -> {
								license.getName().set("Apache License, Version 2.0");
								license.getUrl().set("https://www.apache.org/licenses/LICENSE-2.0");
							});
						});
					});
				});
			});
		});
	}

	private void ensureBuildBeforePublish(Project project) {
		TaskContainer tasks = project.getTasks();
		TaskProvider<Task> build = tasks.named(LifecycleBasePlugin.BUILD_TASK_NAME);
		tasks.withType(PublishToMavenRepository.class).configureEach((publish) -> publish.dependsOn(build));
		tasks.withType(PublishToMavenLocal.class).configureEach((publish) -> publish.dependsOn(build));
	}
}
