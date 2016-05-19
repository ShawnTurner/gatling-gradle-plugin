package com.commercehub.gradle.plugin

import org.gradle.api.Project

/**
 * Created by bmanley on 11/9/15.
 */
class GatlingPluginExtension {
    public static final String DEFAULT_GATLING_VERSION = '2.2.0-M3'
    public static final boolean DEFAULT_CHECK_FOR_KOS = true
    public static final int DEFAULT_KO_THRESHOLD = 0
    public static final int DEFAULT_DAYS_TO_CHECK = 0
    public static final String DEFAULT_GATLING_DATA_DIR = 'data'
    public static final String DEFAULT_GATLING_REPORTS_DIR = 'reports'
    public static final String DEFAULT_GATLING_BODIES_DIR = 'bodies'

    private final Project project

    GatlingPluginExtension(Project project) {
        this.project = project
    }

    def gatling(Closure closure) {
        closure.setDelegate this
        closure.call()
    }

    /**
     * gatling version
     */
    String gatlingVersion

    String getGatlingVersion() {
        gatlingVersion ?: DEFAULT_GATLING_VERSION
    }

    /**
     * Set whether or not the gatling task will check for KOed requests
     */
    Boolean checkForKOs

    boolean getCheckForKOs() {
        checkForKOs ?: DEFAULT_CHECK_FOR_KOS
    }

    /**
     * The number of KOs allowed before a build is considered failed
     */
    int koThreshold

    int getKoThreshold() {
        koThreshold ?: DEFAULT_KO_THRESHOLD
    }

    /**
     * Set number of days to go back when checking stats of previous builds
     */
    int numberOfDaysToCheck

    int getNumberOfDaysToCheck() {
        numberOfDaysToCheck ?: DEFAULT_DAYS_TO_CHECK
    }

    /**
     * The base graphite url
     */
    String graphiteUrl

    /**
     * Set Gatling data directory
     */
    def gatlingDataDir

    File getGatlingDataDir() {
        project.file gatlingDataDir ?: DEFAULT_GATLING_DATA_DIR
    }

    /**
     * Set Gatling reports directory
     */
    def gatlingReportsDir

    File getGatlingReportsDir() {
        project.file gatlingReportsDir ?: DEFAULT_GATLING_REPORTS_DIR
    }

    /**
     * Set Gatling bodies directory
     */
    def gatlingBodiesDir

    File getGatlingBodiesDir() {
        project.file gatlingBodiesDir ?: DEFAULT_GATLING_BODIES_DIR
    }

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

    /**
     * Set the the prefix for the published graphite metrics
     */
    String graphiteMetricPrefix = null
}
