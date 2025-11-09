plugins {
    id("io.mateo.build.java-library-conventions")
    id("io.mateo.build.publishing-conventions")
}

description = "Example API"

dependencies {
    api(libs.jspecify)
}
