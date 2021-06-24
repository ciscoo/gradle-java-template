package io.mateo.build;

import com.diffplug.gradle.spotless.SpotlessExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CodeStyleConventions implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getPluginManager().apply("com.diffplug.spotless");
		project.getPluginManager().withPlugin("java", (plugin) -> {
			configureJavaCodeStyle(project);
		});
		configureDocumentation(project);
	}

	private void configureJavaCodeStyle(Project project) {
		project.getExtensions().configure(SpotlessExtension.class, (spotless) -> {
			spotless.java((java) -> {
				Project rootProject = project.getRootProject();
				java.licenseHeaderFile(rootProject.file("src/spotless/apache-license-2.0.java"),
						"(package|import|open|module)");
				java.importOrderFile(rootProject.file("src/eclipse/eclipse.importorder"));
				java.eclipse().configFile(rootProject.file("src/eclipse/eclipse-formatter-settings.xml"));
				java.removeUnusedImports();
				java.trimTrailingWhitespace();
				java.endWithNewline();
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
