package com.commercehub.gradle.plugin

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import java.text.SimpleDateFormat

/**
 * Created by bmanley on 11/9/15.
 */
@Slf4j
class MetricChecker {

    /**
     * Return JSON from the given URL
     *
     * @param url the url to go to
     * @return JSON from url
     */
    static getJsonFromUrl(String url) {
        log.info("Using url: ${url}")
        URL apiUrl = new URL(url)

        return new JsonSlurper().parse(apiUrl)
    }

    /**
     * Detect any failed assertions ("KOs") from the current test run
     *
     * @param fromDate start time
     * @param untilDate end time
     */
    static void detectFailedRequestQualityGate(String baseUrl, String scenario, Date fromDate, Date untilDate,
                                               int koThreshold) {
        // Specific metric key assumed. Avoid using reg expression syntax.
        String metric = "gatling.${scenario}.allRequests.ko.count"

        // Graphite date format
        String from = new SimpleDateFormat("HH:mm_yyyyMMdd", Locale.US).format(fromDate)
        String until = new SimpleDateFormat("HH:mm_yyyyMMdd", Locale.US).format(untilDate)

        // Graphite runs into problems if from and until times are the same. If they're the same add one minute.
        if (from == until) {
            log.warn("From and until times are the same: $until")

            final long ONE_MIN_IN_MILLIS = 60000

            long untilTime = untilDate.getTime()
            Date untilDatePlusOneMin = new Date(untilTime + ONE_MIN_IN_MILLIS)
            until = new SimpleDateFormat("HH:mm_yyyyMMdd", Locale.US).format(untilDatePlusOneMin)
        }

        log.info("Checking for KOs between ${from} and ${until}.")

        Object[][] dataPoints = getMetricForDateRange(baseUrl, metric, from, until)

        // If any counts exist fail
        int koCount = 0

        for (Object[] objArray: dataPoints) {
            if (objArray[0] != null) {
                koCount += objArray[0]
            }
        }

        if (koCount > koThreshold) {
            log.error("Found $koCount KOed requests for $scenario.")
            throw new GatlingGradlePluginException("Test failed. Found $koCount failed requests, which is greater than " +
                    "the allowed $koThreshold. See report for failed requests.\n")
        }
    }

    /**
     * Returns a single set of data points given a specific metric key (meaning no special characters like '*')
     * and date range in the format "HH:mm_yyyyMMdd".
     *
     * @param metric the metric to check
     * @param from the start date
     * @param until the end date
     * @return set of datapoints for the given metric between the given start and end dates
     */
    static getMetricForDateRange(String baseUrl, String metric, String from, String until) {
        // Create URL
        String apiPrefix = '/render/?target='
        String format = '&format=json'
        String dateRange = ""
        String apiString

        if (from != null) {
            dateRange += "&from=" + from
        }
        if (until != null) {
            dateRange += "&until=" + until
        }

        apiString = baseUrl + apiPrefix + metric + format + dateRange

        // Get JSON from URL
        def metricResult = getJsonFromUrl(apiString)

        Map dataMap =  metricResult.get(0)
        def datapoints = dataMap.get("datapoints")
        log.debug("Found datapoints: ${datapoints}")

        return datapoints
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
    static void checkPreviousDays(String baseUrl, String scenario, String metricName, int numberOfDays) {
        log.info("Checking the past ${numberOfDays} days.")

        final long DAYS_IN_MS = numberOfDays * 1000 * 60 * 60 * 24
        final Date START_DATE = new Date(System.currentTimeMillis() - DAYS_IN_MS).clearTime()

        String apiPrefix = '/render/?target='
        String metric = "gatling.${scenario}.${metricName}.all.mean"
        String format = "&format=json"
        String from = "&from=${new SimpleDateFormat("HH:mm_yyyyMMdd", Locale.US).format(START_DATE)}"

        // Get json from Graphite API
        String apiString = baseUrl + apiPrefix + metric + format + from
        def json = getJsonFromUrl(apiString)

        if (json.size > 0) {

            // Get list of datapoints
            def datapoints = json.datapoints.get(0)
            log.debug("Datapoints: ${datapoints}")

            // Filter nulls
            def meanTimes = filterNulls(datapoints)
            log.debug("Datapoints without nulls: ${meanTimes}")

            if (meanTimes.size() > 0) {

                // Get the most recent response time
                def currentResponseTime = meanTimes.last()
                log.debug("Most recent mean response time: ${currentResponseTime}")

                // Remove the most recent response time from the list
                meanTimes.dropRight(1)

                // Calculate the average response time
                def averageResponseTime = AverageCalculator.calculateAverageResponseTime(meanTimes)
                log.debug("Average response time for ${scenario}, ${metricName} for last ${numberOfDays}: " +
                        "${averageResponseTime}")

                // Calculate maximum acceptable response time
                def acceptanceThreshold = averageResponseTime * 1.5
                log.debug("Max acceptable response time: ${acceptanceThreshold}")

                if (currentResponseTime > acceptanceThreshold) {
                    log.error("Response time ${currentResponseTime} exceeded acceptable value ${acceptanceThreshold}.")
                    throw new GatlingGradlePluginException(
                            "Current response time ${currentResponseTime} is greater than maximum " +
                                    "acceptable value ${acceptanceThreshold} for ${metricName}.\n")
                }
            }
        } else {
            log.warn("No datapoints found for $metricName")
        }
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

        log.debug("Filtered out ${nullCount} nulls.")

        return meanTimes
    }
}
