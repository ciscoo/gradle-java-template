/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mateo.build;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.attributes.Usage;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.VariantVersionMappingStrategy;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.tasks.PublishToMavenLocal;
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

public class MavenPublishingConventions implements Plugin<Project> {

	private final ProviderFactory providers;

	public MavenPublishingConventions(ProviderFactory providers) {
		this.providers = providers;
	}

	@Override
	public void apply(Project project) {
		project.getPluginManager().apply("maven-publish");
		ensureBuildBeforePublishing(project);
		configurePublication(project);
	}

	private void configurePublication(Project project) {
		project.getExtensions().configure(PublishingExtension.class, (publishing) -> {
			publishing.getPublications().register("maven", MavenPublication.class, (maven) -> {
				if (project.getPluginManager().hasPlugin("java")) {
					maven.from(project.getComponents().getByName("java"));
				}
				if (project.getPluginManager().hasPlugin("java-platform")) {
					maven.from(project.getComponents().getByName("javaPlatform"));
				}
				maven.pom((pom) -> {
					pom.getName().set(this.providers.provider(project::getName));
					pom.getDescription().set(this.providers.provider(() -> {
						if (project.getDescription() == null) {
							throw new GradleException("Project description is not defined for " + project.getName());
						}
						return project.getName();
					}));
					pom.getUrl().set("https://github.com/ciscoo/gradle-java-template");
					pom.scm((scm) -> {
						scm.getConnection().set("scm:git:git://github.com/ciscoo/gradle-java-template.git");
						scm.getDeveloperConnection().set("scm:git:git://github.com/ciscoo/gradle-java-template.git");
						scm.getUrl().set("https://github.com/ciscoo/gradle-java-template");
					});
					pom.licenses((licenses) -> {
						licenses.license((license) -> {
							license.getName().set("Apache License, Version 2.0");
							license.getUrl().set("https://www.apache.org/licenses/LICENSE-2.0");
						});
					});
					pom.developers((developers) -> {
						developers.developer((developer) -> {
							developer.getName().set("Francisco Mateo");
							developer.getEmail().set("cisco21c@gmail.com");
						});
					});
				});
				if (project.getPluginManager().hasPlugin("java")) {
					maven.versionMapping((versionMapping) -> {
						versionMapping.usage(Usage.JAVA_API, (variant) -> {
							variant.fromResolutionOf(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME);
						});
						versionMapping.usage(Usage.JAVA_RUNTIME, VariantVersionMappingStrategy::fromResolutionResult);
					});
				}
			});
		});
	}

	private void ensureBuildBeforePublishing(Project project) {
		TaskContainer tasks = project.getTasks();
		TaskProvider<Task> build = tasks.named(LifecycleBasePlugin.BUILD_TASK_NAME);
		tasks.withType(PublishToMavenRepository.class).configureEach((publish) -> publish.dependsOn(build));
		tasks.withType(PublishToMavenLocal.class).configureEach((publish) -> publish.dependsOn(build));
	}

}
