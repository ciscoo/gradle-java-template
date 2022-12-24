// https://youtrack.jetbrains.com/issue/KTIJ-19369.
@Suppress(
    "DSL_SCOPE_VIOLATION",
    "MISSING_DEPENDENCY_CLASS",
    "UNRESOLVED_REFERENCE_WRONG_RECEIVER",
    "FUNCTION_CALL_EXPECTED"
)
plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.springJavaFormat)
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
    implementation(libs.gradle.springJavaFormat)
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

tasks {
    checkFormat {
        dependsOn(spotlessCheck)
    }

}
