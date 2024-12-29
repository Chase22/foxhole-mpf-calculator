import groovy.json.JsonSlurper

JsonSlurper().parse(file("package.json"))
    .uncheckedCast<Map<String,Any>>()
    .get("scripts")
    .uncheckedCast<Map<String,Any>>()
    .keys.forEach {
        task<Exec>("yarn_$it") {
            group = "yarn"
            commandLine("sh")
            args("-c", "yarn $it")
            setErrorOutput(System.err)
            setStandardOutput(System.out)
        }
    }

@Suppress("UNCHECKED_CAST")
fun <T> Any?.uncheckedCast() : T = this as T