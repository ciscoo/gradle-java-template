plugins {
    id("io.mateo.build.java-library-conventions")
    id("io.mateo.build.maven-publishing-conventions")
}

description = "Example API"

dependencies {
    api(libs.jspecify)
}
