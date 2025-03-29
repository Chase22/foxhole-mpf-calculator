import org.jetbrains.kotlin.gradle.dsl.JsModuleKind
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget

plugins {
    kotlin("multiplatform") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
}

val modules = arrayOf("mpf-calculator")

dependencies {
    commonMainImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    commonTestImplementation(kotlin("test"))
}

val htmlDir =
    provider {
        tasks.getByName<ProcessResources>("htmlGeneratorProcessResources").destinationDir
    }

kotlin {
    modules.forEach {
        js(it, IR) {
            browser {
                binaries.executable()

                commonWebpackConfig {
                    cssSupport {
                        enabled = true
                    }
                }

                webpackTask {
                    sourceMaps = true
                    mainOutputFileName = "$it.js"
                }

                runTask {
                    sourceMaps = true
                    mainOutputFileName = "$it.js"
                }
            }
        }
    }

    jvm("htmlGenerator")
    sourceSets {
        this.getByName("htmlGeneratorMain") {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")
            }
        }

        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
            }
        }

        modules.forEach {
            named("${it}Main") {
                resources.srcDirs(
                    htmlDir,
                    provider {
                        downloadJsonData.outputFile
                            .get()
                            .asFile.parentFile
                    },
                )
            }
        }

        commonTest.dependencies {
            implementation("io.kotest:kotest-assertions-core:5.9.1")
        }
    }
}
tasks.withType<KotlinJsCompile>().configureEach {
    compilerOptions {
        target = "es2015"
        this.moduleKind = JsModuleKind.MODULE_ES
    }
}

val downloadJsonData =
    task<DownloadJsonDataTask>("downloadJsonData") {
        outputFile = layout.buildDirectory.dir("json").map { it.file("foxhole.json") }
    }

val htmlGeneratorJar by tasks.existing
val htmlGeneratorRuntimeClasspath by configurations.existing

val buildHtml =
    task<JavaExec>("buildHtml") {
        dependsOn(downloadJsonData)

        outputs.dir(htmlDir)

        group = "build"

        classpath(htmlGeneratorJar, htmlGeneratorRuntimeClasspath)
        mainClass = "de.chasenet.foxhole.MainKt"

        args(
            htmlDir.get().absolutePath,
            downloadJsonData.outputFile
                .get()
                .asFile.absolutePath,
        )
    }

kotlin.targets.withType<KotlinJsIrTarget>().configureEach {
    runTask.configure {
        dependsOn(buildHtml)
    }
}

tasks.withType<ProcessResources> {
    if (name == "htmlGeneratorProcessResources") return@withType
    dependsOn(buildHtml, downloadJsonData)
}
