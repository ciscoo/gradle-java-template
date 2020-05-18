package io.mateo.gradle.build;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;

public class PublishedPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getPluginManager().apply(PublishingConventionsPlugin.class);
		configurePublishedArtifact(project);
	}

	private void configurePublishedArtifact(Project project) {
		PluginManager pluginManager = project.getPluginManager();
		boolean isJavaProject = pluginManager.hasPlugin("java");
		boolean isJavaPlatform = pluginManager.hasPlugin("java-platform");
		project.getExtensions().configure(PublishingExtension.class, (publishing) -> {
			publishing.getPublications().named(PublishingConventionsPlugin.MAVEN_PUBLICATION_NAME,
					MavenPublication.class, (maven) -> {
						if (isJavaProject) {
							addArtifact(maven, project, "java");
						}
						if (isJavaPlatform) {
							addArtifact(maven, project, "javaPlatform");
						}
					});
		});
	}

	public void addArtifact(MavenPublication maven, Project project, String componentName) {
		project.getComponents().matching((component) -> componentName.equals(component.getName()))
				.configureEach(maven::from);
	}
}
