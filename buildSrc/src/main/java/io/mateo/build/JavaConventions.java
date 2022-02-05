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
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.logging.TestLogEvent;
import org.gradle.jvm.tasks.Jar;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class JavaConventions implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply("io.mateo.build.code-style-conventions");
		pluginManager.apply("java");
		pluginManager.withPlugin("java", (plugin) -> {
			configureToolchain(project);
			configureJavaConventions(project);
			configureConfigurations(project);
			TaskProvider<Copy> copyLegalFiles = configureLegalFilesTask(project);
			configureJarConventions(project, copyLegalFiles);
			configureTestConventions(project);
		});
	}

	private void configureJarConventions(Project project, TaskProvider<Copy> copyLegalFiles) {
		SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
		Set<String> sourceJarTaskNames = sourceSets.stream().map(SourceSet::getSourcesJarTaskName)
				.collect(Collectors.toSet());
		Set<String> javadocJarTaskNames = sourceSets.stream().map(SourceSet::getJavadocJarTaskName)
				.collect(Collectors.toSet());
		project.getTasks().withType(Jar.class).configureEach((jar) -> {
			jar.getMetaInf().from(copyLegalFiles);
			jar.manifest((manifest) -> {
				Map<String, Object> attributes = new TreeMap<>();
				attributes.put("Automatic-Module-Name", project.getName().replace("-", "."));
				attributes.put("Created-By", createCreatedBy());
				attributes.put("Implementation-Title",
						determineImplementationTitle(project, sourceJarTaskNames, javadocJarTaskNames, jar));
				attributes.put("Implementation-Version", project.getVersion());
				manifest.attributes(attributes);
			});
		});
	}

	private String createCreatedBy() {
		return String.format("%s (%s %s)", System.getProperty("java.version"), System.getProperty("java.vendor"),
				System.getProperty("java.vm.version"));
	}

	private String determineImplementationTitle(Project project, Set<String> sourceJarTaskNames,
			Set<String> javadocJarTaskNames, Jar jar) {
		if (sourceJarTaskNames.contains(jar.getName())) {
			return "Source for " + project.getName();
		}
		if (javadocJarTaskNames.contains(jar.getName())) {
			return "Javadoc for " + project.getName();
		}
		return Objects.requireNonNull(project.getDescription(),
				"Project description must not null for " + project.getName());
	}

	private TaskProvider<Copy> configureLegalFilesTask(Project project) {
		return project.getTasks().register("copyLegalFiles", Copy.class, (task) -> {
			task.from(Path.of(project.getRootProject().file("build-logic/src/main/resources").toURI()));
			task.include("NOTICE.txt", "LICENSE.txt");
			task.into(project.getLayout().getBuildDirectory().dir("legal"));
		});
	}

	private void configureTestConventions(Project project) {
		project.getTasks().withType(Test.class, (test) -> {
			test.useJUnitPlatform();
			Set<TestLogEvent> events = new HashSet<>();
			events.add(TestLogEvent.FAILED);
			events.add(TestLogEvent.SKIPPED);
			test.getTestLogging().setEvents(Collections.unmodifiableSet(events));
		});
	}

	private void configureJavaConventions(Project project) {
		project.setGroup("io.mateo");
		project.getTasks().withType(JavaCompile.class, (compileJava) -> {
			CompileOptions options = compileJava.getOptions();
			options.setEncoding(StandardCharsets.UTF_8.toString());
			options.getRelease().set(Integer.parseInt(Versions.JAVA_VERSION_RELEASE.getMajorVersion()));
			if (options.getCompilerArgs().contains("-parameters")) {
				options.getCompilerArgs().add("-parameters");
			}
		});
	}

	private void configureToolchain(Project project) {
		project.getExtensions().configure(JavaPluginExtension.class, (java) -> {
			java.toolchain((toolchain) -> toolchain.getLanguageVersion()
					.set(JavaLanguageVersion.of(Integer.parseInt(Versions.JAVA_VERSION_TARGET.getMajorVersion()))));
		});
	}

	private void configureConfigurations(Project project) {
		project.getConfigurations()
				.configureEach((configuration) -> configuration.resolutionStrategy((resolutionStrategy) -> {
					resolutionStrategy.cacheDynamicVersionsFor(0, TimeUnit.MINUTES);
					resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.MINUTES);
				}));
	}

}
