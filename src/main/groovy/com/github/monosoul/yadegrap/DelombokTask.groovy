package com.github.monosoul.yadegrap

import lombok.launch.DelombokWrapper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

import static org.gradle.api.logging.LogLevel.LIFECYCLE
import static org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME

/**
 * Task that preforms delombok operation on the provided sources.
 */
class DelombokTask extends DefaultTask {

    DelombokTask() {
        this.group = 'lombok'
        this.description = "Delomboks the source of ${project.name}."
    }

    private final main = (project.properties['sourceSets'] as SourceSetContainer).getByName(MAIN_SOURCE_SET_NAME)

    /**
     * Path to sources that should be delomboked.
     * <br>
     * By default uses the first source directory of main's java sources.
     */
    @InputDirectory
    File inputDir = main.java.srcDirs.first()

    /**
     * Path to the destination directory where delomboked sources are going to be placed.
     * <br>
     * By default uses "delomboked" dir in the project's buildDir.
     */
    @OutputDirectory
    File outputDir = new File(project.buildDir, 'delomboked')

    /**
     * Path to lombok.jar.
     * <br>
     * By default tries to find it in the main's compile classpath.
     */
    @InputFile
    File pathToLombok = main.compileClasspath.findResult {
        it.name.startsWith('lombok') && it.name.endsWith('jar') ? it : null
    }

    /**
     * Class path to be used during the execution of delombok task. Should have all the classes used by the sources
     * which are going to be delomboked.
     * <br>
     * By default uses main's compile classpath.
     */
    @Input
    String classPath = main.compileClasspath.asPath

    /**
     * Boot class path to be used during the execution of delmbok task.
     */
    @Input
    @Optional
    String bootClassPath = null

    /**
     * Source path to be used during the execution of delmbok task.
     */
    @Input
    @Optional
    String sourcePath = null

    /**
     * Module path to be used during the execution of delmbok task.
     */
    @Input
    @Optional
    String modulePath = null

    /**
     * Files' encoding.
     * <br>
     * Default: UTF-8
     */
    @Input
    String encoding = "UTF-8"

    /**
     * Logging verbosity.
     * <br>
     * Default: false
     */
    @Input
    Boolean verbose = false

    /**
     * Formatting options for the delombok task.
     * <br>
     * By default is empty.
     */
    @Input
    Map<String, String> formatOptions = [:]

    private final loggingPrintStream = new PrintStream(System.out, true) {
        @Override
        void print(final String s) {
            log(s)
        }

        @Override
        void println(final String x) {
            log(x)
        }

        @Override
        PrintStream printf(final String format, final Object... args) {
            log(String.format(format, args))
            return this
        }

        private log(String msg) {
            logger.log(LIFECYCLE, msg)
        }
    }

    @TaskAction
    def run() {
        def delombokWrapper = new DelombokWrapper(
                new URLClassLoader(
                        pathToLombok.toURI().toURL()
                )
        )

        delombokWrapper
                .addDirectory(inputDir)
                .setOutput(outputDir)
                .setEncoding(encoding)
                .setVerbose(verbose)
                .setFormatPreferences(
                        formatOptions.collectEntries {
                            key, value -> [key.toLowerCase(), value.toLowerCase()]
                        } as Map<String, String>
                )
                .setClasspath(classPath)
                .setPrintStream(loggingPrintStream)
        if (bootClassPath != null)
            delombokWrapper.setBootclasspath(bootClassPath)
        if (sourcePath != null)
            delombokWrapper.setSourcepath(sourcePath)
        if (modulePath != null)
            delombokWrapper.setModulepath(modulePath)

        delombokWrapper.delombok()
    }
}
