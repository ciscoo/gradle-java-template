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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;

/**
 * Task to generate code snippets suitable for documentation use. Currently, VitePress does not natively support
 * multi-region code snippets; see <a href="https://github.com/vuejs/vitepress/issues/3690"> issue #3690</a>.
 */
public abstract class GenerateSnippets extends SourceTask {

    private static final Pattern startRegionPattern = Pattern.compile("#region");

    private static final Pattern endRegionPattern = Pattern.compile("#endregion");

    @Inject
    public GenerateSnippets(ProjectLayout layout) {
        Project project = getProject();
        project.getPluginManager().withPlugin("java", ignored -> {
            source(project.getExtensions()
                    .getByType(JavaPluginExtension.class)
                    .getSourceSets()
                    .named(SourceSet.MAIN_SOURCE_SET_NAME)
                    .map(SourceSet::getAllSource));
        });
        getOutputDirectory().convention(layout.getBuildDirectory().dir("processed-region-sources"));
    }

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    @TaskAction
    public void processRegions() throws IOException {
        for (File source : getSource()) {
            Path parsed = getOutputDirectory().getAsFile().get().toPath().resolve(source.getName());
            List<String> linesToPrint = new ArrayList<>();
            try (BufferedReader br = Files.newBufferedReader(source.toPath())) {
                boolean shouldPrint = false;
                String line;
                while ((line = br.readLine()) != null) {
                    if (startRegionPattern.matcher(line).find()) {
                        shouldPrint = true;
                    }
                    if (endRegionPattern.matcher(line).find()) {
                        shouldPrint = false;
                    }
                    if (shouldPrint) {
                        linesToPrint.add(line);
                    }
                }
            }
            if (linesToPrint.isEmpty()) {
                continue;
            }
            String content = linesToPrint.stream()
                    .filter(line -> !startRegionPattern.matcher(line).find())
                    .collect(Collectors.joining(System.lineSeparator()));

            Files.writeString(parsed, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}
