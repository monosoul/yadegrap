package com.github.monosoul.yadegrap

import lombok.launch.DelombokWrapper
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

import static org.gradle.api.logging.LogLevel.LIFECYCLE
import static org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME

/**
 * Task that preforms delombok operation on the provided sources.
 */
class DelombokTask extends DefaultTask {

    private ObjectFactory objectFactory
    private ProviderFactory providerFactory

    @Inject
    DelombokTask(
            ObjectFactory objectFactory,
            ProviderFactory providerFactory
    ) {
        this.group = 'lombok'
        this.description = "Delomboks the source of ${project.name}."
        this.objectFactory = objectFactory
        this.providerFactory = providerFactory
    }

    /**
     * Path to sources that should be delomboked.
     * <br>
     * By default uses the first source directory of main's java sources.
     */
    @InputDirectory
    final DirectoryProperty inputDir = objectFactory.directoryProperty().convention(
            project.layout.dir(
                    getMain(project).map { sourceSet ->
                        sourceSet.java.sourceDirectories.first()
                    }
            )
    )

    /**
     * Path to the destination directory where delomboked sources are going to be placed.
     * <br>
     * By default uses "delomboked" dir in the project's buildDir.
     */
    @OutputDirectory
    final DirectoryProperty outputDir = objectFactory.directoryProperty().convention(
            project.layout.buildDirectory.dir('delomboked')
    )

    /**
     * Path to lombok.jar.
     * <br>
     * By default tries to find it in the main's compile classpath.
     */
    @InputFile
    final RegularFileProperty pathToLombok = objectFactory.fileProperty().convention(
            project.layout.file(
                    getMain(project).map { sourceSet ->
                        sourceSet.compileClasspath.findResult {
                            it.name.startsWith('lombok') && it.name.endsWith('jar') ? it : null
                        }
                    }
            )
    )

    /**
     * Class path to be used during the execution of delombok task. Should have all the classes used by the sources
     * which are going to be delomboked.
     * <br>
     * By default uses main's compile classpath.
     */
    @Input
    final Property<String> classPath = objectFactory.property(String).convention(
            getMain(project).map { it.compileClasspath.asPath }
    )

    /**
     * Boot class path to be used during the execution of delmbok task.
     */
    @Input
    @Optional
    final Property<String> bootClassPath = objectFactory.property(String)

    /**
     * Source path to be used during the execution of delmbok task.
     */
    @Input
    @Optional
    final Property<String> sourcePath = objectFactory.property(String)

    /**
     * Module path to be used during the execution of delmbok task.
     */
    @Input
    @Optional
    final Property<String> modulePath = objectFactory.property(String)

    /**
     * Files' encoding.
     * <br>
     * Default: UTF-8
     */
    @Input
    final Property<String> encoding = objectFactory.property(String).convention('UTF-8')

    /**
     * Logging verbosity.
     * <br>
     * Default: false
     */
    @Input
    final Property<Boolean> verbose = objectFactory.property(Boolean).convention(false)

    /**
     * Formatting options for the delombok task.
     * <br>
     * By default is empty.
     */
    @Input
    final MapProperty<String, String> formatOptions = objectFactory.mapProperty(String, String).convention([:])

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
                        pathToLombok.asFile.get().toURI().toURL()
                )
        )

        delombokWrapper
                .addDirectory(inputDir.asFile.get())
                .setOutput(outputDir.asFile.get())
                .setEncoding(encoding.get())
                .setVerbose(verbose.get())
                .setFormatPreferences(
                        formatOptions.get().collectEntries {
                            key, value -> [key.toLowerCase(), value.toLowerCase()]
                        } as Map<String, String>
                )
                .setClasspath(classPath.get())
                .setPrintStream(loggingPrintStream)
        if (bootClassPath.present)
            delombokWrapper.setBootclasspath(bootClassPath.get())
        if (sourcePath.present)
            delombokWrapper.setSourcepath(sourcePath.get())
        if (modulePath.present)
            delombokWrapper.setModulepath(modulePath.get())

        delombokWrapper.delombok()
    }

    private static Provider<SourceSet> getMain(Project project) {
        return (project.properties['sourceSets'] as SourceSetContainer).named(MAIN_SOURCE_SET_NAME)
    }
}
