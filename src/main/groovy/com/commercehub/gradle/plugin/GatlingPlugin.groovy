package com.commercehub.gradle.plugin

import groovy.util.logging.Slf4j
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by bmanley on 11/9/15.
 */
@Slf4j
class GatlingPlugin implements Plugin<Project> {
    private Project project

    // Dates used when checking graphite
    Date from
    Date until

    int daysToCheck
    String errorMessage

    void apply(Project project) {
        this.project = project

        project.plugins.apply('scala')

        project.extensions.create('gatlingTest', GatlingPluginExtension, project)

        project.dependencies {
            testCompile "io.gatling.highcharts:gatling-charts-highcharts:${getGatlingVersion()}",
                    'com.nimbusds:nimbus-jose-jwt:2.22.1'
        }
        project.repositories {
            maven {
                url 'http://repository.excilys.com/content/groups/public'
                url 'https://oss.sonatype.org/content/repositories/snapshots'
            }
        }

        project.task('gatlingTest', dependsOn: ['processTestResources', 'compileTestScala']) << {
            final def SOURCE_SET = project.sourceSets.test
            final def GATLING_CLASSPATH = SOURCE_SET.output + SOURCE_SET.runtimeClasspath

            log.debug("Source Set: ${SOURCE_SET}")
            log.debug("Gatling Classpath: ${GATLING_CLASSPATH}")

            errorMessage = ""

            setupTaskProperties()

            getGatlingSimulation().each {
                String scenario = it

                log.info("Executing scenario '${scenario}'.")
                log.debug("Gatling data folder: ${getGatlingDataFolder()}")
                log.debug("Gatling reports folder: ${getGatlingReportsFolder()}")
                log.debug("Gatling bodies folder: ${getGatlingBodiesFolder()}")

                GatlingTask.run(project, scenario, GATLING_CLASSPATH)

                checkResults(scenario)
            }

            // Throw exception if there were any errors
            if (errorMessage != "") {
                log.error(errorMessage)
                throw new GradleException(errorMessage)
            }
        }
    }

    /**
     * Set up properties for the gatling test task
     */
    void setupTaskProperties() {
        daysToCheck = getNumberOfDaysToCheck()

        if (getNumberOfDaysToCheck() != null && getNumberOfDaysToCheck() < 0) {
            log.error("Number of days must be a positive integer.")
            throw new GradleException("Number of days to check must be a positive integer.")

        } else if (getNumberOfDaysToCheck() == 0) {
            log.info("Not checking previous response times.")
        }

        if (getCheckForKOs()) {
            from = new Date()
            log.debug("From date: ${from}")
        }
    }

    /**
     * Verify all pass/failure conditions specified
     * @param scenario the scenario to look up in graphite
     */
    void checkResults(String scenario) {
        if (daysToCheck > 0) {
            def metrics = getMetricsToCheck()

            metrics.each {
                log.debug("Checking metric '${it.toString()}'.")

                try {
                    MetricChecker.checkPreviousDays(getBaseUrl(), scenario.toLowerCase(), it.toString(),
                            getNumberOfDaysToCheck())

                } catch (GatlingGradlePluginException e) {
                    errorMessage += e.getMessage()
                    log.error("Error checking metric '${it.toString()}': ${e.getMessage()}")
                }
            }
        }

        if (getCheckForKOs()) {
            until = new Date() //new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse("2015-11-3 11:40:00.0")
            log.debug("Until date: ${until}")

            try {
                MetricChecker.detectFailedRequestQualityGate(getBaseUrl(), scenario.toLowerCase(), from, until,
                        getKoThreshold())
            } catch (GatlingGradlePluginException e) {
                errorMessage += e.getMessage()
            }
        }
    }

    boolean getCheckForKOs() {
        return project.gatlingTest.checkForKOs
    }

    int getKoThreshold() {
        return project.gatlingTest.koThreshold
    }

    int getNumberOfDaysToCheck() {
        return project.gatlingTest.numberOfDaysToCheck
    }

    String getBaseUrl() {
        return project.gatlingTest.graphiteUrl
    }

    String getGatlingDataFolder() {
        return project.gatlingTest.gatlingDataFolder
    }

    String getGatlingReportsFolder() {
        return project.gatlingTest.gatlingReportsFolder
    }

    String getGatlingBodiesFolder() {
        return project.gatlingTest.gatlingBodiesFolder
    }

    def getGatlingSimulation() {
        return project.gatlingTest.gatlingSimulation
    }

    def getMetricsToCheck() {
        return project.gatlingTest.metricsToCheck
    }

    String getGatlingConfFile() {
        return project.gatlingTest.gatlingConfFile
    }

    String getGatlingVersion() {
        return project.gatlingTest.gatlingVersion
    }
}
