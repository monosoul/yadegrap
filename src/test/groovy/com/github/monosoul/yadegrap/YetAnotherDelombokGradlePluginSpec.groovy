package com.github.monosoul.yadegrap

import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

class YetAnotherDelombokGradlePluginSpec extends Specification {

    @Shared
    def builder = ProjectBuilder.builder()
    def project = builder.build()

    void setup() {
        project.plugins.apply(YetAnotherDelombokGradlePlugin)
    }

    void cleanup() {
        project.rootDir.deleteDir()
    }

    def "should register the task when java plugin is applied"() {
        when:
            project.plugins.apply(JavaPlugin)
        then:
            project.tasks.findByName("delombok") != null
    }

    def "should not register the task when java plugin is not applied"() {
        expect:
            project.tasks.findByName("delombok") == null
    }
}
