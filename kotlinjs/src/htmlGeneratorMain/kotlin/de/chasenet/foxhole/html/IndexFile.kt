@file:Suppress("ktlint:standard:no-wildcard-imports")

package de.chasenet.foxhole.html

import de.chasenet.foxhole.HtmlFile
import de.chasenet.foxhole.html.utils.boxIcon
import de.chasenet.foxhole.html.utils.category
import de.chasenet.foxhole.html.utils.faction
import de.chasenet.foxhole.html.utils.resource
import de.chasenet.foxhole.model.ItemCategory
import de.chasenet.foxhole.model.LogiItem
import de.chasenet.foxhole.model.resources
import de.chasenet.foxhole.utils.capitalized
import kotlinx.html.*

object IndexFile : HtmlFile {
    override val fileName: String = "index.html"

    override fun TagConsumer<*>.generate(logiItems: List<LogiItem>) {
        logiItems
            .filter { it.isMpfCraftable }
            .filter { it.itemCategory == null }
            .takeIf { it.isNotEmpty() }
            ?.joinToString { it.itemName }
            ?.also { throw IllegalArgumentException("Items without item category found: $it") }

        val itemsByCategory = logiItems.filter { it.isMpfCraftable }.groupBy { it.itemCategory }

        html {
            lang = "en"
            head {
                meta(charset = "UTF-8")
                title("Foxhole MPF Calculator")
                script(type = "module", src = "mpf-calculator.js") {}
                link(rel = "stylesheet", href = "main.scss")
            }
            body {
                main {
                    div {
                        classes = setOf("logo-container")
                        img(src = "foxhole_logo_large.png") {
                            attributes["width"] = "70%"
                        }
                        h1 { text("MPF Queue Calculator") }
                        select {
                            id = "faction-selection"
                            option {
                                value = "colonial"
                                label = "colonial"
                            }
                            option {
                                value = "warden"
                                label = "warden"
                            }
                        }
                    }

                    section {
                        table {
                            id = "mpf-selection"
                            tr {
                                th {
                                    boxIcon("reset") {
                                        color = "white"

                                        attributes["class"] = "reset-button"
                                        attributes["title"] = "Clear All Queues"
                                    }
                                }
                                th { text("Queue") }
                                th { text("Item") }
                                th { text("Bmat") }
                                th { text("Rmat") }
                                th { text("Emat") }
                                th { text("Hemat") }
                            }

                            ItemCategory.entries.forEach { category ->
                                tr {
                                    td {
                                        boxIcon("reset") {
                                            color = "white"

                                            attributes["class"] = "reset-button"
                                            this.category = category.name
                                            attributes["title"] = "Clear Queue"
                                        }
                                    }
                                    td {
                                        text(category.name.split("_").joinToString(" ") { it.capitalized() })
                                    }
                                    td {
                                        select("queue-select") {
                                            this.category = category.name

                                            option {}
                                            itemsByCategory[category]!!.sortedBy { it.itemName }.forEach { item ->
                                                option("item-option") {
                                                    label = item.itemName
                                                    value = item.itemName
                                                    this.faction = item.faction.joinToString()
                                                }
                                            }
                                        }
                                    }

                                    resources.forEach { resource ->
                                        td {
                                            this.resource = resource
                                            this.category = category.name
                                            classes = setOf("cost-cell")
                                            text(0)
                                        }
                                    }
                                }
                            }

                            tr {
                                td()
                                td()
                                td {
                                    text("Total")
                                }
                                resources.forEach { resource ->
                                    td("total-cost-cell") {
                                        this.resource = resource
                                        text(0)
                                    }
                                }
                            }

                            tr {
                                td()
                                td()
                                td {
                                    text("Crates")
                                }
                                resources.forEach { resource ->
                                    td("total-crate-cell") {
                                        this.resource = resource
                                        text(0)
                                    }
                                }
                            }
                        }
                    }
                }
                footer {
                    ul {
                        li {
                            text("Data provided by ")
                            a(href = "https://foxholelogi.com") { text("foxholelogi.com") }
                        }
                        text(" - ")
                        li {
                            text("Licensed under ")
                            a(href = "https://github.com/Chase22/foxhole-mpf-calculator/blob/main/LICENSE") {
                                text(
                                    "GPLv3",
                                )
                            }
                        }
                        text(" - ")
                        li {
                            text("Created by Chase. Source available at ")
                            a(href = "https://github.com/Chase22/foxhole-mpf-calculator") { text("GitHub") }
                        }
                    }
                }
            }
        }
    }
}
