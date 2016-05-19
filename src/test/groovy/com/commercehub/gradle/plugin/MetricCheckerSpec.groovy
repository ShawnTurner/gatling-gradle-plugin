package com.commercehub.gradle.plugin

import spock.lang.Specification


/**
 * Created by bmanley on 11/9/15.
 */
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
