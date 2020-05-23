package io.mateo.gradle.build;

import org.apache.commons.lang3.SystemUtils;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.CopySpec;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.resources.TextResource;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.testing.Test;
import org.gradle.external.javadoc.JavadocMemberLevel;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.gradle.testretry.TestRetryPlugin;
import org.gradle.testretry.TestRetryTaskExtension;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Objects.requireNonNull;

public class JavaBaseConventionsPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getPluginManager().withPlugin("java-base", (plugin) -> {
			configureJavaExtension(project);
			configureJavaCompileConventions(project);
			configureJavadocConventions(project);
			configureTestConventions(project);
			configureJarManifestConventions(project);
		});
	}

	private void configureJarManifestConventions(Project project) {
		project.afterEvaluate((evaluated) -> {
			evaluated.getTasks().withType(Jar.class).configureEach((jar) -> {
				jar.metaInf((metaInf) -> copyLegalFiles(evaluated, metaInf));
				jar.manifest((manifest) -> {
					Map<String, Object> attributes = new TreeMap<>();
					ExtraPropertiesExtension extra = evaluated.getExtensions().getExtraProperties();
					attributes.put("Automatic-Module-Name", evaluated.getName().replace("-", "."));
					attributes.put("Build-Date", extra.get("buildDate"));
					attributes.put("Build-Time", extra.get("buildTime"));
					attributes.put("Created-By", String.format("%s (%s %s)", SystemUtils.JAVA_VERSION,
							SystemUtils.JAVA_VENDOR, SystemUtils.JAVA_VM_VERSION));
					attributes.put("Implementation-Title", evaluated.getDescription());
					attributes.put("Implementation-Version", evaluated.getVersion().toString());
					attributes.put("Specification-Title", evaluated.getDescription());
					attributes.put("Specification-Version", evaluated.getVersion().toString());
					manifest.attributes(attributes);
				});
			});
		});
	}

	private void configureTestConventions(Project project) {
		project.getTasks().withType(Test.class).configureEach((test) -> {
			test.useJUnitPlatform();
			test.setMaxHeapSize("1024M");
		});
		if (Boolean.parseBoolean(System.getenv("CI"))) {
			project.getPluginManager().apply(TestRetryPlugin.class);
			project.getTasks().withType(Test.class)
					.configureEach((test) -> project.getPlugins().withType(TestRetryPlugin.class, (testRetryPlugin) -> {
						test.getExtensions().configure(TestRetryTaskExtension.class, (testRetry) -> {
							testRetry.getFailOnPassedAfterRetry().set(true);
							testRetry.getMaxRetries().set(3);
						});
					}));
		}
	}

	private void configureJavadocConventions(Project project) {
		// https://docs.oracle.com/en/java/javase/14/docs/specs/man/javadoc.html
		project.getTasks().withType(Javadoc.class).configureEach((javadoc) -> {
			javadoc.options((options) -> {
				options.setMemberLevel(JavadocMemberLevel.PROTECTED);
				options.setHeader(project.getName());
				options.setEncoding("UTF-8");
				options.setLocale("en");
				StandardJavadocDocletOptions docletOptions = (StandardJavadocDocletOptions) options;
				docletOptions.addBooleanOption("Xdoclint:html,syntax", true);
				docletOptions.addBooleanOption("html5", true);
				docletOptions.use();
				docletOptions.noTimestamp();
			});
		});
	}

	private void configureJavaCompileConventions(Project project) {
		project.getTasks().withType(JavaCompile.class).configureEach((compile) -> {
			compile.getOptions().setEncoding("UTF-8");
			// Useful for Spring libraries
			List<String> compilerArgs = compile.getOptions().getCompilerArgs();
			if (!compilerArgs.contains("-parameters")) {
				compilerArgs.add("-parameters");
			}
		});
	}

	private void configureJavaExtension(Project project) {
		project.getExtensions().configure(JavaPluginExtension.class, (java) -> {
			java.setSourceCompatibility(Versions.jvmTarget);
		});
	}

	private void copyLegalFiles(Project project, CopySpec metaInf) {
		copyLegalFile(project, metaInf, "NOTICE.txt");
		copyLegalFile(project, metaInf, "LICENSE.txt");
	}

	private void copyLegalFile(Project project, CopySpec metaInf, String filename) {
		URL url = requireNonNull(getClass().getClassLoader().getResource(filename),
				"Legal file does not exist: " + filename);
		TextResource textResource = createLegalTextResource(project, url, filename);
		metaInf.from(createLegalFile(textResource.asFile(), filename));
	}

	private TextResource createLegalTextResource(Project project, URL url, String filename) {
		TextResource textResource;
		try {
			textResource = project.getResources().getText().fromUri(url.toURI());
		} catch (URISyntaxException ex) {
			throw new GradleException("Failed to copy " + filename, ex);
		}
		return textResource;
	}

	@SuppressWarnings("ResultOfMethodCallIgnored") // file rename
	private File createLegalFile(File source, String filename) {
		File legalFile = new File(source.getParentFile(), filename);
		source.renameTo(legalFile);
		return legalFile;
	}
}
