import {Cost} from "./Cost";

export type PlayerFaction =  "colonial" | "warden"
export type Faction = "neutral" | PlayerFaction

export type Category = "heavy_ammunition" |
    "heavy_arms" |
    "shipables" |
    "small_arms" |
    "supplies" |
    "uniforms" |
    "vehicles"

export const CratePerQueue: Record<Category, number> = {
    heavy_ammunition: 9,
    heavy_arms: 9,
    shipables: 5,
    small_arms: 9,
    supplies: 9,
    uniforms: 9,
    vehicles: 5
}

export const ResourcePerCrate: Record<keyof Cost, number> = {
    bmat: 100,
    emat: 40,
    hemat: 30,
    rmat: 20

}

export interface LogiItem {
    readonly displayId: number,
    readonly faction: ReadonlyArray<string>,
    readonly imgName: string,
    readonly itemName: string,
    readonly itemDesc: string,
    readonly itemCategory: string,
    readonly itemClass: string,
    readonly ammoUsed: string,
    readonly numberProduced: number,
    readonly isTeched: boolean,
    readonly isMpfCraftable: boolean,
    readonly craftLocation: ReadonlyArray<string>,
    readonly cost: {
        readonly bmat: number | undefined
        readonly rmat: number | undefined
        readonly emat: number | undefined
        readonly hemat: number | undefined
    }
}

export interface Item {
    readonly displayId: number,
    readonly faction: ReadonlyArray<Faction>,
    readonly imgName: string,
    readonly itemName: string,
    readonly itemDesc: string,
    readonly itemCategory: Category,
    readonly itemClass: string,
    readonly ammoUsed: string,
    readonly numberProduced: number,
    readonly isMpfCraftable: boolean,
    readonly craftLocation: ReadonlyArray<string>,
    readonly cost: Cost
}

export function mapLogiItemToItem(logiItem: LogiItem): Item {
    return {
        ammoUsed: logiItem.ammoUsed,
        cost: {
            bmat: logiItem.cost.bmat || 0,
            emat: logiItem.cost.emat || 0,
            hemat: logiItem.cost.hemat || 0,
            rmat: logiItem.cost.rmat || 0,
        },
        craftLocation: logiItem.craftLocation,
        displayId: logiItem.displayId,
        faction: logiItem.faction as ReadonlyArray<Faction>,
        imgName: logiItem.imgName,
        isMpfCraftable: logiItem.isMpfCraftable,
        itemCategory: logiItem.itemCategory as Category,
        itemClass: logiItem.itemClass,
        itemDesc: logiItem.itemDesc,
        itemName: logiItem.itemName,
        numberProduced: logiItem.numberProduced

    }
}