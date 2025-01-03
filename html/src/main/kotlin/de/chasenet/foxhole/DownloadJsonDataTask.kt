package de.chasenet.foxhole

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.net.URI
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime

abstract class DownloadJsonDataTask : DefaultTask() {
    @OutputFile
    val outputFile: RegularFileProperty = project.objects.fileProperty().convention(
        project.layout.buildDirectory.file("foxhole.json")
    )

    init {
        outputs.upToDateWhen {
            // Check if output is not older than 1 day
            Duration.between(
                Instant.ofEpochSecond(outputFile.asFile.get().lastModified()),
                OffsetDateTime.now()
            ).toDays() <= 1
        }
    }

    @TaskAction
    fun downloadFile() {
        URI.create("https://foxholelogi.com/assets/foxhole.json").toURL().openStream()
            .copyTo(outputFile.asFile.get().outputStream())
    }

}