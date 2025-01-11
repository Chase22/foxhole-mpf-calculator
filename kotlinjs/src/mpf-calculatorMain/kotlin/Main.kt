import de.chasenet.foxhole.model.Cost
import de.chasenet.foxhole.model.CratesPerQueue
import de.chasenet.foxhole.model.Faction
import de.chasenet.foxhole.model.ItemCategory
import de.chasenet.foxhole.model.LogiItem
import de.chasenet.foxhole.model.deserializeLogiItems
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLOptionElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTableCellElement
import org.w3c.dom.events.Event
import org.w3c.fetch.Request

fun Map<ItemCategory, CategoryContext>.safeGet(category: ItemCategory): CategoryContext =
    this[category] ?: throw NullPointerException("Can't find categoryContext for $category")

class CategoryContext(
    val selectedItemFlow: Flow<String>,
    val select: HTMLSelectElement,
    items: Flow<List<LogiItem>>,
) {
    val costFlow =
        selectedItemFlow
            .combine(items) { selectedItem, items ->
                console.log(selectedItem)
                if (selectedItem.isBlank()) return@combine null

                items.find { it.itemName == selectedItem }.also {
                    if (it == null) {
                        console.warn("Item not found: $selectedItem")
                    }
                }
            }.map { item ->
                item?.let {
                    val cratesInQueue by notNull(CratesPerQueue[it.itemCategory])

                    it.cost.queueCost(cratesInQueue)
                } ?: Cost()
            }
}

fun main() {
    val coroutineScope = CoroutineScope(window.asCoroutineDispatcher())

    val items =
        flow {
            emit(
                window
                    .fetch(
                        Request("/foxhole.json"),
                    ).then {
                        it.text()
                    }.then {
                        deserializeLogiItems(it)
                    }.await(),
            )
        }

    val categoryContextMap: Map<ItemCategory, CategoryContext> =
        document
            .getElementsByClassName<HTMLSelectElement>("queue-select")
            .associate { select ->
                val category = select.category ?: throw NullPointerException("queue-select is missing category")

                val savedItem = getSavedSelectedItem(category)

                select.children
                    .asListOf<HTMLOptionElement>()
                    .forEach { option ->
                        option.selected = option.value == savedItem
                    }

                category to
                    CategoryContext(
                        items = items,
                        select = select,
                        selectedItemFlow =
                            select
                                .addEventFlow("change") {
                                    select.value
                                }.onStart { emit(savedItem) }
                                .onEach {
                                    saveSelectedItem(category, it)
                                },
                    )
            }

    val totalCostFlow =
        combine(categoryContextMap.values.map { it.costFlow }) { costs ->
            costs.reduce { a, b -> a + b }
        }

    val totalCratesFlow = totalCostFlow.map(Cost::asCrates)

    document
        .getElementsByClassName<HTMLElement>("reset-button")
        .forEach { element ->
            val category = element.category

            if (category == null) {
                element.addEventListener(
                    "click",
                    {
                        categoryContextMap.values.forEach { context ->
                            context.select.value = ""
                            context.select.dispatchEvent(Event("change"))
                        }
                    },
                )
            } else {
                val select =
                    categoryContextMap
                        .safeGet(category)
                        .select

                element.addEventListener(
                    "click",
                    {
                        select.value = ""
                        select.dispatchEvent(Event("change"))
                    },
                )
            }
        }

    document.getElementsByClassName<HTMLTableCellElement>("cost-cell").forEach { cell ->
        val category by notNull(cell.category) { "cost-cell is missing category" }
        val resource by notNull(cell.resource) { "cost-cell is missing resource" }

        coroutineScope.launch {
            categoryContextMap
                .safeGet(category)
                .costFlow
                .collectLatest { cost ->
                    cell.innerText = cost[resource].toString()
                }
        }
    }

    coroutineScope.launch {
        val selectElement by notNull(document.getElementById<HTMLSelectElement>("faction-selection")) {
            "faction-select not found"
        }

        selectElement
            .addEventFlow("change") { it.target.unsafeCast<HTMLSelectElement>().value }
            .map { Faction.valueOf(it) }
            .onEach(::savePlayerFaction)
            .onStart { emit(getPlayerFaction()) }
            .collectLatest { faction ->
                hideItemsBasedOnFaction(faction)
            }
    }

    document.getElementsByClassName<HTMLTableCellElement>("total-cost-cell").map { cell ->
        val resource = cell.resource ?: throw NullPointerException("total-cost-cell is missing resource")

        coroutineScope.launch {
            totalCostFlow.collectLatest { cost ->
                cell.innerText = cost[resource].toString()
            }
        }
    }

    document.getElementsByClassName<HTMLTableCellElement>("total-crate-cell").map { cell ->
        val resource = cell.resource ?: throw NullPointerException("total-cost-cell is missing resource")

        coroutineScope.launch {
            totalCratesFlow.collectLatest { crates ->
                cell.innerText = crates[resource].toString()
            }
        }
    }
}

private fun hideItemsBasedOnFaction(faction: Faction) {
    document
        .getElementsByClassName("item-option")
        .asListOf<HTMLOptionElement>()
        .forEach { option ->
            option.hidden = option.faction?.contains(faction.name) == false
        }
}
