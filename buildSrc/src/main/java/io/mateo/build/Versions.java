package io.mateo.build;

import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;

public class Versions {

	public static final JavaVersion JAVA_VERSION_TARGET = JavaVersion.VERSION_16;

	public static final JavaVersion JAVA_VERSION_RELEASE = JavaVersion.VERSION_1_8;

	public final Project project;

	public Versions(Project project) {
		this.project = project;
	}

	public String springBoot() {
		return this.get("springBoot.version");
	}

	public String springCloud() {
		return this.get("springCloud.version");
	}

	private String get(String propertyName) {
		Object property = this.project.property("springBoot.version");
		if (property == null) {
			throw new GradleException("Version property does not exist: " + propertyName);
		}
		return property.toString();
	}

}
