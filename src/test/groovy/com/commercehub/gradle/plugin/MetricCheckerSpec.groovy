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

class MetricCheckerSpec extends Specification {

    void testFilterNulls() {
        given: 'a list of values'
        def list = [[null, 1234], ['null', 1234], ['nil', 1234], ['foo', 1234], [1, 1234], ['0', 1234]]

        when: 'the list is filtered'
        def filteredList = MetricChecker.filterNulls(list)

        then: 'null values are removed from the list'
        assert filteredList.size() == 5
        assert !filteredList.contains(null)
    }

    void testBuildMetricPath() {
        given:
        final String PREFIX = "foo"
        final String SCENARIO = "bar"
        final String METRIC = "blah"
        final String EXPECTED_METRIC_PATH = "gatling.${PREFIX}.${SCENARIO}.${METRIC}.all.mean"

        when:
        String builtMetricPath = MetricChecker.buildMetricPath(PREFIX, SCENARIO, METRIC)

        then:
        assert builtMetricPath == EXPECTED_METRIC_PATH
    }

    void testBuildMetricPathNoPrefix() {
        given:
        final String PREFIX = null
        final String SCENARIO = "foo"
        final String METRIC = "blah"
        final String EXPECTED_METRIC_PATH = "gatling.${SCENARIO}.${METRIC}.all.mean"

        when:
        String builtMetricPath = MetricChecker.buildMetricPath(PREFIX, SCENARIO, METRIC)

        then:
        assert builtMetricPath == EXPECTED_METRIC_PATH
    }
}
