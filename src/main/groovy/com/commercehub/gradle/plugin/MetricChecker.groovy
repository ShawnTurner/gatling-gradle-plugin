package com.commercehub.gradle.plugin

import groovy.json.JsonSlurper

import java.text.SimpleDateFormat

/**
 * Created by bmanley on 11/9/15.
 */
class MetricChecker {
    private static final String GATLING_PREFIX = "gatling."

    private static getJsonFromUrl(String url) {
        URL apiUrl = new URL(url)

        return new JsonSlurper().parse(apiUrl)
    }

    /**
     * Verify that the current mean response time for the given metric is not over 20% greater than the average mean response
     * time for the past given number of days.
     *
     * @param baseUrl base graphite url
     * @param scenario the scenario to check
     * @param metricName the metric to check (case sensitive with underscores instead of spaces ie 'Get_by_Organization_id')
     * @param numberOfDays the number of days to go back
     */
    static void checkPreviousDays(String baseUrl, String graphitePrefix, String scenario,
                                  String metricName, int numberOfDays, Number degradationTolerance) {
        final long DAYS_IN_MS = numberOfDays * 1000 * 60 * 60 * 24
        final Date START_DATE = new Date(System.currentTimeMillis() - DAYS_IN_MS).clearTime()

        String apiPrefix = '/render/?target='
        String metric = buildMetricPath(graphitePrefix, scenario, metricName)
        String format = "&format=json"
        String from = "&from=${new SimpleDateFormat("HH:mm_yyyyMMdd", Locale.US).format(START_DATE)}"

        // Get json from Graphite API
        String apiString = baseUrl + apiPrefix + metric + format + from
        def json = getJsonFromUrl(apiString)

        if (json.size > 0) {

            // Get list of datapoints
            def datapoints = json.datapoints.get(0)

            // Filter nulls
            def meanTimes = filterNulls(datapoints)

            if (meanTimes.size() > 0) {

                // Get the most recent response time
                def currentResponseTime = meanTimes.last()

                // Remove the most recent response time from the list
                meanTimes.dropRight(1)

                // Calculate the average response time
                def averageResponseTime = AverageCalculator.calculateAverageResponseTime(meanTimes)

                // Calculate maximum acceptable response time
                def acceptanceThreshold = averageResponseTime * (1.0 + degradationTolerance)

                if (currentResponseTime > acceptanceThreshold) {
                    throw new GatlingGradlePluginException(
                            "Current response time ${currentResponseTime} exceeds the tolerance for performance " +
                                    "degradation (${degradationTolerance}) for ${metricName}.\n")
                }
            }
        }
    }

    /**
     * Verify that the current mean response time for the given metric is not over a given maximum threshold
     * specfied by the test.
     *
     * @param baseUrl base graphite url
     * @param scenario the scenario to check
     * @param metricName the metric to check (case sensitive with underscores instead of spaces ie 'Get_by_Organization_id')
     * @param maximumPerformanceThreshold the maximum amount of time(ms) you expect this test to run
     */

    static void checkMaximumThresholdTolerance(String baseUrl, String graphitePrefix, String scenario,
                                               String metricName, Number maximumPerformanceThreshold) {
        String apiPrefix = '/render/?target='
        String metric = buildMetricPath(graphitePrefix, scenario, metricName)
        String format = "&format=json"
        String from = "&from=today"

        // Get json from Graphite API
        String apiString = baseUrl + apiPrefix + metric + format + from
        def json = getJsonFromUrl(apiString)
        if (json.size > 0) {

            // Get list of datapoints
            def datapoints = json.datapoints.get(0)

            // Filter nulls
            def meanTimes = filterNulls(datapoints)

            if (meanTimes.size() > 0) {

                // Get the most recent response time
                def currentResponseTime = meanTimes.last()
                if (currentResponseTime > maximumPerformanceThreshold) {
                    throw new GatlingGradlePluginException(
                            "Current response time ${currentResponseTime} exceeds the tolerance for minimum " +
                                    "performance threshold (${maximumPerformanceThreshold}) for ${metricName}.\n")
                }
            }
        }
    }

    static String buildMetricPath(String prefix, String scenario, String metricName) {
        StringBuilder stringBuilder = new StringBuilder()

        stringBuilder.append(GATLING_PREFIX)

        if (prefix != null) {
            stringBuilder.append(prefix)
            stringBuilder.append(".")
        }

        stringBuilder.append(scenario.toLowerCase())
        stringBuilder.append(".")
        stringBuilder.append(metricName)
        stringBuilder.append(".all.mean")

        return stringBuilder.toString()
    }

    /**
     * Remove nulls from the given list of response times
     *
     * @param datapoints list of response times
     * @return list without nulls
     */
    static filterNulls(def datapoints) {
        def meanTimes = []

        int nullCount = 0

        // Only add non-null values to list
        for (def point: datapoints) {
            if (point.get(0) != null) {
                meanTimes.add(point.get(0))

            } else {
                nullCount++
            }
        }

        return meanTimes
    }
}
