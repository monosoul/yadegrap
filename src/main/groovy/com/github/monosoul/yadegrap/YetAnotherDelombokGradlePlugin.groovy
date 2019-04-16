package com.github.monosoul.yadegrap

import org.gradle.api.Plugin
import org.gradle.api.Project

class YetAnotherDelombokGradlePlugin implements Plugin<Project> {
    @Override
    void apply(final Project project) {
        project.pluginManager.withPlugin('org.gradle.java') {
            project.tasks.register('delombok', DelombokTask)
        }
    }
}
