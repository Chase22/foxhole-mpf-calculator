import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.listProperty
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

interface HtmlFile {
    val name: String

    fun TagConsumer<*>.generate()
}

abstract class GenerateHtmlTask : DefaultTask() {

    private val fileGenerators: ListProperty<KClass<out HtmlFile>> =
        project.objects.listProperty<KClass<out HtmlFile>>().convention(
            listOf(IndexFile::class)
        )

    @get:OutputDirectory
    val targetDirectory: DirectoryProperty = project.objects.directoryProperty().convention(
        project.layout.buildDirectory.dir("html")
    )

    @TaskAction
    fun generateHtml() {
        val targetDir = targetDirectory.asFile.get().apply {
            mkdir()
        }

        fileGenerators.get().map {
            it.objectInstance ?: it.primaryConstructor!!.call()
        }.forEach { file ->
            targetDir.resolve("${file.name}.html").outputStream().bufferedWriter().use {
                val consumer = it.appendHTML()
                with(file) {
                    consumer.generate()
                }
            }
        }
    }
}