import '@fontsource/jost';
import loadData from "./loadData";
import {groupBy} from "./ArrayUtils";
import {Category, Item} from './Models'
import {appendChild, createElement} from "./HtmlUtils";
import {combineLatest, concat, fromEvent, map, Observable, of, tap} from "rxjs";
import {add, asCrates, calculateItemQueueCost, Cost, ZERO_COST} from "./Cost";
import {getSavedSelectedItemName, setSavedSelectedItemName} from "./LocalStorage";

const items = loadData().filter(value => value.faction.indexOf("colonial") > 0)

const itemsByCategory = groupBy(items, item => item.itemCategory)

const costObservables: Record<Category, Observable<Cost> | undefined> = {
    heavy_ammunition: undefined,
    heavy_arms: undefined,
    shipables: undefined,
    small_arms: undefined,
    supplies: undefined,
    uniforms: undefined,
    vehicles: undefined
}

const mpfSelectionTable = document.getElementById("mpf-selection")

const headRow = mpfSelectionTable.getElementsByTagName("tr").item(0)

for (const costKey in ZERO_COST) {
    appendChild(headRow, "th", (th) => {
        th.innerText = costKey
    })
}

for (const [category, items] of Object.entries(itemsByCategory)) {
    const selectedItemName = getSavedSelectedItemName(category as Category)

    appendChild(mpfSelectionTable, "tr", tr => {
        appendChild(tr, "td", (td) => {
            td.innerText = capitalize(category)
        })

        appendChild(tr, "td", (td) => {
            appendChild(td, "select", (select) => {
                let option = makeOption("");
                option.selected = selectedItemName === ""

                select.append(option)
                items.sort((a, b) => a.itemName > b.itemName ? 1 : -1).forEach(item => {
                    let option = makeOption(item.itemName);
                    option.selected = selectedItemName === item.itemName

                    select.append(option)
                })

                costObservables[category] = concat(
                    of(calculateItemQueueCost(getItem(category, getSavedSelectedItemName(category as Category)))),
                    fromEvent(select, "change")
                        .pipe(tap(() => setSavedSelectedItemName(category as Category, select.value)))
                        .pipe(map(() => getItem(category, select.value)))
                        .pipe(map((item) => calculateItemQueueCost(item))))

            })
        })

        for (const costKey in ZERO_COST) {
            appendChild(tr, "td", (td) => {
                td.id = `${category}-cost-${costKey}`
                td.innerText = "0"

                costObservables[category].subscribe(cost => {
                    td.innerText = cost[costKey].toString()
                })
            })
        }
    })
}

const totalCostObservable = combineLatest(Object.values(costObservables) as Observable<Cost>[])
    .pipe(map(value => value.reduce((acc, curr) => add(acc, curr))))

const totalCrateCostObservable = totalCostObservable.pipe(map(asCrates))

appendChild(mpfSelectionTable, "tr", tr => {
    appendChild(tr, "td")
    appendChild(tr, "td", td => {
        td.innerText = "Total"
    })
    for (const costKey in ZERO_COST) {
        appendChild(tr, "td", (td) => {
            td.id = `total-cost-${costKey}`
            td.innerText = "0"

            totalCostObservable.subscribe(totalCost => {
                td.innerText = totalCost[costKey]
            })

        })
    }
})

appendChild(mpfSelectionTable, "tr", tr => {
    appendChild(tr, "td")
    appendChild(tr, "td", td => {
        td.innerText = "Crates"
    })
    for (const costKey in ZERO_COST) {
        appendChild(tr, "td", (td) => {
            td.id = `total-cost-crates-${costKey}`
            td.innerText = "0"

            totalCrateCostObservable.subscribe(totalCost => {
                td.innerText = totalCost[costKey]
            })
        })
    }
})

function getItem(category: string, itemName: string): Item | undefined {
    const items = itemsByCategory[category] as Item[]
    return items.find(item => item.itemName === itemName)
}

function makeOption(label: string) {
    return createElement("option", (option) => {
        option.label = label
        option.value = label
    })
}

function capitalize(value: string): string {
    return value.split("_").map((it) => it.charAt(0).toUpperCase() + it.substring(1)).join(" ")
}