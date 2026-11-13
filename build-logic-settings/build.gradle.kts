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
        ktlint(libs.ktlint.get().version)
        target("**/*.settings.gradle.kts")
        target("**/*.gradle.kts")
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_26
    }
}
