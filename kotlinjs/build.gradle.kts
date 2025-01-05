import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget

plugins {
    kotlin("multiplatform") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
}

val modules = arrayOf("mpf-calculator")

dependencies {
    commonMainImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
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
                    mainOutputFileName = "$it.js"
                }

                runTask {
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

        modules.forEach {
            named("${it}Main") {
                resources.srcDirs(htmlDir)
            }
        }
    }
}
tasks.withType<KotlinJsCompile>().configureEach {
    compilerOptions {
        target = "es2015"
    }
}

val downloadJsonData = task<DownloadJsonDataTask>("downloadJsonData")

val htmlGeneratorJar by tasks.existing
val htmlGeneratorRuntimeClasspath by configurations.existing

val buildHtml =
    task<JavaExec>("buildHtml") {
        dependsOn(downloadJsonData)

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
    dependsOn(buildHtml)
}
