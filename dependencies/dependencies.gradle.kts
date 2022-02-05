plugins {
    id("java-platform")
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform(libs.springBootBom))
    api(platform(libs.springCloudBom))
}
