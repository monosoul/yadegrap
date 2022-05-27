package com.github.monosoul.yadegrap


import org.gradle.api.plugins.JavaPlugin
import org.gradle.internal.impldep.com.google.common.io.Files
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

class DelombokTaskSpec extends Specification {

    @Shared
    def lombokVersion = "1.18.24"
    @Shared
    def project = ProjectBuilder.builder().build()
    @Shared
    DelombokTask task

    void setupSpec() {
        project.plugins.apply(JavaPlugin)
        project.repositories.mavenCentral()
        project.dependencies.add("compileOnly", "org.projectlombok:lombok:$lombokVersion")
        task = project.tasks.create("delombok", DelombokTask) { DelombokTask t ->
            t.formatOptions.set(
                    [
                            "generateDelombokComment": "skip",
                            "suppressWarnings"       : "skip"
                    ]
            )
        }
    }

    void cleanupSpec() {
        project.rootDir.deleteDir()
    }

    def "should autodetect properties"() {
        expect:
            task.inputDir.asFile.get() == project.file("src/main/java")
            task.outputDir.asFile.get() == project.file("build/delomboked")
            task.pathToLombok.present
            task.pathToLombok.asFile.get().name == "lombok-${lombokVersion}.jar"
            task.classPath.get() == project.sourceSets.main.compileClasspath.asPath
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
        and:
            def delombokedFileContents = delombokedFile.text
            def expectedDelombokedFileContents = expectedDelombokedFile.text
            delombokedFileContents == expectedDelombokedFileContents
    }
}
