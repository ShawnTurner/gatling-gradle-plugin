package com.commercehub.gradle.plugin

/**
 * Created by bmanley on 11/9/15.
 */
class AverageCalculatorTest extends GroovyTestCase {

    void testCalculateAverageResponseTime() {
        // Given: a list of numbers
        def numberList = [2, 4, 4, 4, 5, 5, 7, 9]

        // When: the average is calculated
        def average = AverageCalculator.calculateAverageResponseTime(numberList)

        // Then: the average should equal 5
        assert average == 5
    }

    void testCalculateStandardDev() {
        // Given: a list of numbers
        def numberList = [2, 4, 4, 4, 5, 5, 7, 9]
        def average = 5

        // When: the standard deviation is calculated
        def standardDev = AverageCalculator.calculateStandardDev(numberList, average)

        // Then: the standard deviation should equal 2
        assert standardDev == 2.toDouble()
    }

    void testFilterOutliers() {
        // Given: a list of numbers
        def numberList = [0, 50, 50, 50, 50, 50, 50, 100]

        // When: the list is filtered
        def filteredList = AverageCalculator.filterOutliers(numberList)

        // Then: the outliers should be filtered out, reducing the list to 6 entries from 8
        assert filteredList.size() == 6
        assert !filteredList.contains(0)
        assert !filteredList.contains(100)
    }
}
