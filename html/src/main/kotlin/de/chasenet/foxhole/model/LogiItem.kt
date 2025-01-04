package de.chasenet.foxhole.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
enum class ItemCategory {
    small_arms,
    heavy_arms,
    heavy_ammunition,
    supplies,
    uniforms,
    vehicles,
    shipables
}

object ItemCategorySerializer : SafeSerializer<ItemCategory>(ItemCategory.serializer())


open class SafeSerializer<T>(
    private val serializer: KSerializer<T>
) : KSerializer<T?> {
    override val descriptor = serializer.descriptor

    // safe because @Serializable skips null fields
    override fun serialize(encoder: Encoder, value: T?) = encoder.encodeSerializableValue(serializer, value!!)

    override fun deserialize(decoder: Decoder): T? = try {
        decoder.decodeSerializableValue(serializer)
    } catch (_: Exception) {
        null
    }
}

@Serializable
data class LogiItem(
    val displayId: Int,
    val faction: List<String>,
    val imgName: String,
    val itemName: String,
    val itemDesc: String,
    @Serializable(with = ItemCategorySerializer::class) val itemCategory: ItemCategory?,
    val itemClass: String? = null,
    val ammoUsed: String? = null,
    val numberProduced: Int,
    val isTeched: Boolean,
    val isMpfCraftable: Boolean,
    val craftLocation: List<String>,
    val cost: Cost,
)

val resources = arrayOf("bmat", "rmat", "emat", "hemat")

@Serializable
data class Cost(
    val bmat: Int = 0,
    val rmat: Int = 0,
    val emat: Int = 0,
    val hemat: Int = 0
)
