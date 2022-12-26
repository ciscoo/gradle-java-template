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

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.PluginManager;

import java.util.Collections;

/**
 * Conventions for compiling Java library sources.
 */
public class JavaLibraryConventions implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply("java-library");
		pluginManager.apply("io.mateo.build.java-conventions");
		pluginManager.apply("io.mateo.build.dependency-management-conventions");
		configureDocumentationSources(project);
		dependOnInternalPlatform(project);
	}

	private void dependOnInternalPlatform(Project project) {
		DependencyHandler dependencies = project.getDependencies();
		Dependency dependenciesDependency = project.getDependencies()
				.platform(dependencies.project(Collections.singletonMap("path", ":dependencies")));
		dependencies.add(DependencyManagementConventions.DEPENDENCY_MANAGEMENT_CONFIGURATION_NAME,
				dependenciesDependency);
	}

	private void configureDocumentationSources(Project project) {
		project.getExtensions().configure(JavaPluginExtension.class, java -> {
			java.withSourcesJar();
			java.withJavadocJar();
		});
	}

}
