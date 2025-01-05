package de.chasenet.foxhole.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val logiItemJson =
    Json {
        ignoreUnknownKeys = true
    }

fun deserializeLogiItems(json: String) = logiItemJson.decodeFromString<List<LogiItem>>(json)

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
    val hemat: Int = 0,
)
