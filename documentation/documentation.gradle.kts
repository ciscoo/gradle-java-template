plugins {
    id("io.mateo.build.java-conventions")
    alias(libs.plugins.spotless)
}

description = "Gradle Java Template Documentation"

tasks {
    jar {
        enabled = false
    }
    javadoc {
        enabled = false
    }
}
