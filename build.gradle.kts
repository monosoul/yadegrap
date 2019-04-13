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

gradlePlugin {
    plugins {
        create("yetAnotherDelombokGradlePlugin") {
            id = "yadegrap"
            implementationClass = "com.github.monosoul.yadegrap.YetAnotherDelombokGradlePlugin"
        }
    }
}

pluginBundle {
    (plugins) {
        "yetAnotherDelombokGradlePlugin" {
            displayName = "Yet Another Delombok Gradle Plugin"
            description = "This plugin provides a configurable delombok task that uses Ant delombok task."
            tags = listOf("lombok", "delombok", "java")
            version = project.version as String

            website = "https://github.com/monosoul/yadegrap"
            vcsUrl = "https://github.com/monosoul/yadegrap"
        }
    }

    mavenCoordinates {
        artifactId = project.name
        version = project.version as String
    }
}