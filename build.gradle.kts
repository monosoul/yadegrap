import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED

group = "com.github.monosoul"
version = "1.0.0"

plugins {
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.15.0"
    `maven-publish`
    groovy
    jacoco
}

java {
    sourceCompatibility = VERSION_1_8
    targetCompatibility = VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.spockframework:spock-core:2.0-groovy-3.0") {
        exclude("org.codehaus.groovy")
    }
    testImplementation(gradleTestKit())
}

tasks {
    val jacocoTestReport = "jacocoTestReport"(JacocoReport::class) {
        reports {
            xml.required.set(true)
            html.required.set(false)
        }
    }

    "check" {
        dependsOn(jacocoTestReport)
    }

    withType<Test> {
        useJUnitPlatform()

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
            description = """
This plugin provides a configurable delombok task.
Compatibility table:
yadegrap | lombok
1.0.0    | >=1.16.4
0.0.1    | >=1.16.4
"""
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