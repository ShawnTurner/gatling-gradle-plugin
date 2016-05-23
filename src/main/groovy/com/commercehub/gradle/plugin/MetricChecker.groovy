package com.commercehub.gradle.plugin

import groovy.json.JsonSlurper

import java.text.SimpleDateFormat

/**
 * Created by bmanley on 11/9/15.
 */
class MetricChecker {
    private static final String GATLING_PREFIX = "gatling."

    /**
     * Return JSON from the given URL
     *
     * @param url the url to go to
     * @return JSON from url
     */
    static getJsonFromUrl(String url) {
        URL apiUrl = new URL(url)

        return new JsonSlurper().parse(apiUrl)
    }

    /**
     * Detect any failed assertions ("KOs") from the current test run
     *
     * @param fromDate start time
     * @param untilDate end time
     */
    static void detectFailedRequestQualityGate(int koThreshold) {
        File gatlingLogFile = new File('build/gatling.log')

        new KoChecker().checkForKos(koThreshold, gatlingLogFile)
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
                                  String metricName, int numberOfDays) {
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
                def acceptanceThreshold = averageResponseTime * 1.5

                if (currentResponseTime > acceptanceThreshold) {
                    throw new GatlingGradlePluginException(
                            "Current response time ${currentResponseTime} is greater than maximum " +
                                    "acceptable value ${acceptanceThreshold} for ${metricName}.\n")
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

        stringBuilder.append(scenario)
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
