plugins {
    id("java-library-conventions")
}

description = "Example project"

dependencies {
    api(projects.exampleApi)
    api(libs.jspecify)
}
