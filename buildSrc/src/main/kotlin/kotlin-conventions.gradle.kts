import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin
}

tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = Versions.jvmReleaseTarget.toString()
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
    compileKotlin {
        kotlinOptions {
            apiVersion = "1.3"
            languageVersion = "1.3"
            allWarningsAsErrors = true
        }
    }
}
