plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.spotless)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

spotless {
    java {
        licenseHeaderFile(layout.projectDirectory.file("src/main/resources/spotless/apache-license-2.0.java"))
    }
}

dependencies {
    implementation(platform(libs.springBootBom))
    implementation(libs.gradle.spotless)
}

gradlePlugin {
    plugins {
        register("codeStyleConventions") {
            id = "io.mateo.build.code-style-conventions"
            implementationClass = "io.mateo.build.CodeStyleConventionsPlugin"
        }
        register("dependencyManagementConventions") {
            id = "io.mateo.build.dependency-management-conventions"
            implementationClass = "io.mateo.build.DependencyManagementConventions"
        }
        register("javaConventions") {
            id = "io.mateo.build.java-conventions"
            implementationClass = "io.mateo.build.JavaConventions"
        }
        register("javaLibraryConventions") {
            id = "io.mateo.build.java-library-conventions"
            implementationClass = "io.mateo.build.JavaLibraryConventions"
        }
        register("mavenPublishingConventions") {
            id = "io.mateo.build.maven-publishing-conventions"
            implementationClass = "io.mateo.build.MavenPublishingConventions"
        }
    }
}
