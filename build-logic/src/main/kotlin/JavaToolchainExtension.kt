import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmImplementation
import javax.inject.Inject

abstract class JavaToolchainExtension
    @Inject
    constructor(
        providers: ProviderFactory,
    ) {
        companion object {
            const val DEFAULT_TARGET_VERSION = 25
            private const val DEFAULT_RELEASE_VERSION = 17
        }

        init {
            providers
                .gradleProperty("javaToolchain.target")
                .map { it.toInt() }
                .orElse(DEFAULT_TARGET_VERSION)
                .map { JavaLanguageVersion.of(it) }
                .let { targetVersion.set(it) }

            providers
                .gradleProperty("javaToolchain.release")
                .map { it.toInt() }
                .orElse(DEFAULT_RELEASE_VERSION)
                .map { JavaLanguageVersion.of(it) }
                .let { releaseVersion.set(it) }

            providers
                .gradleProperty("javaToolchain.implementation")
                .let {
                    if (it.isPresent) {
                        when (it.get()) {
                            "j9" -> implementation.set(JvmImplementation.J9)
                            else -> throw IllegalArgumentException("Unsupported JDK implementation: ${it.get()}")
                        }
                    } else {
                        implementation.set(JvmImplementation.VENDOR_SPECIFIC)
                    }
                }
        }

        /**
         * Java language version to use for [JavaPluginExtension.getToolchain]. The
         * language version will also be used for test compilation and
         * [org.gradle.api.tasks.JavaExec] task types.
         *
         * The convention is Java [DEFAULT_TARGET_VERSION].
         */
        abstract val targetVersion: Property<JavaLanguageVersion>

        /**
         * Java language version to use for compiling artifacts for public consumption. If
         * releasing artifacts that support older versions of Java, then this should be
         * configured to the minimum supported version.
         *
         * The convention is Java [DEFAULT_RELEASE_VERSION].
         */
        abstract val releaseVersion: Property<JavaLanguageVersion>

        /**
         * Java Virtual Machine implementation to use for the toolchain.
         */
        abstract val implementation: Property<JvmImplementation>
    }
