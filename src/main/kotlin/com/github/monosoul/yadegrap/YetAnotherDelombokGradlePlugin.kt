package com.github.monosoul.yadegrap

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class YetAnotherDelombokGradlePlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = project.run {

        pluginManager.withPlugin("org.gradle.java") {
            tasks {
                register("delombok", DelombokTask::class)
            }
        }
    }
}