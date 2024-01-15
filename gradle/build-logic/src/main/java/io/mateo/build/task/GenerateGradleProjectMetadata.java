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
package io.mateo.build.task;

import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.util.GradleVersion;

/**
 * Task to generate project metadata for use with documentation sources.
 */
public abstract class GenerateGradleProjectMetadata extends DefaultTask {

    @Inject
    public GenerateGradleProjectMetadata(ProjectLayout layout, ProviderFactory providers) {
        getGeneratedMetadata().set(layout.getBuildDirectory().file("gradle-project-metadata.json"));
        getProperties().put("version", providers.provider(() -> getProject().getVersion().toString()));
        getProperties().put("gradleVersion", GradleVersion.current().getVersion());
    }

    @OutputFile
    public abstract RegularFileProperty getGeneratedMetadata();

    @Input
    public abstract MapProperty<String, Object> getProperties();

}
