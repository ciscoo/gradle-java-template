plugins {
    `kotlin-dsl`
    alias(libs.plugins.build.spotless)
}

dependencies {
    implementation(libs.gradle.foojarResolverConvention)
}

spotless {
    kotlin {
        endWithNewline()
        trimTrailingWhitespace()
        targetExclude("**/build/**")
        ktlint(libs.versions.ktlint.get())
    }
}
