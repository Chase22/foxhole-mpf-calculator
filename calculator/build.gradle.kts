import groovy.json.JsonSlurper

plugins {
    base
}

JsonSlurper().parse(file("package.json"))
    .uncheckedCast<Map<String,Any>>()
    .get("scripts")
    .uncheckedCast<Map<String,Any>>()
    .keys.forEach {
        task<Exec>("yarn_$it") {
            group = "yarn"
            commandLine("sh")
            args("-c", "yarn $it")
            logging.captureStandardOutput(LogLevel.INFO)
            logging.captureStandardError(LogLevel.ERROR)
        }
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
fun <T> Any?.uncheckedCast() : T = this as T