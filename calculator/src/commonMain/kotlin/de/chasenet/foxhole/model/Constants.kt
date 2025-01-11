package de.chasenet.foxhole.model

val CratesPerQueue: Map<ItemCategory, Int> =
    mapOf(
        ItemCategory.small_arms to 9,
        ItemCategory.heavy_arms to 9,
        ItemCategory.heavy_ammunition to 9,
        ItemCategory.supplies to 9,
        ItemCategory.uniforms to 9,
        ItemCategory.shipables to 5,
        ItemCategory.vehicles to 5,
    )

val ResourcesPerCrate: Map<Resource, Int> =
    mapOf(
        Resource.bmat to 100,
        Resource.emat to 40,
        Resource.hemat to 30,
        Resource.rmat to 20,
    )
