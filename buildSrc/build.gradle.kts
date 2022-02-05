plugins {
    id("java-gradle-plugin")
    id("io.spring.javaformat")
    alias(libs.plugins.spotless)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradle.spotless)
    implementation(libs.gradle.springFormat)
}

spotless {
    java {
        licenseHeaderFile(file("../src/spotless/apache-license-2.0.java"))
    }
}

tasks {
    checkFormat {
        dependsOn(spotlessCheck)
    }
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
