package de.chasenet.foxhole.utils

fun String.capitalized() = this.replaceFirstChar { it.titlecase() }
