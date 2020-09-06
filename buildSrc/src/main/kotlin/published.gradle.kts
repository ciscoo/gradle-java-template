pluginManager.apply("publishing-conventions")

val isJavaProject = pluginManager.hasPlugin("java")
val isJavaPlatform  = pluginManager.hasPlugin("java-platform")

pluginManager.withPlugin("maven-publish") {
	configure<PublishingExtension> {
		publications {
			named<MavenPublication>("maven") {
				if (isJavaProject) {
					components.matching { name == "java" }.configureEach { from(this) }
				}
				if (isJavaPlatform) {
					components.matching { name == "javaPlatform"}.configureEach { from(this) }
				}
			}
		}
	}
}
