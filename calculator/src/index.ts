import '@fontsource/jost';
import loadData from "./loadData";
import {groupBy} from "./ArrayUtils";
import {Category, CategoryUiContext, Item, PlayerFaction} from './Models'
import {as} from "./HtmlUtils";
import {combineLatest, fromEvent, map, startWith, tap} from "rxjs";
import {add, asCrates, calculateItemQueueCost} from "./Cost";
import {getPlayerFaction, getSavedSelectedItemName, setPlayerFaction, setSavedSelectedItemName} from "./LocalStorage";

import 'boxicons'

const items = loadData()
const itemsByCategory = groupBy(items, item => item.itemCategory)

const uiContexts = new Map<Category, CategoryUiContext>()

Array.from(document.getElementsByClassName("queue-select"))
    .map(as<HTMLSelectElement>)
    .map(select => {
        const category: Category = select.getAttribute("data-category") as Category

        const selectedItemName = getSavedSelectedItemName(category)

        Array.from(select.children)
            .map(option => option as HTMLOptionElement)
            .forEach(option => {
                option.selected = option.value === selectedItemName
            })

        let costObservable = fromEvent(select, "change")
            .pipe(startWith(calculateItemQueueCost(getItem(category, getSavedSelectedItemName(category)))))
            .pipe(tap(() => setSavedSelectedItemName(category as Category, select.value)))
            .pipe(map(() => getItem(category, select.value)))
            .pipe(map((item) => calculateItemQueueCost(item)));

        uiContexts.set(category, {
            costObservable: costObservable,
            selectElement: select
        })
    })

Array.from(document.getElementsByClassName("reset-button"))
    .forEach(element => {
        const category = element.getAttribute("data-category") as Category | null

        if (category == null) {
            fromEvent(element, "click").subscribe(() => {
               if (confirm("Do you want to clear all queues?")) {
                   Array.from(uiContexts.values()).forEach(ctx => {
                       ctx.selectElement.value = ""
                       ctx.selectElement.dispatchEvent(new Event("change"))
                   })
               }
            })
        }

        fromEvent(element, "click").subscribe(() => {
            uiContexts.get(category).selectElement.value = ""
            uiContexts.get(category).selectElement.dispatchEvent(new Event("change"))
        })
    })

Array.from(document.getElementsByClassName("cost-cell"))
    .map(td => td as HTMLTableCellElement)
    .forEach(td => {
        const category = td.getAttribute("data-category") as Category
        const resource = td.getAttribute("data-resource")

        uiContexts.get(category).costObservable.subscribe(cost => {
            td.innerText = cost[resource]
        })
    })

const totalCostObservable = combineLatest(
    Array.from(uiContexts.values()).map(context => context.costObservable)
).pipe(map(value => value.reduce((acc, curr) => add(acc, curr))))

const totalCrateCostObservable = totalCostObservable.pipe(map(asCrates))

Array.from(document.getElementsByClassName("total-cost-cell"))
    .map(td => td as HTMLTableCellElement)
    .forEach(td => {
        const resource = td.getAttribute("data-resource")

        totalCostObservable.subscribe(cost => {
            td.innerText = cost[resource]
        })
    })

Array.from(document.getElementsByClassName("total-crate-cell"))
    .map(td => td as HTMLTableCellElement)
    .forEach(td => {
        const resource = td.getAttribute("data-resource")

        totalCrateCostObservable.subscribe(cost => {
            td.innerText = cost[resource]
        })
    })

fromEvent(document.getElementById("faction-selection"), "change")
    .pipe(map(ev => (ev.target as HTMLSelectElement).value as PlayerFaction))
    .pipe(startWith(getPlayerFaction()))
    .pipe(tap(faction => setPlayerFaction(faction)))
    .subscribe(faction => hideItemsBasedOnFaction(faction))

function getItem(category: string, itemName: string): Item | undefined {
    if (itemName === "") return undefined

    const items = itemsByCategory[category] as Item[]
    const item = items.find(item => item.itemName === itemName);

    if (item == undefined) {
        console.warn(`No item named ${itemName} found in category ${category}`)
    }
    return item
}

function hideItemsBasedOnFaction(playerFaction: PlayerFaction) {
    Array.from(document.getElementsByClassName("item-option")).map(as<HTMLOptionElement>).forEach(option => {
        const factions = option.dataset["faction"].split(",").map(faction => faction.trim())

        option.hidden = !factions.includes(playerFaction)
    })
}