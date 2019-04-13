group = "com.github.monosoul"
version = "0.0.1"

plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "0.10.1"
}

repositories {
    jcenter()
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}