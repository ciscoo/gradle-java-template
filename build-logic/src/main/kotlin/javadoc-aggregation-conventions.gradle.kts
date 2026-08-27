val javadoc =
    configurations.dependencyScope("javadoc") {
        description = "Dependencies for Javadoc aggregation."
    }

val javadocSources =
    configurations.resolvable("javadocSources") {
        description = "Java sources for aggregated Javadoc generation."
        isTransitive = false
        extendsFrom(javadoc)
        attributes {
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.VERIFICATION))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType.SOURCES))
            attribute(VerificationType.VERIFICATION_TYPE_ATTRIBUTE, objects.named(VerificationType.MAIN_SOURCES))
        }
    }

val javadocClasspath =
    configurations.resolvable("javadocClasspath") {
        description = "Classpath for aggregated Javadoc generation to resolve type references in the source code."
        extendsFrom(javadoc)
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        }
    }

val javadocAggregate =
    tasks.register<Javadoc>("javadocAggregate") {
        description = "Generates aggregated Javadoc API documentation."
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        classpath = javadocClasspath.get()
        destinationDirectory = layout.buildDirectory.dir("javadoc-aggregate")
        setSource(javadocSources)
        include("**/*.java")
        options {
            this as StandardJavadocDocletOptions
            docTitle = "Gradle Java Template $version API"
            windowTitle = docTitle
            memberLevel = JavadocMemberLevel.PROTECTED
            outputLevel = JavadocOutputLevel.QUIET
            author()
            splitIndex()
            use()
        }
    }

pluginManager.withPlugin("lifecycle-base") {
    tasks.named<Delete>(LifecycleBasePlugin.CLEAN_TASK_NAME) {
        delete(javadocAggregate)
    }
}
