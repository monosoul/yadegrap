package com.github.monosoul.yadegrap

import lombok.launch.DelombokWrapper
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel.LIFECYCLE
import org.gradle.api.tasks.*
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.gradle.kotlin.dsl.getByName
import java.io.File
import java.io.PrintStream
import java.net.URLClassLoader

/**
 * Task that preforms delombok operation on the provided sources.
 */
open class DelombokTask : DefaultTask() {
    init {
        group = "lombok"
        description = "Delomboks the source of ${project.name}."
    }

    private val Project.sourceSets
        get() = properties["sourceSets"] as SourceSetContainer
    private val SourceSetContainer.main
        get() = getByName<SourceSet>(MAIN_SOURCE_SET_NAME)

    /**
     * Path to sources that should be delomboked.
     *
     * By default uses the first source directory of main's java sources.
     */
    @InputDirectory
    var inputDir: File = project.sourceSets.main.java.srcDirs.first()

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
    var pathToLombok: File? = project.sourceSets.main.compileClasspath.singleOrNull {
        it.name.startsWith("lombok") && it.extension == "jar"
    }

    /**
     * Class path to be used during the execution of delombok task. Should have all the classes used by the sources
     * which are going to be delomboked.
     *
     * By default uses main's compile classpath.
     */
    @Input
    var classPath: String = project.sourceSets.main.compileClasspath.asPath

    /**
     * Boot class path to be used during the execution of delmbok task.
     */
    @Input
    @Optional
    var bootClassPath: String? = null

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

    private val loggingPrintStream = object : PrintStream(System.out) {
        override fun println(x: String?) {
            x?.run {
                log(x)
            }
        }

        override fun print(f: String?) {
            f?.run {
                log(f)
            }
        }

        override fun printf(format: String?, vararg args: Any?): PrintStream {
            format?.run {
                log(format(*args))
            }
            return this
        }

        private fun log(msg: String) {
            logger.log(LIFECYCLE, msg)
        }
    }

    @TaskAction
    fun run() {
        val delombokWrapper = DelombokWrapper(
                URLClassLoader(
                        //gradle wouldn't allow pathToLombok to be null
                        arrayOf(pathToLombok!!.toURI().toURL())
                )
        )

        delombokWrapper
                .addDirectory(inputDir)
                .setOutput(outputDir)
                .setEncoding(encoding)
                .setVerbose(verbose)
                .setFormatPreferences(
                        formatOptions.map { it.key.toLowerCase() to it.value.toLowerCase() }.toMap()
                )
                .setClasspath(classPath)
        bootClassPath?.let {
            delombokWrapper.setBootclasspath(it)
        }
        sourcePath?.let {
            delombokWrapper.setSourcepath(it)
        }
        modulePath?.let {
            delombokWrapper.setModulepath(it)
        }

        delombokWrapper.setPrintStream(loggingPrintStream)
        delombokWrapper.delombok()
    }
}