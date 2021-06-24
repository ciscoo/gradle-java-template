package io.mateo.build;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.JavadocMemberLevel;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Locale;

import static org.gradle.api.plugins.JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME;

public class JavaLibraryConventions implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply("io.mateo.build.java-conventions");
		pluginManager.apply("java-library");
		pluginManager.withPlugin("java-library", (plugin) -> {
			configureJavaLibraryConventions(project);
			configureInternalConfiguration(project);
			configureJavadocConventions(project);
		});
	}

	private void configureJavadocConventions(Project project) {
		project.getTasks().withType(Javadoc.class, (javadoc) -> {
			StandardJavadocDocletOptions options = (StandardJavadocDocletOptions) javadoc.getOptions();
			options.setMemberLevel(JavadocMemberLevel.PROTECTED);
			options.setHeader(project.getName());
			options.setEncoding(StandardCharsets.UTF_8.toString());
			options.setLocale(Locale.ENGLISH.getLanguage());
			options.addBooleanOption("Xdoclint:html,syntax", true);
			options.addBooleanOption("html5", true);
			options.use();
			options.noTimestamp();
		});
	}

	private void configureJavaLibraryConventions(Project project) {
		project.getExtensions().configure(JavaPluginExtension.class, (java) -> {
			java.withSourcesJar();
			java.withJavadocJar();
		});
	}

	private void configureInternalConfiguration(Project project) {
		ConfigurationContainer configurations = project.getConfigurations();
		NamedDomainObjectProvider<Configuration> internal = configurations.register("internal", (configuration) -> {
			configuration.setVisible(false);
			configuration.setCanBeConsumed(false);
			configuration.setCanBeResolved(false);
		});
		configurations.matching((configuration) -> configuration.getName().endsWith("Classpath"))
				.configureEach((configuration) -> configuration.extendsFrom(internal.get()));
		configurations
				.matching((configuration) -> configuration.getName().equals(ANNOTATION_PROCESSOR_CONFIGURATION_NAME))
				.configureEach((configuration) -> configuration.extendsFrom(internal.get()));
		DependencyHandler dependencies = project.getDependencies();
		Dependency dependenciesDependency = project.getDependencies()
				.platform(dependencies.project(Collections.singletonMap("path", ":dependencies")));
		dependencies.add(internal.getName(), dependenciesDependency);
	}

}
