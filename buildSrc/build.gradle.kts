plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.spotless)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradle.spotless)
}

gradlePlugin {
    plugins {
        register("codeStyleConventions") {
            id = "io.mateo.build.code-style-conventions"
            implementationClass = "io.mateo.build.CodeStyleConventions"
        }
        register("javaConventions") {
            id = "io.mateo.build.java-conventions"
            implementationClass = "io.mateo.build.JavaConventions"
        }
        register("javaLibraryConventions") {
            id = "io.mateo.build.java-library-conventions"
            implementationClass = "io.mateo.build.JavaLibraryConventions"
        }
        register("kotlinConventions") {
            id = "io.mateo.build.KotlinConventions"
            implementationClass = "io.mateo.build.KotlinConventions"
        }
        register("mavenPublishingConventions") {
            id = "io.mateo.build.maven-publishing-conventions"
            implementationClass = "io.mateo.build.MavenPublishingConventions"
        }
    }
}
