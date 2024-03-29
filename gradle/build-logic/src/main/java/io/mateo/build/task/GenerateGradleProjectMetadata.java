/*
 * Copyright 2024 the original author or authors.
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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.util.GradleVersion;

/**
 * Task to generate project metadata for use with documentation sources.
 */
public abstract class GenerateGradleProjectMetadata extends DefaultTask {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    public GenerateGradleProjectMetadata(ProjectLayout layout, ProviderFactory providers) {
        setDescription("Generate project metadata to use in the VitePress build.");
        getGeneratedMetadata().set(layout.getBuildDirectory().file("gradle-project-metadata.json"));
        getProperties().put("version", providers.provider(() -> getProject()
                .getVersion()
                .toString()));
        getProperties().put("gradleVersion", GradleVersion.current().getVersion());
    }

    @OutputFile
    public abstract RegularFileProperty getGeneratedMetadata();

    @Input
    public abstract MapProperty<String, Object> getProperties();

    @TaskAction
    public void writeMetadata() throws IOException {
        Path path = this.getGeneratedMetadata().get().getAsFile().toPath().toAbsolutePath();
        MAPPER.writerWithDefaultPrettyPrinter()
                .writeValue(Files.newBufferedWriter(path), this.getProperties().get());
    }
}
