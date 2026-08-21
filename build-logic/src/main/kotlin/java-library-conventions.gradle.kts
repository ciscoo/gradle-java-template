plugins {
    `java-library`
    id("java-conventions")
    id("checkstyle-conventions")
    id("jacoco-conventions")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.checkstyleMain {
    config = resources.text.fromFile(checkstyle.configDirectory.file("checkstyleMain.xml"))
}
