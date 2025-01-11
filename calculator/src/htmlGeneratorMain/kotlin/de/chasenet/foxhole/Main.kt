package de.chasenet.foxhole

import de.chasenet.foxhole.html.IndexFile
import de.chasenet.foxhole.model.LogiItem
import de.chasenet.foxhole.model.deserializeLogiItems
import kotlinx.html.TagConsumer
import kotlinx.html.stream.appendHTML
import java.io.FileNotFoundException
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writer

val htmlFiles = listOf(IndexFile)

fun main(args: Array<String>) {
    if (args.size < 2) {
        throw IllegalArgumentException("2 arguments required: <Html output directory> <foxhole item json input file>")
    }

    val (htmlOutputDir, foxholeJsonDataFile) = args.map { Path(it) }

    if (!foxholeJsonDataFile.exists()) {
        throw FileNotFoundException("Foxhole item json input file does not exist: $foxholeJsonDataFile")
    }

    htmlOutputDir.createDirectories()

    val logiItems = deserializeLogiItems(foxholeJsonDataFile.readText())

    htmlFiles.forEach {
        with(it) {
            htmlOutputDir
                .resolve(fileName)
                .writer()
                .appendHTML()
                .generate(logiItems)
        }
    }

    println(args.joinToString())
}

interface HtmlFile {
    val fileName: String

    fun TagConsumer<*>.generate(logiItems: List<LogiItem>)
}
