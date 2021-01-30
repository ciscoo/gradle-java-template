configurations {
	val internal by registering {
		isVisible = false
		isCanBeConsumed = false
		isCanBeResolved = false
	}
	matching { it.name.endsWith("Classpath") }.configureEach {
		extendsFrom(internal.get())
	}
	matching { it.name == JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME }.configureEach {
		extendsFrom(internal.get())
	}
}
