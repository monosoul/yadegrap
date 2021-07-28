# Yet Another Delombok Gradle Plugin (YADeGraP)
A plugin that provides a configurable Gradle task to perform delombok.

[![Build Status](https://github.com/monosoul/yadegrap/actions/workflows/build.yaml/badge.svg?branch=master)](https://github.com/monosoul/yadegrap/actions/workflows/build.yaml?query=branch%3Amaster)
[![codecov](https://codecov.io/gh/monosoul/yadegrap/branch/not-ant/graph/badge.svg)](https://codecov.io/gh/monosoul/yadegrap)
![GitHub release](https://img.shields.io/github/release/monosoul/yadegrap.svg)
![license](https://img.shields.io/github/license/monosoul/yadegrap.svg)

[Gradle Plugin Portal page](https://plugins.gradle.org/plugin/com.github.monosoul.yadegrap)

## Lombok compatibility table

| YADeGraP version | Lombok version |
|:----------------:|:--------------:|
| 1.0.0 | \>= 1.16.4 |
| 0.0.1 | \>= 1.16.4 |

## Getting Started

To apply the plugin simply add it to the plugins block of your build script:

```kotlin
plugins {
    id("com.github.monosoul.yadegrap") version "1.0.0"
}
```

Or using legacy plugin application method:

```kotlin
buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("gradle.plugin.com.github.monosoul:yadegrap:1.0.0")
    }
}

apply(plugin = "com.github.monosoul.yadegrap")
```

### Prerequisites

You should have `java` or `java-library` plugin applied (or any other plugin that applies `java` plugin internally). **
If your build script will not have `java` plugin applied, then the `delombok` task would not appear.**

To use the plugin you have to have [lombok](https://projectlombok.org) version `1.16.4` or higher in your project's
compile classpath.

Also, you'll need Gradle. :)

### Usage example

A build script example:

```kotlin
plugins {
    java
    id("com.github.monosoul.yadegrap") version "1.0.0"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.20")
}
```

After you applied the plugin, a task named `delombok` would appear under the `lombok` group, so would be able to call it
like that:

```bash
./gradlew delombok
```

Example of task dependencies:

```kotlin
tasks {
    val delombok = "delombok"(DelombokTask::class)

    "javadoc"(Javadoc::class) {
        dependsOn(delombok)
        setSource(delombok)
    }
}
```

This way `javadoc` task would depend on `delombok` task, and delomboked sources would be used for the javadoc
generation.
<br>
<br>
Basically it should be enough to use it, as it would try to configure itself automatically. But if it fails to do so, or
you need to configure it yourself, then you have the following options.

### Configuration options

| Option | Type | Description |
|:-------|:----:|:------------|
| inputDir | Directory | Path to the sources that should be delomboked.<br>By default uses the first source directory of `main`'s java sources. |
| outputDir | Directory | Path to the destination directory where delomboked sources are going to be placed.<br>By default uses "delomboked" dir in the project's `buildDir`. |
| pathToLombok | RegularFile | Path to lombok.jar.<br>By default tries to find it in the `main`'s compile classpath. |
| classPath | String | Class path to be used during the execution of delombok task. Should have all the classes used by the sources which are going to be delomboked.<br>By default uses `main`'s compile classpath. |
| bootClassPath | String | Boot class path to be used during the execution of delmbok task. |
| sourcePath | String | Source path to be used during the execution of delmbok task. |
| modulePath | String | Module path to be used during the execution of delmbok task. |
| encoding | String | Files' encoding.<br>Default: UTF-8 |
| verbose | Boolean | Logging verbosity.<br>Default: false |
| formatOptions | Map<String, String> | Formatting options for the delombok task.<br>By default is empty.<br>[List of available format options](https://github.com/rzwitserloot/lombok/blob/master/src/delombok/lombok/delombok/FormatPreferences.java#L42) |

Example of task configuration:

```kotlin
tasks {
    "delombok"(DelombokTask::class) {
        verbose.set(true)
        formatOptions.set(
            mapOf(
                "generateDelombokComment" to "skip",
                "generated" to "generate",
                "javaLangAsFQN" to "skip"
            )
        )
    }
}
```

This way the delombok task would be called with verbose output and specified format options.

## License

The software is licensed under the [Apache-2.0 License](LICENSE).