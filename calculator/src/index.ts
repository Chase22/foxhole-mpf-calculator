import '@fontsource/jost';
import loadData from "./loadData";
import {groupBy} from "./ArrayUtils";
import {Category, Item, PlayerFaction} from './Models'
import {as} from "./HtmlUtils";
import {combineLatest, concat, fromEvent, map, Observable, of, startWith, tap} from "rxjs";
import {add, asCrates, calculateItemQueueCost, Cost} from "./Cost";
import {getPlayerFaction, getSavedSelectedItemName, setPlayerFaction, setSavedSelectedItemName} from "./LocalStorage";

const items = loadData()

const itemsByCategory = groupBy(items, item => item.itemCategory)

const costObservables = new Map<Category, Observable<Cost>>()

Array.from(document.getElementsByClassName("queue-select"))
    .map(select => select as HTMLSelectElement)
    .map(select => {
        const category: Category = select.getAttribute("data-category") as Category

        const selectedItemName = getSavedSelectedItemName(category)

        Array.from(select.children)
            .map(option => option as HTMLOptionElement)
            .forEach(option => {
                option.selected = option.value === selectedItemName
            })

        costObservables[category] = concat(
            of(calculateItemQueueCost(getItem(category, getSavedSelectedItemName(category as Category)))),
            fromEvent(select, "change")
                .pipe(tap(() => setSavedSelectedItemName(category as Category, select.value)))
                .pipe(map(() => getItem(category, select.value)))
                .pipe(map((item) => calculateItemQueueCost(item))))
    })

Array.from(document.getElementsByClassName("cost-cell"))
    .map(td => td as HTMLTableCellElement)
    .forEach(td => {
        const category = td.getAttribute("data-category")
        const resource = td.getAttribute("data-resource")

        costObservables[category].subscribe(cost => {
            td.innerText = cost[resource]
        })
    })

const totalCostObservable = combineLatest(Object.values(costObservables) as Observable<Cost>[])
    .pipe(map(value => value.reduce((acc, curr) => add(acc, curr))))

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