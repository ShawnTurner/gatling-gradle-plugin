package com.commercehub.gradle.plugin

class GatlingTaskMetricsConfig {
    private final GatlingPluginExtension extension

    GatlingTaskMetricsConfig(GatlingPluginExtension extension) {
        this.extension = extension
    }

    def metricsToCheck = []

    String graphiteUrl

    String getGraphiteUrl() {
        graphiteUrl ?: extension.metrics.graphiteUrl
    }

    String metricPrefix

    String getMetricPrefix() {
        metricPrefix ?:  extension.metrics.metricPrefix
    }

    Integer daysToCheck

    int getDaysToCheck() {
        daysToCheck ?: extension.metrics.daysToCheck
    }

    Number degradationTolerance

    Number getDegradationTolerance() {
        degradationTolerance ?: extension.metrics.degradationTolerance
    }

    def thresholdsByMetricIndex = []
}
