package io.mateo.build;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions;
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile;

import java.util.ArrayList;
import java.util.List;

public class KotlinConventions implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply("io.mateo.java-conventions");
		pluginManager.apply("org.jetbrains.kotlin.jvm");
		pluginManager.withPlugin("org.jetbrains.kotlin.jvm", (plugin) -> {
			project.getTasks().withType(KotlinCompile.class).configureEach((compile) -> {
				KotlinJvmOptions kotlinOptions = compile.getKotlinOptions();
				kotlinOptions.setApiVersion("1.3");
				kotlinOptions.setLanguageVersion("1.3");
				kotlinOptions.setAllWarningsAsErrors(true);
				List<String> freeCompilerArgs = new ArrayList<>(compile.getKotlinOptions().getFreeCompilerArgs());
				freeCompilerArgs.add("-Xsuppress-version-warnings");
				compile.getKotlinOptions().setFreeCompilerArgs(freeCompilerArgs);
			});
		});
	}

}
