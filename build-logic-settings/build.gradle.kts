plugins {
    `kotlin-dsl`
    alias(libs.plugins.build.spotless)
}

dependencies {
    implementation(libs.foojayResolverConvention)
    implementation(libs.nmcpSettings)
}

spotless {
    kotlinGradle {
//        ktlint(libs.versions.ktlint.get())
        target("**/*.settings.gradle.kts")
        target("**/*.gradle.kts")
    }
}
