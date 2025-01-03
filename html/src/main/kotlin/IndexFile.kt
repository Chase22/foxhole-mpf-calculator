import kotlinx.html.*

object IndexFile : HtmlFile {
    override val name: String = "index"

    override fun TagConsumer<*>.generate() {
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
                    }

                    section {
                        table {
                            id = "mpf-selection"
                            tr {
                                th { text("Queue") }
                                th { text("Item") }
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