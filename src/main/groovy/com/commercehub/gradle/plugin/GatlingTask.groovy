/*
 * Copyright (C) 2016 Commerce Technologies, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.commercehub.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import org.gradle.process.JavaForkOptions
import org.gradle.process.internal.DefaultJavaForkOptions

class GatlingTask extends DefaultTask {
    private final GatlingPluginExtension extension =
            project.extensions[GatlingPlugin.GATLING_EXTENSION_NAME] as GatlingPluginExtension

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
        (failBuild != null) ? failBuild : extension.failBuild
    }

    GatlingTaskMetricsConfig metrics = new GatlingTaskMetricsConfig(extension)

    @SuppressWarnings('ConfusingMethodName')
    def metrics(Closure closure) {
        closure.setDelegate this.metrics
        closure.call()
    }

    JavaForkOptions jvmOptions = new DefaultJavaForkOptions(project.fileResolver)

    @SuppressWarnings('ConfusingMethodName')
    def jvmOptions(Closure closure) {
        closure.setDelegate this.jvmOptions
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
        def gatlingRuntimeClasspath = getGatlingRuntimeClasspath()
        def extension = this.extension
        def gatlingSimulation = this.gatlingSimulation
        def gatlingLogFile = new File(project.buildDir, 'gatling.log')
        project.javaexec {
            jvmOptions.copyTo(delegate)
            standardInput = System.in
            standardOutput = gatlingLogFile.newOutputStream()
            main = 'io.gatling.app.Gatling'
            classpath = gatlingRuntimeClasspath

            jvmArgs "-Dgatling.core.directory.binaries=${sourceSet.output.classesDir}",
                    '-Xss1m'

            args '-df', extension.gatlingDataDir
            args '-rf', extension.gatlingReportsDir
            args '-bdf', extension.gatlingBodiesDir
            args '-s', gatlingSimulation
        }

        if (checkForKOs) {
            try {
                KoChecker.checkForKos(getKoThreshold(), gatlingLogFile)
            } catch (GatlingGradlePluginException e) {
                handleFailure("FAILED KO Check", e)
            }
        }
    }

    private void checkMetrics() {
        def metricIndex = 0;
        def hasThresholds = (metrics.thresholdsByMetricIndex != null);
        if (hasThresholds && (metrics.thresholdsByMetricIndex.size() != metrics.metricsToCheck.size())) {
            GatlingGradlePluginException error = new GatlingGradlePluginException("Number of Metrics to check" +
                "(${metrics.metricsToCheck.size()}) is not equivalent to number of thresholds to check " +
                "(${metrics.thresholdsByMetricIndex.size()}).");
            handleFailure("FAILED Pre Metric Check", error);
        }

        metrics.metricsToCheck.each { metricName ->
            project.logger.debug("Checking metric '${metricName.toString()}'.")
            try {
                MetricChecker.checkPreviousDays(metrics.graphiteUrl, metrics.metricPrefix, gatlingSimulation,
                        metricName, metrics.daysToCheck, metrics.degradationTolerance)
            } catch (GatlingGradlePluginException e) {
                handleFailure("FAILED Metric Check ($metricName)", e)
            }
            if (hasThresholds) {
                try {
                    MetricChecker.checkMaximumThresholdTolerance(metrics.graphiteUrl, metrics.metricPrefix, gatlingSimulation,
                            metricName, metrics.thresholdsByMetricIndex[metricIndex])
                } catch (GatlingGradlePluginException e) {
                    handleFailure("FAILED Metric Check ($metricName)", e)
                }
            }

            metricIndex = metricIndex + 1;
        }
    }

    private getGatlingRuntimeClasspath() {
        return getSourceSet().output + getSourceSet().runtimeClasspath
    }

    void handleFailure(String message, Throwable exception) {
        if (getFailBuild()) {
            throw new GradleException(message, exception)
        }
        project.logger.error(message)
    }

    class GatlingTaskMetricsConfig {
        private final GatlingPluginExtension extension

        GatlingTaskMetricsConfig(GatlingPluginExtension extension) {
            this.extension = extension
        }

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

        Number degradationTolerance

        Number getDegradationTolerance() {
            degradationTolerance ?: extension.metrics.degradationTolerance
        }

        def thresholdsByMetricIndex = []
    }
}
