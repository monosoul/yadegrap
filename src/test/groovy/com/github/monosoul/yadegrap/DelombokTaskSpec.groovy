package com.github.monosoul.yadegrap

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.internal.impldep.com.google.common.io.Files
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

class DelombokTaskSpec extends Specification {

    @Shared
    def lombokVersion = "1.18.2"
    @Shared
    def builder = ProjectBuilder.builder()
    @Shared
    Project project
    @Shared
    DelombokTask task

    void setupSpec() {
        project = builder.build()
        project.plugins.apply(JavaPlugin)
        project.repositories.jcenter()
        project.dependencies.add("compileOnly", "org.projectlombok:lombok:$lombokVersion")
        task = project.tasks.create("delombok", DelombokTask) { DelombokTask t ->
            t.formatOptions = [
                    "generateDelombokComment": "skip",
                    "suppressWarnings": "skip"
            ]
        }
    }

    void cleanupSpec() {
        project.rootDir.deleteDir()
    }

    def "should autodetect properties"() {
        expect:
            task.inputDir == project.file("src/main/java")
            task.outputDir == project.file("build/delomboked")
            task.pathToLombok.exists()
            task.pathToLombok.name == "lombok-${lombokVersion}.jar"
            task.classPath == project.sourceSets.main.compileClasspath.asPath
    }

    def "should perform delombok"() {
        setup:
            def somePojoFileName = "SomePojo.java"
        and:
            def somePojoFile = new File(getClass().getResource("/$somePojoFileName").toURI())
            project.mkdir("src/main/java")
            def target = project.file("src/main/java/$somePojoFileName")
            Files.copy(somePojoFile, target)
        and:
            def delombokedFile = project.file("build/delomboked/$somePojoFileName")
            def expectedDelombokedFile = new File(getClass().getResource("/delomboked/$somePojoFileName").toURI())
        when:
            task.run()
        then:
            delombokedFile.exists()
            delombokedFile.readLines() == expectedDelombokedFile.readLines()
    }
}
