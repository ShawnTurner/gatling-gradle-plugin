package com.commercehub.gradle.plugin

import groovy.util.logging.Slf4j

/**
*  Created by bmanley on 11/9/15.
*/
@Slf4j
class AverageCalculator {

    /**
     * Calculate the average response time for the given list of response times
     *
     * @param responseTimes list of response times
     * @return average of the given times
     */
    static calculateAverageResponseTime(def responseTimes) {
        log.debug("Calculating average of: ${responseTimes}")
        def averageResponseTime = responseTimes.sum() / responseTimes.size()

        log.debug("Returning average: ${averageResponseTime}")
        return averageResponseTime
    }

    /**
     * Calculate the population standard deviation of the given list of response times
     *
     * @param responseTimes list of response times
     * @param average the average response time
     * @return standard deviation of the list
     */
    static calculateStandardDev(def responseTimes, def average) {
        def devSquaredList = []

        log.debug("Calculating standard deviation of: ${responseTimes}")

        for (def time: responseTimes) {
            def deviation = time - average
            def devSquared = deviation * deviation
            devSquaredList.add(devSquared)
        }

        // variance = sum(deviation ^ 2) / population size
        def variance = devSquaredList.sum() / devSquaredList.size()

        // stdDev = (variance ^ (1/2))
        def stdDev = Math.sqrt(variance)
        log.debug("Returning standard deviation: ${stdDev}")

        return stdDev
    }

    /**
     * Filter outliers out of the list of response times.
     * A value more than two standard deviations from the average is considered an outlier.
     *
     * @param responseTimes list of response times
     * @return list of response times without outliers
     */
    static filterOutliers(def responseTimes) {
        log.debug("Filtering out outliers for: ${responseTimes}")

        def average = calculateAverageResponseTime(responseTimes)
        def standardDev = calculateStandardDev(responseTimes, average)

        def low = average - 2 * standardDev
        log.debug("Using floor: ${low}")
        def high = average + 2 * standardDev
        log.debug("Using ceiling: ${high}")

        def returnList = []

        for (def time: responseTimes) {
            if (time > low && time < high) {
                returnList.add(time)
            } else {
                log.debug("Filtered out time: ${time}")
            }
        }

        return returnList
    }
}
