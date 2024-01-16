# Gradle Java Template

Template for building Java projects or Java libraries.

This project is primarily for personal projects, but others may find it useful.

## Conventions

The majority of the build logic is contained within an included build `build-logic` within `./gradle/build-logic`.
Many of conventions are based on the conventions from JUnit, Spring Boot, Spring Framework, Micronaut, and personal
opinions gathered from open source and professional work.

## Getting Started

Clone or download a zip and update the following:

* Root project name in `settings.gradle.kts`
* Project description each project Gradle file
* POM configuration in `io.mateo.build.maven-publishing-conventions.gradle.kts`
