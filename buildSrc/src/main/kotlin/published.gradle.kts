pluginManager.apply("publishing-conventions")

val isJavaProject = pluginManager.hasPlugin("java")
val isJavaPlatform  = pluginManager.hasPlugin("java-platform")

pluginManager.withPlugin("maven-publish") {
	configure<PublishingExtension> {
		publications {
			named<MavenPublication>("maven") {
				if (isJavaProject) {
					components.matching { it.name == "java" }.configureEach { from(this) }
				}
				if (isJavaPlatform) {
					components.matching { it.name == "javaPlatform"}.configureEach { from(this) }
				}
			}
		}
	}
}
