package de.chasenet.foxhole.model

import de.chasenet.foxhole.utils.SafeSerializer
import kotlinx.serialization.Serializable

@Suppress("ktlint:standard:enum-entry-name-case")
@Serializable
enum class ItemCategory {
    small_arms,
    heavy_arms,
    heavy_ammunition,
    supplies,
    uniforms,
    vehicles,
    shipables,
}

object ItemCategorySerializer : SafeSerializer<ItemCategory>(ItemCategory.serializer())
