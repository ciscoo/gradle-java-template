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
