package com.commercehub.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by bmanley on 11/9/15.
 */
class GatlingPlugin implements Plugin<Project> {
    public static final String GATLING_EXTENSION_NAME = 'gatling'
    private Project project

    void apply(Project project) {
        this.project = project

        project.plugins.apply('scala')

        project.extensions.create(GATLING_EXTENSION_NAME, GatlingPluginExtension, project)
    }
}
