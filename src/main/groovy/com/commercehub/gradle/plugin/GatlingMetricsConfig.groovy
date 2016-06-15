package com.commercehub.gradle.plugin


class GatlingMetricsConfig {
    public static final String DEFAULT_METRIC_PREFIX = ''
    public static final int DEFAULT_DAYS_TO_CHECK = 7
    public static final Number DEFAULT_DEGRADATION_TOLERANCE = 0.50

    String graphiteUrl
    
    String metricPrefix
    
    String getMetricPrefix() {
        metricPrefix ?:  DEFAULT_METRIC_PREFIX
    }
    
    Integer daysToCheck

    int getDaysToCheck() {
        daysToCheck ?: DEFAULT_DAYS_TO_CHECK
    }

    Number degradationTolerance

    Number getDegradationTolerance() {
        degradationTolerance ?: DEFAULT_DEGRADATION_TOLERANCE
    }
}

