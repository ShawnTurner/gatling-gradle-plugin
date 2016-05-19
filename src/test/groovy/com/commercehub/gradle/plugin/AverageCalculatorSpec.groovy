package com.commercehub.gradle.plugin

import spock.lang.Specification

/**
 * Created by bmanley on 11/9/15.
 */
class AverageCalculatorSpec extends Specification {

    void testCalculateAverageResponseTime() {
        given: 'a list of numbers'
        def numberList = [2, 4, 4, 4, 5, 5, 7, 9]

        when: 'the average is calculated'
        def average = AverageCalculator.calculateAverageResponseTime(numberList)

        then: 'the average should equal 5'
        assert average == 5
    }

    void testCalculateStandardDev() {
        given: 'a list of numbers'
        def numberList = [2, 4, 4, 4, 5, 5, 7, 9]
        def average = 5

        when: 'the standard deviation is calculated'
        def standardDev = AverageCalculator.calculateStandardDev(numberList, average)

        then: 'the standard deviation should equal 2'
        assert standardDev == 2.toDouble()
    }

    void testFilterOutliers() {
        given: 'a list of numbers'
        def numberList = [0, 50, 50, 50, 50, 50, 50, 100]

        when: 'the list is filtered'
        def filteredList = AverageCalculator.filterOutliers(numberList)

        then: 'the outliers should be filtered out, reducing the list to 6 entries from 8'
        assert filteredList.size() == 6
        assert !filteredList.contains(0)
        assert !filteredList.contains(100)
    }
}
