plugins {
    `java-library`
    id("io.mateo.build.java-conventions")
}

java {
    withSourcesJar()
    withJavadocJar()
}
