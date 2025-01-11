package de.chasenet.foxhole.model

import kotlinx.serialization.Serializable
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

@Serializable
data class Cost(
    val bmat: Int = 0,
    val rmat: Int = 0,
    val emat: Int = 0,
    val hemat: Int = 0,
) {
    operator fun plus(cost: Cost): Cost =
        Cost(
            bmat = bmat + cost.bmat,
            rmat = rmat + cost.rmat,
            emat = emat + cost.emat,
            hemat = hemat + cost.hemat,
        )

    operator fun get(resource: Resource): Int =
        when (resource) {
            Resource.bmat -> bmat
            Resource.rmat -> rmat
            Resource.emat -> emat
            Resource.hemat -> hemat
        }

    fun asCrates() =
        Cost(
            bmat = ceil(bmat / ResourcesPerCrate[Resource.bmat]!!.toFloat()).toInt(),
            emat = ceil(bmat / ResourcesPerCrate[Resource.emat]!!.toFloat()).toInt(),
            hemat = ceil(bmat / ResourcesPerCrate[Resource.hemat]!!.toFloat()).toInt(),
            rmat = ceil(bmat / ResourcesPerCrate[Resource.rmat]!!.toFloat()).toInt(),
        )

    fun queueCost(cratesInQueue: Int) =
        Cost(
            bmat = calculateQueueCost(bmat, cratesInQueue),
            rmat = calculateQueueCost(rmat, cratesInQueue),
            emat = calculateQueueCost(emat, cratesInQueue),
            hemat = calculateQueueCost(hemat, cratesInQueue),
        )
}

private fun calculateQueueCost(
    cost: Int,
    cratesInQueue: Int,
) = (0..cratesInQueue)
    .reduce { acc, crate ->
        acc + floor(cost.toFloat() * (1 - min(crate * 0.1, 0.5))).toInt()
    }
