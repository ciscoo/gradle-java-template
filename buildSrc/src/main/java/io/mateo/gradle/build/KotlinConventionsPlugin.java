package io.mateo.gradle.build;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions;
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile;

public class KotlinConventionsPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getTasks().withType(KotlinCompile.class).configureEach((kotlinCompile) -> {
			KotlinJvmOptions kotlinOptions = kotlinCompile.getKotlinOptions();
			kotlinOptions.setJvmTarget(Versions.jvmTarget.toString());
			kotlinOptions.setApiVersion("1.3");
			kotlinOptions.setLanguageVersion("1.3");
		});
	}

}
