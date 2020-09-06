configurations {
	val internal by registering {
		isVisible = false
		isCanBeConsumed = false
		isCanBeResolved = false
	}
	matching { name.endsWith("Classpath") }.configureEach {
		extendsFrom(internal.get())
	}
	matching { name == JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME }.configureEach {
		extendsFrom(internal.get())
	}
}
