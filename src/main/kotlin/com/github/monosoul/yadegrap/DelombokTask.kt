package com.github.monosoul.yadegrap

import org.gradle.api.AntBuilder.AntMessagePriority.DEBUG
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.withGroovyBuilder
import java.io.File
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME as MAIN

/**
 * Task that preforms delombok operation on the provided sources.
 */
open class DelombokTask : DefaultTask() {
    init {
        group = "lombok"
        description = "Delomboks the source of ${project.name}."
    }

    private val main = (project.properties["sourceSets"] as SourceSetContainer).getByName<SourceSet>(MAIN)

    /**
     * Path to sources that should be delomboked.
     *
     * By default uses the first source directory of main's java sources.
     */
    @InputDirectory
    var inputDir: File = main.java.srcDirs.first()

    /**
     * Path to the destination directory where delomboked sources are going to be placed.
     *
     * By default uses "delomboked" dir in the project's buildDir.
     */
    @OutputDirectory
    var outputDir: File = File(project.buildDir, "delomboked")

    /**
     * Path to lombok.jar.
     *
     * By default tries to find it in the main's compile classpath.
     */
    @InputFile
    var pathToLombok: File? = main.compileClasspath
            .filter { it.name.startsWith("lombok") && it.extension == "jar" }
            .singleOrNull()

    /**
     * Class path to be used during the execution of delmbok task. Should have all the classes used by the sources
     * which are going to be delomboked.
     *
     * By default uses main's compile classpath.
     */
    @Input
    var classPath: String = main.compileClasspath.asPath

    /**
     * Source path to be used during the execution of delmbok task.
     */
    @Input
    @Optional
    var sourcePath: String? = null

    /**
     * Module path to be used during the execution of delmbok task.
     */
    @Input
    @Optional
    var modulePath: String? = null

    /**
     * Files' encoding.
     *
     * Default: UTF-8
     */
    @Input
    var encoding: String = "UTF-8"

    /**
     * Logging verbosity.
     *
     * Default: false
     */
    @Input
    var verbose: Boolean = false

    /**
     * Formatting options for the delombok task.
     *
     * By default is empty.
     */
    @Input
    var formatOptions: Map<String, String> = mapOf()

    @TaskAction
    fun run() {
        if (verbose) {
            ant.lifecycleLogLevel = DEBUG
        }

        ant.withGroovyBuilder {
            "taskdef"(
                    "name" to name,
                    "classname" to "lombok.delombok.ant.Tasks\$Delombok",
                    "classpath" to pathToLombok)
            "mkdir"("dir" to outputDir)
            name(
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