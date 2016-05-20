package com.commercehub.gradle.plugin


class GatlingMetricsConfig {
    public static final String DEFAULT_METRIC_PREFIX = ''
    public static final int DEFAULT_DAYS_TO_CHECK = 7

    String graphiteUrl
    
    String metricPrefix
    
    String getMetricPrefix() {
        metricPrefix ?:  DEFAULT_METRIC_PREFIX
    }
    
    Integer daysToCheck

    int getDaysToCheck() {
        daysToCheck ?: DEFAULT_DAYS_TO_CHECK
    }
}

