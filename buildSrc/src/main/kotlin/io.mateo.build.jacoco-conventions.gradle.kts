plugins {
    id("java") apply false
    jacoco
}

val libs = versionCatalogs.named("libs")

jacoco {
    libs.findVersion("jacoco").ifPresent {
        toolVersion = it.requiredVersion
    }
}

pluginManager.withPlugin("java") {
    tasks {
        test {
            finalizedBy(jacocoTestReport)
        }
        jacocoTestReport {
            dependsOn(test)
        }
        jacocoTestCoverageVerification {
            violationRules.rule {
                limit {
                    minimum = "0.9".toBigDecimal()
                }
            }
        }
    }
}
