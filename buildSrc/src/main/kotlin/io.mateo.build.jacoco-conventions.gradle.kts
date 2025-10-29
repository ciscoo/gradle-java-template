plugins {
    id("java") apply false
    jacoco
}

val libs = versionCatalogs.named("libs")

jacoco {
    toolVersion = libs.findVersion("jacoco").orElseThrow().requiredVersion
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
