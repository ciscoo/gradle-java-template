plugins {
    id("io.mateo.build.java-conventions")
    alias(libs.plugins.asciidoctor.jvmConvert)
    alias(libs.plugins.asciidoctor.jvmPdf)
    alias(libs.plugins.asciidoctor.jvmEpub)
    alias(libs.plugins.spotless)
}

description = "Gradle Java Template Documentation"

tasks {
    jar {
        enabled = false
    }
    javadoc {
        enabled = false
    }
}
