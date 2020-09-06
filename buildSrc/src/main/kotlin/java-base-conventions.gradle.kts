import org.apache.commons.lang3.SystemUtils
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.testretry.TestRetryTaskExtension
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.Locale

pluginManager.withPlugin("java-base") {
	configure<JavaPluginExtension> {
		if (Versions.jvmTarget.isJava9Compatible) {
			sourceCompatibility = Versions.jvmTarget
		}
	}
	tasks {
		withType<JavaCompile>().configureEach {
			with(options) {
				encoding = StandardCharsets.UTF_8.toString()
				if (Versions.jvmTarget.isJava9Compatible) {
					release.set(Integer.valueOf(Versions.jvmTarget.majorVersion))
				}
				if (!compilerArgs.contains("-parameters")) {
					compilerArgs.add("-parameters")
				}
			}
		}
		// https://docs.oracle.com/en/java/javase/14/docs/specs/man/javadoc.html
		withType<Javadoc>().configureEach {
			options {
				memberLevel = JavadocMemberLevel.PROTECTED
				header = project.name
				encoding = StandardCharsets.UTF_8.toString()
				locale = Locale.ENGLISH.language
				this as StandardJavadocDocletOptions
				addBooleanOption("Xdoclint:html,syntax", true)
				addBooleanOption("html5", true)
				use()
				noTimestamp()
			}
		}
		withType<Test>().configureEach {
			useJUnitPlatform()
			testLogging {
				events = setOf(
						TestLogEvent.STANDARD_ERROR,
						TestLogEvent.FAILED,
						TestLogEvent.SKIPPED
				)
			}

		}
		if (java.lang.Boolean.parseBoolean(System.getenv("CI"))) {
			project.pluginManager.apply("org.gradle.test-retry")
			withType<Test>().configureEach {
				extensions.configure<TestRetryTaskExtension> {
					failOnPassedAfterRetry.set(true)
					maxRetries.set(3)
				}
			}
		}
		val copyLegalFiles = register<Copy>("copyLegalFiles") {
			from(Path.of(rootProject.file("buildSrc/src/main/resources").toURI()))
			include("NOTICE.txt", "LICENSE.txt")
			into(project.layout.buildDirectory.dir("legal"))
		}
		withType<Jar>().configureEach {
			metaInf.from(copyLegalFiles)
			manifest {
				attributes["Automatic-Module-Name"] = project.name.replace("-", ".")
				attributes["Created-By"] = "${SystemUtils.JAVA_VERSION} (${SystemUtils.JAVA_VENDOR} ${SystemUtils.JAVA_VM_VERSION})"
				attributes["Implementation-Title"] = project.description
				attributes["Implementation-Version"] = project.version.toString()
				attributes["Specification-Title"] = project.description
				attributes["Specification-Version"] = project.version.toString()
			}
		}
	}
}
