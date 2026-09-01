plugins {
    id("java-library-conventions")
    id("publishing-conventions")
}

description = "Example API project"

dependencies {
    api(libs.jspecify)
}
