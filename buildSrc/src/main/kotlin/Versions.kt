import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import kotlin.reflect.KProperty

class Versions(private val project: Project) {

	private val properties = object {
		operator fun getValue(receiver: Any?, property: KProperty<*>) = get(property.name)
	}

	operator fun get(name: String) = project.extra.get("$name.version") as String

	companion object {
		val jvmTarget = JavaVersion.VERSION_15
		val jvmReleaseTarget = JavaVersion.VERSION_1_8
	}

}
