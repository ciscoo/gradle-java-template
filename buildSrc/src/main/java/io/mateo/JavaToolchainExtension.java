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
package io.mateo;

import javax.inject.Inject;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Property;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

public abstract class JavaToolchainExtension {

    private static final String DEFAULT_TARGET_VERSION = "21";

    private static final String DEFAULT_RELEASE_VERSION = "17";

    private final Property<JavaLanguageVersion> targetVersion;

    private final Property<JavaLanguageVersion> releaseVersion;

    @Inject
    public JavaToolchainExtension(ObjectFactory objects) {
        this.targetVersion =
                objects.property(JavaLanguageVersion.class).convention(JavaLanguageVersion.of(DEFAULT_TARGET_VERSION));
        this.releaseVersion =
                objects.property(JavaLanguageVersion.class).convention(JavaLanguageVersion.of(DEFAULT_RELEASE_VERSION));
    }

    /**
     * Java language version to use for {@link JavaPluginExtension#getToolchain}. The
     * language version will also be used for test compilation and
     * {@link org.gradle.api.tasks.JavaExec} task types.
     *
     * The convention is Java {@value DEFAULT_TARGET_VERSION}.
     */
    public Property<JavaLanguageVersion> getTargetVersion() {
        return this.targetVersion;
    }

    /**
     * Java language version to use for compiling artifacts for public consumption. If
     * releasing artifacts that support older versions of Java, then this should be
     * configured to the minimum supported version.
     *
     * The convention is Java {@value DEFAULT_RELEASE_VERSION}.
     */
    public Property<JavaLanguageVersion> getReleaseVersion() {
        return this.releaseVersion;
    }
}
