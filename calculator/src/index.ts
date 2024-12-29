import '@fontsource/jost';
import loadData from "./loadData";
import {groupBy} from "./ArrayUtils";
import {Item} from './Models'
import {also, appendChild} from "./HtmlUtils";

const items = loadData().filter(value =>
    value.faction.indexOf("colonial") > 0
)

const itemsByCategory = groupBy(items, item => item.itemCategory)

const mpfSelectionTable = document.getElementById("mpf-selection")
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
                            select.addEventListener("change", ev => {
                                // @ts-ignore
                                onSelectionChanged(category, select.value)
                            })
                        }
                    )
                })
            })
        }
    )
}

function onSelectionChanged(category: string, itemName: string) {
    const items = itemsByCategory[category] as Item[]
    const item = items.find(item => item.itemName === itemName)

    console.log(item.itemName)
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

function calculateQueueCost(costPerCrate: number, crateCount: number) {
    let cost = 0;
    for (let i = 0; i < crateCount; i++) {
        cost += costPerCrate * (1 - Math.min(crateCount*0.1, 0.5))
    }
    return cost
}