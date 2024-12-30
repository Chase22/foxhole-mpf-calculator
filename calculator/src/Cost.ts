import {CratePerQueue, Item, ResourcePerCrate} from "./Models";

export interface Cost {
    bmat: number,
    rmat: number,
    emat: number,
    hemat: number
}

export const ZERO_COST: Cost = {
    bmat: 0,
    rmat: 0,
    emat: 0,
    hemat: 0
}

export function calculateQueueCost(costPerCrate: number, crateCount: number) {
    let cost = 0;
    for (let i = 1; i <= crateCount; i++) {
        cost += Math.floor(costPerCrate * (1 - Math.min(i * 0.1, 0.5)))
    }
    return cost
}

export function add(a: Cost, b: Cost): Cost {
    return {
        bmat: a.bmat + b.bmat,
        emat: a.emat + b.emat,
        hemat: a.hemat + b.hemat,
        rmat: a.rmat + b.rmat
    }
}

export function calculateItemQueueCost(item: Item): Cost {
    if (item == undefined) return ZERO_COST
    const crateCount = CratePerQueue[item.itemCategory]
    return {
        bmat: calculateQueueCost(item.cost.bmat, crateCount),
        emat: calculateQueueCost(item.cost.emat, crateCount),
        hemat: calculateQueueCost(item.cost.hemat, crateCount),
        rmat: calculateQueueCost(item.cost.rmat, crateCount)
    }
}

export function asCrates(cost: Cost): Cost {
    return {
        bmat: Math.ceil(cost.bmat/ResourcePerCrate["bmat"]),
        emat: Math.ceil(cost.emat/ResourcePerCrate["emat"]),
        hemat: Math.ceil(cost.hemat/ResourcePerCrate["hemat"]),
        rmat: Math.ceil(cost.rmat/ResourcePerCrate["rmat"])
    }
}