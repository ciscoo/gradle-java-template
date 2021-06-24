plugins {
    id("java-gradle-plugin")
    id("com.diffplug.spotless")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val rootProperties = `java.util`.Properties().apply {
    load(file("../gradle.properties").inputStream())
}

spotless {
    java {
        eclipse().configFile(file("../src/eclipse/eclipse-formatter-settings.xml"))
    }
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:${rootProperties["spotless.version"]}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.0")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.5.0")
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
