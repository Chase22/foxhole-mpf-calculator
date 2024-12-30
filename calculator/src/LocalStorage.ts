import {Category, PlayerFaction} from "./Models";

const FACTION_KEY = "player-faction"

const SELECTED_ITEM_KEY_PREFIX = "selected-item"

export function setPlayerFaction(faction: PlayerFaction) {
    localStorage.setItem(FACTION_KEY, faction)
}

export function getPlayerFaction(): PlayerFaction {
    return localStorage.getItem(FACTION_KEY) as PlayerFaction | "colonial"
}

export function getSavedSelectedItemName(category: Category): string {
    return localStorage.getItem(selectedItemKey(category)) as string | ""
}

export function setSavedSelectedItemName(category: Category, itemName: string) {
    localStorage.setItem(selectedItemKey(category), itemName)
}

function selectedItemKey(category: Category): string {
    return `${SELECTED_ITEM_KEY_PREFIX}-${category}`
}