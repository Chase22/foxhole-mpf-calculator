import de.chasenet.foxhole.model.ItemCategory
import de.chasenet.foxhole.model.Resource
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import kotlin.reflect.KProperty

class DatasetDelegate<T>(
    private val mapper: (String) -> T = { it.unsafeCast<T>() },
) {
    operator fun getValue(
        thisRef: HTMLElement,
        property: KProperty<*>,
    ): T? = thisRef.dataset[property.name]?.let { mapper(it) }
}

val HTMLElement.category: ItemCategory? by DatasetDelegate {
    try {
        ItemCategory.valueOf(it)
    } catch (e: IllegalArgumentException) {
        console.error("Unknown category: $it")
        return@DatasetDelegate null
    }
}
val HTMLElement.faction: List<String>? by DatasetDelegate { it.split(",").map(String::trim) }
val HTMLElement.resource: Resource? by DatasetDelegate {
    try {
        Resource.valueOf(it)
    } catch (e: IllegalArgumentException) {
        console.error("Unknown resource: $it")
        return@DatasetDelegate null
    }
}
