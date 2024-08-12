plugins {
    `java-library`
    id("io.mateo.build.java-conventions")
    id("io.mateo.build.jacoco-conventions")
}

java {
    withSourcesJar()
    withJavadocJar()
}
