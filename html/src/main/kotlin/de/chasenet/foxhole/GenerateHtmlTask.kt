package de.chasenet.foxhole

import kotlinx.html.TagConsumer
import kotlinx.html.stream.appendHTML
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GenerateHtmlTask : DefaultTask() {
    @get:OutputFile
    val outputFile: RegularFileProperty = project.objects.fileProperty().convention(
        project.layout.buildDirectory.dir("html").map { it.file("index.html") }
    )

    @TaskAction
    fun generateHtml() {
        outputFile.get().asFile.outputStream().bufferedWriter().use {
            val consumer = it.appendHTML()
            with(this) {
                consumer.generate()
            }
        }
    }

    abstract fun TagConsumer<*>.generate()
}