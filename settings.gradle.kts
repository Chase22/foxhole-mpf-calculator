@file:Suppress("UnstableApiUsage")


include("calculator")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

pluginManagement {
    includeBuild("html")
}