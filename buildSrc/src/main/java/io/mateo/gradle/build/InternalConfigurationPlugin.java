package io.mateo.gradle.build;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.specs.Spec;

import java.util.List;

public class InternalConfigurationPlugin implements Plugin<Project> {

	private static final Spec<Configuration> ALL_CLASSPATH_SPEC = (configuration) -> configuration.getName()
			.endsWith("Classpath");

	private static final Spec<Configuration> ANNOTATION_PROCESSOR_SPEC = (
			configuration) -> JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME.equals(configuration.getName());

	private static final List<Spec<Configuration>> SPECS = List.of(ALL_CLASSPATH_SPEC, ANNOTATION_PROCESSOR_SPEC);

	@Override
	public void apply(Project project) {
		project.getPluginManager().withPlugin("java-base", (plugin) -> configureConfiguration(project));
	}

	private void configureConfiguration(Project project) {
		ConfigurationContainer configurations = project.getConfigurations();
		NamedDomainObjectProvider<Configuration> internalConfiguration = configurations.register("internal",
				(internal) -> {
					internal.setVisible(false);
					internal.setCanBeConsumed(false);
					internal.setCanBeResolved(false);
				});
		SPECS.forEach((spec) -> configurations.matching(spec)
				.configureEach((configuration) -> configuration.extendsFrom(internalConfiguration.get())));
	}

}
