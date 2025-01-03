import groovy.json.JsonSlurper

plugins {
    base
    id("html-builder")
}

abstract class YarnExec : AbstractExecTask<YarnExec>(YarnExec::class.java) {
    @get:Input
    abstract val script: Property<String>

    init {
        group = "yarn"
    }

    override fun exec() {
        commandLine("sh")
        args("-c", "yarn ${script.get()}")
        logging.captureStandardOutput(LogLevel.INFO)
        logging.captureStandardError(LogLevel.ERROR)
        super.exec()
    }

}

JsonSlurper().parse(file("package.json"))
    .uncheckedCast<Map<String, Any>>()
    .get("scripts")
    .uncheckedCast<Map<String, Any>>()
    .keys.forEach {
        task<YarnExec>("yarn_$it") {
            dependsOn("yarn_setup")
            script = it
        }
    }

task<YarnExec>("yarn_setup") {
    script = ""
}

val downloadJsonData = tasks.register<DownloadJsonDataTask>("downloadJsonData")

val generateIndexFile = tasks.register<GenerateIndexFileTask>("generateIndexFile") {
    dependsOn(downloadJsonData)

    foxholeJsonDataFile.set(downloadJsonData.flatMap { it.outputFile })

    outputFile = layout.projectDirectory.dir("src/generated").file("index.html")

}

tasks.named("yarn_build") {
    dependsOn(generateIndexFile)
}

tasks.named("yarn_start") {
    dependsOn(generateIndexFile)
}

tasks.build {
    dependsOn("yarn_build")
}

tasks.check {
    dependsOn("yarn_test-ci")
}

tasks.clean {
    dependsOn("yarn_clean")
}

@Suppress("UNCHECKED_CAST")
fun <T> Any?.uncheckedCast(): T = this as T