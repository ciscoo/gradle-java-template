plugins {
    id("java-conventions")
    `java-library`
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    // https://docs.oracle.com/en/java/javase/15/docs/specs/man/javac.html
    compileJava {
        options.compilerArgs.addAll(listOf(
            "-Xlint:all", // Enable all warnings.
            "-Werror" // Terminates compilation when warnings occur.
        ))
    }
    compileTestJava {
        options.compilerArgs.addAll(listOf(
            "-Xlint", // Enable all recommended warnings.
            "-Xlint:-overrides", // Warns about issues related to method overrides.
            "-Werror",
            "-parameters" // Generates metadata for reflection on method parameters.
        ))
    }
}
