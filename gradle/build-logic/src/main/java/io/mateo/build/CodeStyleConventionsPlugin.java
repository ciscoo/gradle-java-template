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

import com.diffplug.gradle.spotless.SpotlessExtension;
import io.spring.javaformat.gradle.tasks.CheckFormat;
import io.spring.javaformat.gradle.tasks.Format;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;

import java.nio.charset.StandardCharsets;

/**
 * Conventions for code style.
 */
public abstract class CodeStyleConventionsPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply("com.diffplug.spotless");
		pluginManager.apply("io.spring.javaformat");
		pluginManager.withPlugin("java", plugin -> {
			configureSpotlessJavaFormat(project);
			configureSpringJavaFormat(project);
		});
		configureSpotlessDocumentationFormat(project);
	}

	private void configureSpringJavaFormat(Project project) {
		project.getTasks()
			.named(CheckFormat.NAME, checkFormat -> checkFormat.dependsOn(project.getTasks().named("spotlessCheck")));
		project.getTasks().withType(Format.class, format -> format.setEncoding(StandardCharsets.UTF_8.name()));
	}

	private void configureSpotlessJavaFormat(Project project) {
		project.getExtensions().configure(SpotlessExtension.class, spotless -> spotless.java(java -> {
			java.licenseHeaderFile(getClass().getClassLoader().getResource("spotless/apache-license-2.0.java"),
					"(package|import|open|module)");
			java.removeUnusedImports();
			java.trimTrailingWhitespace();
			java.endWithNewline();
		}));
	}

	private void configureSpotlessDocumentationFormat(Project project) {
		project.getExtensions()
			.configure(SpotlessExtension.class, spotless -> spotless.format("documentation", documentation -> {
				documentation.target("**/*.adoc", "**/*.md");
				documentation.trimTrailingWhitespace();
				documentation.endWithNewline();
			}));
	}

}
