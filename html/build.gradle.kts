plugins {
    `java-gradle-plugin`
    `kotlin-dsl`

    kotlin("plugin.serialization") version "2.0.20"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}

gradlePlugin {
    plugins {
        register("html-builder") {
            id = "html-builder"
            implementationClass = "de.chasenet.foxhole.HtmlBuilderPlugin"
        }
    }
}