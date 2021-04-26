import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED

group = "com.github.monosoul"
version = "0.0.1"

plugins {
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.14.0"
    `maven-publish`
    groovy
    jacoco
}

java {
    sourceCompatibility = VERSION_1_8
    targetCompatibility = VERSION_1_8
}

repositories {
    jcenter()
}

dependencies {
    testImplementation("org.spockframework", "spock-core", "1.3-groovy-2.5") {
        exclude("org.codehaus.groovy")
    }
    testImplementation(gradleTestKit())
}

tasks {
    named<JacocoReport>("jacocoTestReport") {
        executionData(
            fileTree(project.rootDir) {
                include("**/build/jacoco/*.exec")
            }
        )

        reports {
            xml.isEnabled = true
            xml.destination = File(buildDir, "reports/jacoco/report.xml")
            html.isEnabled = false
            csv.isEnabled = false
        }
    }

    withType<Test> {
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
            description = """
This plugin provides a configurable delombok task.
Compatibility table:
yadegrap | lombok
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