pluginManager.apply("java-base-conventions")
pluginManager.withPlugin("java-library") {
	configure<JavaPluginExtension> {
		withJavadocJar()
		withSourcesJar()
	}
	// https://docs.oracle.com/en/java/javase/14/docs/specs/man/javac.html
	tasks {
		named<JavaCompile>(JavaPlugin.COMPILE_JAVA_TASK_NAME) {
			options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
		}
		named<JavaCompile>(JavaPlugin.COMPILE_TEST_JAVA_TASK_NAME) {
			options.compilerArgs.addAll(listOf("-Xlint", "-Xlint:-overrides", "-Werror", "-parameters"))
		}
	}
}

pluginManager.withPlugin("java-test-fixtures") {
	components.named<AdhocComponentWithVariants>("java") {
		listOf("testFixturesApiElements", "testFixturesRuntimeElements").forEach {
			withVariantsFromConfiguration(configurations.getByName(it)) { skip() }
		}
	}
}
