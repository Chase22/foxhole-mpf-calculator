package de.chasenet.foxhole

import de.chasenet.foxhole.model.ItemCategory
import de.chasenet.foxhole.model.LogiItem
import de.chasenet.foxhole.model.resources
import kotlinx.html.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.internal.extensions.stdlib.capitalized

abstract class GenerateIndexFileTask : GenerateHtmlTask() {
    @get:InputFile
    abstract val foxholeJsonDataFile: RegularFileProperty

    private val json = Json { ignoreUnknownKeys = true }

    init {
        outputFile.convention(
            project.layout.buildDirectory.dir("html").map { it.file("index.html") }
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun TagConsumer<*>.generate() {
        val items = json.decodeFromStream<List<LogiItem>>(foxholeJsonDataFile.asFile.get().inputStream())

        items.filter { it.isMpfCraftable }.also {
            it.filter { it.itemCategory == null }
                .takeIf { it.isNotEmpty() }
                ?.joinToString { it.itemName }
                ?.also { throw IllegalArgumentException("Items without item category found: $it") }
        }

        val itemsByCategory = items.filter { it.isMpfCraftable }.groupBy { it.itemCategory }

        html {
            lang = "en"
            head {
                meta(charset = "UTF-8")
                title("Foxhole MPF Calculator")
                script(type = "module", src = "../index.ts") {}
                link(rel = "stylesheet", href = "../main.scss")
            }
            body {
                main {
                    div {
                        classes = setOf("logo-container")
                        img(src = "../img/foxhole_logo_large.png") {
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

                            ItemCategory.values().forEach { category ->
                                tr {
                                    td {
                                        boxIcon("reset") {
                                            color = "white"

                                            attributes["class"] = "reset-button"
                                            attributes["data-category"] = category.name
                                            attributes["title"] = "Clear Queue"
                                        }
                                    }
                                    td {
                                        text(category.name.split("_").joinToString(" ") { it.capitalized() })
                                    }
                                    td {
                                        select("queue-select") {
                                            attributes["data-category"] = category.name
                                            
                                            option {
                                                text("")
                                            }
                                            itemsByCategory[category]!!.sortedBy { it.itemName }.forEach { item ->
                                                option("item-option") {
                                                    label = item.itemName
                                                    value = item.itemName
                                                    attributes["data-faction"] = item.faction.joinToString()
                                                }
                                            }
                                        }
                                    }

                                    resources.forEach { resource ->
                                        td {
                                            attributes["data-resource"] = resource
                                            attributes["data-category"] = category.name
                                            classes = setOf("cost-cell")
                                            text(0)
                                        }
                                    }
                                }
                            }

                            tr {
                                td()
                                td {
                                    text("Total")
                                }
                                resources.forEach { resource ->
                                    td("total-cost-cell") {
                                        attributes["data-resource"] = resource
                                        text(0)
                                    }
                                }
                            }

                            tr {
                                td()
                                td {
                                    text("Crates")
                                }
                                resources.forEach { resource ->
                                    td("total-crate-cell") {
                                        attributes["data-resource"] = resource
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
                                    "GPLv3"
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