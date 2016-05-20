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
    public static final String DEFAULT_GATLING_BODIES_DIR = 'user-files/request-bodies'
    public static final boolean DEFAULT_FAIL_BUILD = true
    public static final String DEFAULT_GATLING_CONF_FILE = 'config/gatling.conf'

    private final Project project

    GatlingPluginExtension(Project project) {
        this.project = project
    }

    def gatling(Closure closure) {
        closure.setDelegate this
        closure.call()
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
     * Set Gatling data directory
     */
    def gatlingDataDir

    File getGatlingDataDir() {
        project.file (gatlingDataDir ?: DEFAULT_GATLING_DATA_DIR)
    }

    /**
     * Set Gatling reports directory
     */
    def gatlingReportsDir

    File getGatlingReportsDir() {
        project.file (gatlingReportsDir ?: DEFAULT_GATLING_REPORTS_DIR)
    }

    /**
     * Set Gatling bodies directory
     */
    def gatlingBodiesDir

    File getGatlingBodiesDir() {
        project.file (gatlingBodiesDir ?: DEFAULT_GATLING_BODIES_DIR)
    }

    Boolean failBuild

    boolean getFailBuild() {
        (failBuild != null) ? failBuild : DEFAULT_FAIL_BUILD
    }

    /**
     * Set Gatling conf file location
     */
    def gatlingConfFile

    File getGatlingConfFile() {
        project.file (gatlingConfFile ?: DEFAULT_GATLING_CONF_FILE)
    }

    GatlingMetricsConfig metrics = new GatlingMetricsConfig()

    @SuppressWarnings('ConfusingMethodName')
    def metrics(Closure closure) {
        closure.setDelegate this.metrics
        closure.call()
    }
}
