/*
 * Copyright (C) 2017 Commerce Technologies, LLC
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

import org.gradle.api.Project
import spock.lang.Specification


class GatlingPluginExtensionSpec extends Specification {
    void testConfigurationByClosure() {
        given:
        def project = Mock(Project)

        when:
        GatlingPluginExtension extension = new GatlingPluginExtension(project)
        extension.gatling {
            checkForKOs = true
            koThreshold = 10
            failBuild = false
            gatlingDataDir = 'data'
            gatlingReportsDir = 'reports'
            gatlingBodiesDir = 'bodies'
            gatlingConfFile = 'gatling.conf'
            metrics {
                daysToCheck = 5
                graphiteUrl = 'http://locahost'
                metricPrefix = 'foo'
                degradationTolerance = 0.25
            }
        }
        def gatlingDataDir = extension.gatlingDataDir
        def gatlingReportsDir = extension.gatlingReportsDir
        def gatlingBodiesDir = extension.gatlingBodiesDir
        def gatlingConfFile = extension.gatlingConfFile

        then:
        assert extension.checkForKOs
        assert extension.koThreshold == 10
        assert !extension.failBuild
        assert extension.metrics.graphiteUrl == 'http://locahost'
        assert extension.metrics.metricPrefix == 'foo'
        assert extension.metrics.daysToCheck == 5
        assert extension.metrics.degradationTolerance == 0.25
        assert gatlingDataDir != null
        assert gatlingReportsDir != null
        assert gatlingBodiesDir != null
        assert gatlingConfFile != null
        1 * project.file('data') >> new File('data')
        1 * project.file('reports') >> new File('reports')
        1 * project.file('bodies') >> new File('bodies')
        1 * project.file('gatling.conf') >> new File('gatling.conf')
    }

    void testExtensionCanReturnDefaultValues() {
        given:
        def project = Mock(Project)

        when:
        GatlingPluginExtension extension = new GatlingPluginExtension(project)
        def gatlingDataDir = extension.gatlingDataDir
        def gatlingReportsDir = extension.gatlingReportsDir
        def gatlingBodiesDir = extension.gatlingBodiesDir
        def gatlingConfFile = extension.gatlingConfFile
        def metricsDegradationTolerance = extension.metrics.degradationTolerance

        then:
        assert extension.checkForKOs != null
        assert extension.koThreshold != null
        assert extension.failBuild != null
        assert extension.metrics.metricPrefix != null
        assert extension.metrics.daysToCheck != null
        assert gatlingDataDir != null
        assert gatlingReportsDir != null
        assert gatlingBodiesDir != null
        assert gatlingConfFile != null
        assert metricsDegradationTolerance != null
        4 * project.file(_) >> new File(_ as String)
    }
}
