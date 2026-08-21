plugins {
    `kotlin-dsl`
    alias(libs.plugins.build.spotless)
}

spotless {
    kotlinGradle {
        target("src/main/kotlin/*.gradle.kts", "*.gradle.kts")
        ktlint(libs.ktlint.get().version)
    }
}

dependencies {
    implementation(libs.spotless)
}
