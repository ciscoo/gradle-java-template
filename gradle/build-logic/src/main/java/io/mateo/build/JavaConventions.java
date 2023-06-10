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
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.plugins.jvm.JvmTestSuite;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.testing.logging.TestLogEvent;
import org.gradle.external.javadoc.JavadocMemberLevel;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.jvm.toolchain.JavaToolchainService;
import org.gradle.testing.base.TestingExtension;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Conventions for compiling Java sources.
 */
public abstract class JavaConventions implements Plugin<Project> {

	/**
	 * Compiler arguments for all {@link JavaCompile} task types.
	 *
	 * @see <a href=
	 * "https://docs.oracle.com/en/java/javase/17/docs/specs/man/javac.html#extra-options">The
	 * <code>javac</code> Command - Extra Options</a>
	 */
	public static final List<String> COMPILER_ARGS;

	/**
	 * Compiler arguments for all <em>test</em> {@link JavaCompile} task types.
	 *
	 * @see <a href=
	 * "https://docs.oracle.com/en/java/javase/17/docs/specs/man/javac.html#extra-options">The
	 * <code>javac</code> Command - Extra Options</a>
	 */
	private static final List<String> TEST_COMPILER_ARGS;

	static {
		List<String> commonCompilerArgs = List.of("-Xlint:serial", "-Xlint:cast", "-Xlint:classfile", "-Xlint:dep-ann",
				"-Xlint:divzero", "-Xlint:empty", "-Xlint:finally", "-Xlint:overrides", "-Xlint:path",
				"-Xlint:processing", "-Xlint:static", "-Xlint:try", "-Xlint:-options", "-parameters");
		COMPILER_ARGS = new ArrayList<>();
		COMPILER_ARGS.addAll(commonCompilerArgs);
		COMPILER_ARGS.addAll(List.of("-Xlint:varargs", "-Xlint:fallthrough", "-Xlint:rawtypes", "-Xlint:deprecation",
				"-Xlint:unchecked", "-Werror"));
		TEST_COMPILER_ARGS = new ArrayList<>();
		TEST_COMPILER_ARGS.addAll(commonCompilerArgs);
		TEST_COMPILER_ARGS.addAll(List.of("-Xlint:-varargs", "-Xlint:-fallthrough", "-Xlint:-rawtypes",
				"-Xlint:-deprecation", "-Xlint:-unchecked"));
	}

	@Override
	public void apply(Project project) {
		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply("java");
		pluginManager.apply("io.mateo.build.code-style-conventions");
		JavaToolchainExtension extension = project.getExtensions()
			.create("javaToolchain", JavaToolchainExtension.class);
		disableCachingUnstableDependencies(project);
		configureToolchainConventions(project, extension);
		configureJavaCompileConventions(project, extension);
		configureJavadocConventions(project, extension);
		configureTestSuiteConventions(project);
	}

	private void configureJavadocConventions(Project project, JavaToolchainExtension extension) {
		project.getTasks().withType(Javadoc.class).configureEach(javadoc -> {
			StandardJavadocDocletOptions options = (StandardJavadocDocletOptions) javadoc.getOptions();
			options.setMemberLevel(JavadocMemberLevel.PROTECTED);
			options.setHeader(project.getName());
			options.setSource(extension.getReleaseVersion().get().toString());
			options.setEncoding(StandardCharsets.UTF_8.name());
			// https://docs.oracle.com/en/java/javase/17/docs/specs/man/javadoc.html
			options.addBooleanOption("Xdoclint:all", true);
			options.addBooleanOption("html5", true);
			options.use();
			options.noTimestamp();
		});
	}

	private void configureTestSuiteConventions(Project project) {
		project.getExtensions().getByType(TestingExtension.class).getSuites().configureEach(suite -> {
			JvmTestSuite jvmTestSuite = (JvmTestSuite) suite;
			jvmTestSuite.useJUnitJupiter();
			jvmTestSuite.getTargets().configureEach(target -> target.getTestTask().configure(task -> {
				task.setMaxHeapSize("1024M");
				task.getTestLogging().setEvents(EnumSet.of(TestLogEvent.FAILED, TestLogEvent.SKIPPED));
			}));
		});
	}

	private void configureJavaCompileConventions(Project project, JavaToolchainExtension extension) {
		TaskContainer tasks = project.getTasks();

		// Shared configuration for all Java sources.
		tasks.withType(JavaCompile.class)
			.configureEach(task -> task.getOptions().setEncoding(StandardCharsets.UTF_8.name()));

		// Configuration for the main Java source; generally only
		// one exists named 'compileJava'.
		tasks.named(JavaPlugin.COMPILE_JAVA_TASK_NAME, JavaCompile.class, task -> {
			CompileOptions options = task.getOptions();
			options.setCompilerArgs(COMPILER_ARGS);
			options.getRelease().set(extension.getReleaseVersion().map(JavaLanguageVersion::asInt));
			if (!options.getCompilerArgs().contains("-parameters")) {
				options.getCompilerArgs().add("-parameters");
			}
		});

		// Configuration for all Java test sources.
		// @formatter:off
		List<String> testSuiteCompileJavaTaskNames = project.getExtensions().getByType(TestingExtension.class).getSuites()
				.matching(suite -> suite instanceof JvmTestSuite) // To be safe, only want JvmTestSuite.
				.stream()
				.map(suite -> ((JvmTestSuite) suite).getSources().getCompileJavaTaskName())
				.toList();
		// @formatter:on

		for (String testSuiteCompileJavaTaskName : testSuiteCompileJavaTaskNames) {
			tasks.named(testSuiteCompileJavaTaskName, JavaCompile.class, task -> {
				// No need to configure 'release' since toolchain is configured.
				// Gradle will default to 'java.toolchain.languageVersion'.
				CompileOptions options = task.getOptions();
				options.setCompilerArgs(TEST_COMPILER_ARGS);
			});
		}
	}

	private void configureToolchainConventions(Project project, JavaToolchainExtension javaToolchainExtension) {
		JavaPluginExtension extension = project.getExtensions().getByType(JavaPluginExtension.class);
		JavaToolchainService javaToolchainService = project.getExtensions().getByType(JavaToolchainService.class);
		extension.getToolchain().getLanguageVersion().set(javaToolchainExtension.getTargetVersion());

		TaskContainer tasks = project.getTasks();

		tasks.withType(JavaExec.class)
			.configureEach(
					task -> task.getJavaLauncher().set(javaToolchainService.launcherFor(extension.getToolchain())));
	}

	private void disableCachingUnstableDependencies(Project project) {
		project.getConfigurations()
			.configureEach(configuration -> configuration.resolutionStrategy(resolutionStrategy -> {
				resolutionStrategy.cacheDynamicVersionsFor(0, TimeUnit.SECONDS);
				resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS);
			}));
	}

}
