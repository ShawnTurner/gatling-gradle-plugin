package com.commercehub.gradle.plugin

import org.gradle.api.Project
import spock.lang.Specification

class GatlingTaskMetricsConfigSpec extends Specification {

    final String expectedGraphiteUrl = 'http://locahost'
    final String expectedMetricsPrefix = 'foo'
    final Integer expectedDaysToCheck = 5
    final Number expectedDegradationTolerance = 0.25

    final pluginExtensionConfig = {
        metrics {
            daysToCheck = expectedDaysToCheck
            graphiteUrl = expectedGraphiteUrl
            metricPrefix = expectedMetricsPrefix
            degradationTolerance = expectedDegradationTolerance
        }
    }

    def testDefaultGetters () {
        given: "A GatlingTaskMetricsConfig with no modifications"
        def project = Mock(Project)
        GatlingPluginExtension extension = new GatlingPluginExtension(project)
        extension.gatling pluginExtensionConfig
        GatlingTaskMetricsConfig metricsConfig = new GatlingTaskMetricsConfig(extension)

        when: "The GatlingTaskMetricsConfig's fields are accessed"
        String testGraphiteUrl = metricsConfig.getGraphiteUrl()
        String testMetricsPrefix = metricsConfig.getMetricPrefix()
        Integer testDaysToCheck = metricsConfig.getDaysToCheck()
        Number testDegredationTolerance = metricsConfig.getDegradationTolerance()

        then: "The expected values from the GatlingPluginExtension are returned"
        assert testGraphiteUrl == expectedGraphiteUrl
        assert testMetricsPrefix == expectedMetricsPrefix
        assert testDaysToCheck == expectedDaysToCheck
        assert testDegredationTolerance == expectedDegradationTolerance
    }

    def testNonDefaultGetters() {
        final String OVERWRITTEN_GRAPHITE_URL = 'http://google.com'
        final String OVERWRITTEN_METRIC_PREFIX = 'bar'
        final Integer OVERWRITTEN_DAYS_TO_CHECK = 1
        final Number OVERWRITTEN_DEGRADATION_TOLERANCE = 0.1

        given: "A GatlingTaskMetrics with overwritten fields"
        def project = Mock(Project)
        GatlingPluginExtension extension = new GatlingPluginExtension(project)
        extension.gatling pluginExtensionConfig
        GatlingTaskMetricsConfig metricsConfig = new GatlingTaskMetricsConfig(extension)

        metricsConfig.graphiteUrl = OVERWRITTEN_GRAPHITE_URL
        metricsConfig.metricPrefix = OVERWRITTEN_METRIC_PREFIX
        metricsConfig.daysToCheck = OVERWRITTEN_DAYS_TO_CHECK
        metricsConfig.degradationTolerance = OVERWRITTEN_DEGRADATION_TOLERANCE

        when: "The GatlingTaskMetricsConfig's fields are accessed"
        String testGraphiteUrl = metricsConfig.getGraphiteUrl()
        String testMetricsPrefix = metricsConfig.getMetricPrefix()
        Integer testDaysToCheck = metricsConfig.getDaysToCheck()
        Number testDegradationTolerance = metricsConfig.getDegradationTolerance()

        then: "The expected overwritten values are returned"
        assert testGraphiteUrl == OVERWRITTEN_GRAPHITE_URL
        assert testMetricsPrefix == OVERWRITTEN_METRIC_PREFIX
        assert testDaysToCheck == OVERWRITTEN_DAYS_TO_CHECK
        assert testDegradationTolerance == OVERWRITTEN_DEGRADATION_TOLERANCE
    }
}
