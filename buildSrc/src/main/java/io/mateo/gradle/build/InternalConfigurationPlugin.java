package io.mateo.gradle.build;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;

public class InternalConfigurationPlugin implements Plugin<Project> {

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
		configurations.matching((configuration) -> configuration.getName().endsWith("Classpath"))
				.configureEach((configuration) -> configuration.extendsFrom(internalConfiguration.get()));
	}

}
