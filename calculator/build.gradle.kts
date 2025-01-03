import groovy.json.JsonSlurper

plugins {
    base
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