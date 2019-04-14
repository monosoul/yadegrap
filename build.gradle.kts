import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

group = "com.github.monosoul"
version = "0.0.1"

plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "0.10.1"
    `maven-publish`
    groovy
    jacoco
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    jcenter()
}

dependencies {
    testImplementation("org.spockframework", "spock-core", "1.3-groovy-2.5") {
        exclude("org.codehaus.groovy")
    }
}

tasks {
    "jacocoTestReport"(JacocoReport::class) {
        reports {
            xml.isEnabled = true
            html.isEnabled = false
        }

        val check by tasks
        check.dependsOn(this)
    }

    withType(Test::class) {
        useJUnit()

        testLogging {
            events = setOf(PASSED, SKIPPED, FAILED)
            exceptionFormat = FULL
        }
    }
}

gradlePlugin {
    plugins {
        create("yetAnotherDelombokGradlePlugin") {
            id = "com.github.monosoul.yadegrap"
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
    }
}