package de.chasenet.foxhole

import kotlinx.html.HTMLTag
import kotlin.reflect.KProperty

open class AttributeDelegate<T: Any?>(private val attributeName: String? = null, private val deserializer: (String) -> T = {
    @Suppress("UNCHECKED_CAST")
    it as T
}) {
    operator fun getValue(thisRef: HTMLTag, property: KProperty<*>): T? {
        return thisRef.attributes[key(property)]?.let { deserializer(it) }
    }

    operator fun setValue(thisRef: HTMLTag, property: KProperty<*>, value: T?) {
        if (value == null) {
            thisRef.attributes.remove(key(property))
        } else {
            thisRef.attributes[key(property)] = value.toString()
        }
    }

    private fun key(property: KProperty<*>) = attributeName ?: property.name
}

inline fun <reified T: Enum<T>> enumAttributeDelegate(attributeName: String? = null) = AttributeDelegate<T>(attributeName, {
    enumValueOf<T>(it)
})

var HTMLTag.category: String? by AttributeDelegate<String>("data-category")
var HTMLTag.faction: String? by AttributeDelegate<String>("data-faction")
var HTMLTag.resource: String? by AttributeDelegate<String>("data-resource")