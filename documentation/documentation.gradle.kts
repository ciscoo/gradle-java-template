import org.asciidoctor.gradle.base.AsciidoctorAttributeProvider
import org.asciidoctor.gradle.jvm.AbstractAsciidoctorTask

plugins {
    id("io.mateo.build.java-conventions")
    alias(libs.plugins.asciidoctor.jvmConvert)
    alias(libs.plugins.asciidoctor.jvmPdf)
    alias(libs.plugins.asciidoctor.jvmEpub)
    alias(libs.plugins.plantuml)
    alias(libs.plugins.spotless)
}

description = "Gradle Java Template Documentation"

asciidoctorj {
    setJrubyVersion(libs.jruby.complete.map { it.version as String })
    modules {
        pdf.version(libs.asciidoctorj.pdf.map { it.version as String })
    }
}

tasks {
    plantUml {
        fileFormat = "SVG"
    }
    withType<AbstractAsciidoctorTask>().configureEach {
        baseDirFollowsSourceDir()
        attributeProviders.add(
            AsciidoctorAttributeProvider {
                mapOf(
                    "revnumber" to version,
                    "current-gradle-version" to GradleVersion.current().version,
                )
            },
        )
        resources {
            from(plantUml) {
                into("user-guide/images")
            }
        }
    }
    asciidoctorPdf {
        sources {
            include("index.adoc")
        }
    }
    jar {
        enabled = false
    }
    javadoc {
        enabled = false
    }
}
