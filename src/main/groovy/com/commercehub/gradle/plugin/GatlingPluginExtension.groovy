package com.commercehub.gradle.plugin

import org.gradle.api.Project

/**
 * Created by bmanley on 11/9/15.
 */
class GatlingPluginExtension {

    private final Project project

    GatlingPluginExtension(Project project) {
        this.project = project
    }

    /**
     * Set gatling version
     */
    String gatlingVersion = '2.2.0-M3'

    /**
     * Set whether or not the gatling task will check for KOed requests
     */
    boolean checkForKOs = true

    /**
     * The number of KOs allowed before a build is considered failed
     */
    int koThreshold = 0

    /**
     * Set number of days to go back when checking stats of previous builds
     */
    int numberOfDaysToCheck = 0

    /**
     * The base graphite url
     */
    String graphiteUrl = ""

    /**
     * Set Gatling data directory
     */
    String gatlingDataFolder = "${project.rootDir.absolutePath}/data"

    /**
     * Set Gatling reports directory
     */
    String gatlingReportsFolder = "${project.buildDir.absolutePath}/reports"

    /**
     * Set Gatling bodies directory
     */
    String gatlingBodiesFolder = "${project.rootDir.absolutePath}/bodies"

    /**
     * Set Gatling simulations to run
     */
    def gatlingSimulation = []

    /**
     * Set Graphite metrics to check
     */
    def metricsToCheck = []

    /**
     * Set Gatling conf file location
     */
    String gatlingConfFile = "${project.rootDir.absolutePath}/resources/gatling.conf"
}
