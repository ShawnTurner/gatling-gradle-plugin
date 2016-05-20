package com.commercehub.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction

/**
 * Created by bmanley on 11/16/15.
 */
class GatlingTask extends DefaultTask {
    private final GatlingPluginExtension extension = project.extensions[GatlingPlugin.GATLING_EXTENSION_NAME]

    /**
     * Gatling simulations to run
     */
    String gatlingSimulation

    /**
     * SourceSet containing simulations and dependencies
     */
    SourceSet sourceSet

    SourceSet getSourceSet() {
        sourceSet ?: project.sourceSets.test
    }

    /**
     * Set whether or not the gatling task will check for KOed requests
     */
    Boolean checkForKOs

    boolean getCheckForKOs() {
        checkForKOs ?: extension.checkForKOs
    }

    /**
     * The number of KOs allowed before a build is considered failed
     */
    int koThreshold

    int getKoThreshold() {
        koThreshold ?: extension.koThreshold
    }

    Boolean failBuild

    boolean getFailBuild() {
        failBuild ?: extension.failBuild
    }

    GatlingTaskMetricsConfig metrics = new GatlingTaskMetricsConfig()

    @SuppressWarnings('ConfusingMethodName')
    def metrics(Closure closure) {
        closure.setDelegate this.metrics
        closure.call()
    }

    @TaskAction
    void runSimulations() {
        validateConfig()
        runGatling()
        checkMetrics()
    }

    private void validateConfig() {
        if (!metrics.metricsToCheck.isEmpty() && !metrics.graphiteUrl) {
            throw new GradleException('You must supply a graphiteUrl if you wish to check metrics.')
        }
    }

    private void runGatling() {
        project.javaexec {
            standardInput = System.in
            standardOutput = new File(project.buildDir, 'gatling.log').newOutputStream()
            main = 'io.gatling.app.Gatling'
            classpath = gatlingRuntimeClasspath

            jvmArgs '-Dgatling.core.directory.binaries=./build/classes/test',
                    '-Xmx1024M',
                    '-Xms1024M',
                    '-Xss1m'

            args '-df', extension.gatlingDataDir
            args '-rf', extension.gatlingReportsDir
            args '-bdf', extension.gatlingBodiesDir
            args '-s', gatlingSimulation
        }

        if (checkForKOs) {
            try {
                MetricChecker.detectFailedRequestQualityGate(getKoThreshold())
            } catch (GatlingGradlePluginException e) {
                handleFailure("", e)
            }
        }
    }

    private void checkMetrics() {
        metrics.metricsToCheck.each { metricName ->
            project.logger.debug("Checking metric '${it.toString()}'.")

            try {
                MetricChecker.checkPreviousDays(metrics.graphiteUrl, metrics.metricPrefix, gatlingSimulation,
                        metricName, metrics.daysToCheck)

            } catch (GatlingGradlePluginException e) {
                handleFailure("", e)
            }
        }
    }

    private getGatlingRuntimeClasspath() {
        return sourceSet.output + sourceSet.runtimeClasspath
    }

    private void handleFailure(message, exception) {
        if (failBuild) {
            throw new GradleException(message, exception)
        }
        project.logger.error(message, exception)
    }

    class GatlingTaskMetricsConfig {

        def metricsToCheck = []

        String graphiteUrl

        String getGraphiteUrl() {
            graphiteUrl ?: extension.metrics.graphiteUrl
        }

        String metricPrefix

        String getMetricPrefix() {
            metricPrefix ?:  extension.metrics.metricPrefix
        }

        Integer daysToCheck

        int getDaysToCheck() {
            daysToCheck ?: extension.metrics.daysToCheck
        }
    }
}
