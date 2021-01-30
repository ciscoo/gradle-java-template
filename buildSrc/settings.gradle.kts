pluginManagement {
    val rootProperties = java.util.Properties().apply {
        file("../gradle.properties").inputStream().use { load(it) }
    }
    plugins {
        id("com.diffplug.spotless") version rootProperties["spotless.version"] as String
    }
}
