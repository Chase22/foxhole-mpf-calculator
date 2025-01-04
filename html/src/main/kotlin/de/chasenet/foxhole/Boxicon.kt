package de.chasenet.foxhole

import kotlinx.html.HTMLTag
import kotlinx.html.HtmlTagMarker
import kotlinx.html.TagConsumer
import kotlinx.html.visit
import kotlin.reflect.KProperty

class AttributeDelegate<T: Any?>(private val deserializer: (String) -> T = { String as T }) {
    operator fun getValue(thisRef: HTMLTag, property: KProperty<*>): T? {
        return thisRef.attributes[property.name]?.let { deserializer(it) }
    }

    operator fun setValue(thisRef: HTMLTag, property: KProperty<*>, value: T?) {
        if (value == null) {
            thisRef.attributes.remove(property.name)
        } else {
            thisRef.attributes[property.name] = value.toString()
        }
    }
}


class BoxIcon(
    name: String,
    consumer: TagConsumer<*>
) : HTMLTag("box-icon", consumer, mapOf("name" to name), inlineTag = true, emptyTag = false) {
    var name: String? by AttributeDelegate()
    var color: String? by AttributeDelegate()
    var type: Type? by AttributeDelegate { Type.valueOf(it) }
    var size: Size? by AttributeDelegate { Size.valueOf(it) }
    var rotate: Rotate? by AttributeDelegate { Rotate.valueOf(it) }
    var flip: Flip? by AttributeDelegate { Flip.valueOf(it) }
    var border: Border? by AttributeDelegate { Border.valueOf(it) }
}

@HtmlTagMarker
fun HTMLTag.boxIcon(name: String, block: BoxIcon.() -> Unit = {}) {
    BoxIcon(name, consumer).visit(block)
}

enum class Type {
    regular, solid, logo
}

enum class Size {
    xs, sm, md, lg, cssSize
}

enum class Rotate(value: Int) {
    rotate_90(90), rotate_180(180), rotate_270(270)
}

enum class Flip {
    horizontal, vertical
}

enum class Border {
    square, circle
}