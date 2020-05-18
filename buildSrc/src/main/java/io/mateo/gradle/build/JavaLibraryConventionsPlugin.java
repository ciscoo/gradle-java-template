package io.mateo.gradle.build;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.ConfigurationVariantDetails;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.compile.JavaCompile;

import java.util.List;

public class JavaLibraryConventionsPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getPluginManager().apply(JavaBaseConventionsPlugin.class);
		project.getPluginManager().withPlugin("java-library", (plugin) -> {
			configureJavaExtension(project);
			configureCompileTasks(project);
			disableTestFixturesPublication(project);
		});
	}

	private void disableTestFixturesPublication(Project project) {
		project.getPluginManager().withPlugin("java-test-fixtures", (plugin) -> {
			project.getComponents().named("java", AdhocComponentWithVariants.class, (java) -> {
				List.of("testFixturesApiElements", "testFixturesRuntimeElements").forEach((name) -> {
					java.withVariantsFromConfiguration(project.getConfigurations().named(name).get(),
							ConfigurationVariantDetails::skip);
				});
			});
		});
	}

	private void configureCompileTasks(Project project) {
		// https://docs.oracle.com/en/java/javase/14/docs/specs/man/javac.html
		TaskContainer tasks = project.getTasks();
		tasks.named(JavaPlugin.COMPILE_JAVA_TASK_NAME, JavaCompile.class, (compileJava) -> {
			compileJava.getOptions().getCompilerArgs().addAll(List.of("-Xlint:all", "-Werror"));
		});
		tasks.named(JavaPlugin.COMPILE_TEST_JAVA_TASK_NAME, JavaCompile.class, (compileTestJava) -> {
			compileTestJava.getOptions().getCompilerArgs()
					.addAll(List.of("-Xlint", "-Xlint:-overrides", "-Werror", "-parameters"));
		});
	}

	@SuppressWarnings("UnstableApiUsage")
	private void configureJavaExtension(Project project) {
		project.getExtensions().configure(JavaPluginExtension.class, (java) -> {
			java.withJavadocJar();
			java.withSourcesJar();
		});
	}

}
