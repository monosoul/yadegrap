import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    val lombokDependency = "org.projectlombok:lombok:1.18.2"
    compileOnly(lombokDependency)
    testCompileOnly(lombokDependency)
    testImplementation("org.spockframework", "spock-core", "1.3-groovy-2.5") {
        exclude("org.codehaus.groovy")
    }
}

tasks {
    val compileGroovy = getByName("compileGroovy", GroovyCompile::class) {
        dependsOn.remove("compileJava")
    }
    "compileKotlin"(KotlinCompile::class) {
        dependsOn(compileGroovy)

        classpath += files(compileGroovy.destinationDir)
    }

    "jacocoTestReport"(JacocoReport::class) {
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