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
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.plugins.JavaPlugin;

/**
 * Dependency management conventions without forcing dependency constraints on consumers.
 */
public abstract class DependencyManagementConventions implements Plugin<Project> {

    /**
     * The name of the configuration use to declare dependencies internal of a project.
     */
    public static final String DEPENDENCY_MANAGEMENT_CONFIGURATION_NAME = "dependencyManagement";

    @Override
    public void apply(Project project) {
        ConfigurationContainer configurations = project.getConfigurations();
        Configuration dependencyManagement =
                configurations.create(DEPENDENCY_MANAGEMENT_CONFIGURATION_NAME, configuration -> {
                    configuration.setVisible(false);
                    configuration.setCanBeConsumed(false);
                    configuration.setCanBeResolved(false);
                });
        configurations
                .matching(configuration -> configuration.getName().endsWith("Classpath")
                        || JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME.equals(configuration.getName()))
                .configureEach(configuration -> configuration.extendsFrom(dependencyManagement));
    }
}
