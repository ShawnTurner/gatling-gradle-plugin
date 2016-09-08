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

import groovy.json.JsonSlurper

import java.text.SimpleDateFormat

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
