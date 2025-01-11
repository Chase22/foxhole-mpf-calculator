package de.chasenet.foxhole.html.utils

import kotlinx.html.HTMLTag
import kotlinx.html.HtmlTagMarker
import kotlinx.html.TagConsumer
import kotlinx.html.visit

class BoxIcon(
    name: String,
    consumer: TagConsumer<*>,
) : HTMLTag("box-icon", consumer, mapOf("name" to name), inlineTag = true, emptyTag = false) {
    var name: String? by AttributeDelegate()
    var color: String? by AttributeDelegate()
    var type: Type? by enumAttributeDelegate()
    var size: Size? by enumAttributeDelegate()
    var rotate: Rotate? by enumAttributeDelegate()
    var flip: Flip? by enumAttributeDelegate()
    var border: Border? by enumAttributeDelegate()
}

@HtmlTagMarker
fun HTMLTag.boxIcon(
    name: String,
    block: BoxIcon.() -> Unit = {},
) {
    BoxIcon(name, consumer).visit(block)
}

enum class Type {
    regular,
    solid,
    logo,
}

enum class Size {
    xs,
    sm,
    md,
    lg,
    cssSize,
}

enum class Rotate(
    value: Int,
) {
    rotate_90(90),
    rotate_180(180),
    rotate_270(270),
}

enum class Flip {
    horizontal,
    vertical,
}

enum class Border {
    square,
    circle,
}
