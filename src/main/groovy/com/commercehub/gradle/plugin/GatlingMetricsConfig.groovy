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

