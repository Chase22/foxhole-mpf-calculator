import '@fontsource/jost';
import loadData from "./loadData";
import {groupBy} from "./ArrayUtils";
import {Category, Item} from './Models'
import {also, appendChild, ifPresent} from "./HtmlUtils";
import {combineLatest, concat, fromEvent, map, Observable, of} from "rxjs";
import {add, asCrates, calculateItemQueueCost, Cost, ZERO_COST} from "./Cost";

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
    appendChild(mpfSelectionTable, "tr", tr => {
        appendChild(tr, "td", (td) => {
            td.innerText = capitalize(category)
        })

        appendChild(tr, "td", (td) => {
            appendChild(td, "select", (select) => {
                select.append(makeOption(""))
                items.sort((a, b) => a.itemName > b.itemName ? 1 : -1).forEach(item => {
                    select.append(makeOption(item.itemName))
                })

                let costObservable = concat(of(ZERO_COST), fromEvent(select, "change")
                    .pipe(map(() => getItem(category, select.value)))
                    .pipe(map((item) => calculateItemQueueCost(item))))

                costObservable.subscribe(cost => {
                    for (const costKey in cost) {
                        ifPresent(
                            document.getElementById(`${category}-cost-${costKey}`),
                            (elem) => elem.innerText = cost[costKey].toString()
                        )
                    }
                })
                costObservables[category] = costObservable

            })
        })

        for (const costKey in ZERO_COST) {
            appendChild(tr, "td", (td) => {
                td.id = `${category}-cost-${costKey}`
                td.innerText = "0"
            })
        }
    })
}

combineLatest(Object.values(costObservables) as Observable<Cost>[])
    .pipe(map(value => value.reduce((acc, curr) => add(acc, curr))))
    .subscribe(totalCost => {
        const costAsCrates = asCrates(totalCost)
        for (const totalCostKey in totalCost) {
            ifPresent(document.getElementById(`total-cost-${totalCostKey}`), (elem) => {
                elem.innerText = totalCost[totalCostKey]
            })

            ifPresent(document.getElementById(`total-cost-crates-${totalCostKey}`), (elem) => {
                elem.innerText = costAsCrates[totalCostKey]
            })
        }
    })

appendChild(mpfSelectionTable, "tr", tr => {
    appendChild(tr, "td")
    appendChild(tr, "td", td => {
        td.innerText = "Total"
    })
    for (const costKey in ZERO_COST) {
        appendChild(tr, "td", (td) => {
            td.id = `total-cost-${costKey}`
            td.innerText = "0"
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
        })
    }
})

function getItem(category: string, itemName: string): Item | undefined {
    const items = itemsByCategory[category] as Item[]
    return items.find(item => item.itemName === itemName)
}

function makeOption(label: string) {
    return also(document.createElement("option"), (option) => {
        option.label = label
        option.value = label
    })
}

function capitalize(value: string): string {
    return value.split("_").map((it) => it.charAt(0).toUpperCase() + it.substring(1)).join(" ")
}