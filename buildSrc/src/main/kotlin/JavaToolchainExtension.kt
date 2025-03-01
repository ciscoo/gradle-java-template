import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.jvm.toolchain.JavaLanguageVersion
import javax.inject.Inject

abstract class JavaToolchainExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        companion object {
            private const val DEFAULT_TARGET_VERSION = "21"
            private const val DEFAULT_RELEASE_VERSION = "17"
        }

        /**
         * Java language version to use for [org.gradle.api.plugins.JavaPluginExtension.getToolchain]. The
         * language version will also be used for test compilation and
         * [org.gradle.api.tasks.JavaExec] task types.
         *
         * The convention is Java 21.
         */
        val targetVersion: Property<JavaLanguageVersion> =
            objects
                .property(JavaLanguageVersion::class.java)
                .convention(JavaLanguageVersion.of(DEFAULT_TARGET_VERSION))

        /**
         * Java language version to use for compiling artifacts for public consumption. If
         * releasing artifacts that support older versions of Java, then this should be
         * configured to the minimum supported version.
         *
         * The convention is Java 17.
         */
        val releaseVersion: Property<JavaLanguageVersion> =
            objects
                .property(JavaLanguageVersion::class.java)
                .convention(JavaLanguageVersion.of(DEFAULT_RELEASE_VERSION))
    }
