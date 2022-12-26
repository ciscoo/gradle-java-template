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
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.FeaturePreviews;

public abstract class JavaLibrarySettingsConventions implements Plugin<Settings> {

	@Override
	public void apply(Settings settings) {
		enableAllActiveFeatures(settings);
		configureDefaultRepositories(settings);
	}

	private void configureDefaultRepositories(Settings settings) {
		settings.dependencyResolutionManagement(dependencyResolutionManagement -> dependencyResolutionManagement
				.repositories(RepositoryHandler::mavenCentral));
	}

	private void enableAllActiveFeatures(Settings settings) {
		for (FeaturePreviews.Feature feature : FeaturePreviews.Feature.values()) {
			if (feature.isActive()) {
				settings.enableFeaturePreview(feature.name());
			}
		}
	}

}
