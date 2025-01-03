plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")
}

gradlePlugin {
    plugins {
        register("html-builder") {
            id = "html-builder"
            implementationClass = "HtmlBuilderPlugin"
        }
    }
}