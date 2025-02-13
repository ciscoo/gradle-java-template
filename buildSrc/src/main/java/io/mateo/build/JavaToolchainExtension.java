/*
 * Copyright 2025 the original author or authors.
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

import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Property;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

public abstract class JavaToolchainExtension {

    private static final String DEFAULT_TARGET_VERSION = "21";

    private static final String DEFAULT_RELEASE_VERSION = "17";

    public JavaToolchainExtension() {
        getTargetVersion().convention(JavaLanguageVersion.of(DEFAULT_TARGET_VERSION));
        getReleaseVersion().convention(JavaLanguageVersion.of(DEFAULT_RELEASE_VERSION));
    }

    /**
     * Java language version to use for {@link JavaPluginExtension#getToolchain()}. The
     * language version will also be used for test compilation and
     * {@link org.gradle.api.tasks.JavaExec} task types.
     * <p>
     * The convention is Java {@value DEFAULT_TARGET_VERSION}.
     */
    public abstract Property<JavaLanguageVersion> getTargetVersion();

    /**
     * Java language version to use for compiling artifacts for public consumption. If
     * releasing artifacts that support older versions of Java, then this should be
     * configured to the minimum supported version.
     * <p>
     * The convention is Java {@value DEFAULT_RELEASE_VERSION}.
     */
    public abstract Property<JavaLanguageVersion> getReleaseVersion();
}
