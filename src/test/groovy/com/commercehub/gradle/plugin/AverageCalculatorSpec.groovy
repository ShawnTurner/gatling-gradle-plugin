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
