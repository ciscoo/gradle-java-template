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
package io.mateo.examples;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This file demonstrates multiple code regions.
 */
public class SampleUsage {

    public static void main(String[] args) {
        // #region
        var path = Path.of("foo");
        if (Files.isDirectory(path)) {
            System.out.println("Directory");
        }
        // #endregion
        // lines should not exist
        var fileSystem = path.getFileSystem();
        // #region
        if (Files.isRegularFile(path)) {
            System.out.println("File");
        }
        // #endregion
    }
}
