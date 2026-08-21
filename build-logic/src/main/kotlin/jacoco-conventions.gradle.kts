plugins {
    jacoco
}

val libs = versionCatalogs.named("libs")

jacoco {
    toolVersion =
        libs
            .findLibrary("jacoco")
            .orElseThrow()
            .get()
            .version!!
}

pluginManager.withPlugin("java") {
    tasks {
        val jacocoTestReport =
            named<JacocoReport>("jacocoTestReport") {
                dependsOn(named(JvmTestSuitePlugin.DEFAULT_TEST_SUITE_NAME))
            }
        named<Test>(JvmTestSuitePlugin.DEFAULT_TEST_SUITE_NAME) {
            finalizedBy(jacocoTestReport)
        }
        named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
            violationRules.rule {
                limit {
                    minimum = "0.9".toBigDecimal()
                }
            }
        }
    }
}
