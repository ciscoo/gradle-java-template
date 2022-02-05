package io.mateo.build;

import com.diffplug.gradle.spotless.SpotlessExtension;
import io.spring.javaformat.gradle.tasks.CheckFormat;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskProvider;

public class CodeStyleConventions implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getPluginManager().apply("com.diffplug.spotless");
		project.getPluginManager().withPlugin("java", plugin -> {
			configureSpotlessJavaCodeStyle(project);
			configureSpringJavaFormat(project);
		});
		configureDocumentation(project);
	}

	private void configureSpringJavaFormat(Project project) {
		TaskProvider<Task> spotlessCheck = project.getTasks().named("spotlessCheck");
		project.getTasks().named(CheckFormat.NAME, checkFormat -> checkFormat.dependsOn(spotlessCheck));
	}

	private void configureSpotlessJavaCodeStyle(Project project) {
		project.getExtensions().configure(SpotlessExtension.class, (spotless) -> {
			spotless.java((java) -> {
				Project rootProject = project.getRootProject();
				java.licenseHeaderFile(rootProject.file("src/spotless/apache-license-2.0.java"),
						"(package|import|open|module)");
				java.removeUnusedImports();
				java.trimTrailingWhitespace();
			});
		});
	}

	private void configureDocumentation(Project project) {
		project.getExtensions().configure(SpotlessExtension.class, (spotless) -> {
			spotless.format("documentation", (documentation) -> {
				documentation.target("**/*.adoc", "**/*.md");
				documentation.trimTrailingWhitespace();
				documentation.endWithNewline();
			});
		});
	}

}
