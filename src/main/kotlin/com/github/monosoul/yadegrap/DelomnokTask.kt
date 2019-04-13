package com.github.monosoul.yadegrap

import org.gradle.api.AntBuilder.AntMessagePriority.DEBUG
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.withGroovyBuilder
import java.io.File
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME as MAIN

const val DELOMBOK_ANT_TASK_CLASS = "lombok.delombok.ant.Tasks\$Delombok"

open class DelombokTask : DefaultTask() {
    init {
        group = "lombok"
        description = "Delomboks the source of ${project.name}."
    }

    private val main = (project.properties["sourceSets"] as SourceSetContainer).getByName<SourceSet>(MAIN)

    @InputDirectory
    var inputDir: File = main.java.srcDirs.first()

    @OutputDirectory
    var outputDir: File = File(project.buildDir, "delomboked")

    @InputFile
    var pathToLombok: File = main.compileClasspath
            .filter { it.name.startsWith("lombok") && it.name.endsWith(".jar") }
            .first()

    @Input
    var classPath: String = main.compileClasspath.asPath

    @Input
    @Optional
    var sourcePath: String? = null

    @Input
    @Optional
    var modulePath: String? = null

    @Input
    var encoding: String = "UTF-8"

    @Input
    var verbose: Boolean = false

    @Input
    var formatOptions: Map<String, String> = mapOf(
            "generateDelombokComment" to "skip",
            "generated" to "generate",
            "javaLangAsFQN" to "skip"
    )

    @TaskAction
    fun run() {
        if (verbose) {
            ant.lifecycleLogLevel = DEBUG
        }

        ant.withGroovyBuilder {
            "taskdef"(
                    "name" to name,
                    "classname" to DELOMBOK_ANT_TASK_CLASS,
                    "classpath" to pathToLombok)
            "mkdir"("dir" to outputDir)
            "delombok"(
                    "verbose" to verbose,
                    "encoding" to encoding,
                    "to" to outputDir,
                    "from" to inputDir
            ) {
                "classpath"("path" to classPath)
                sourcePath?.run {
                    "sourcepath"("path" to sourcePath)
                }
                modulePath?.run {
                    "modulepath"("path" to modulePath)
                }
                formatOptions.forEach {
                    "format"("value" to "${it.key}:${it.value}")
                }
            }
        }
    }
}