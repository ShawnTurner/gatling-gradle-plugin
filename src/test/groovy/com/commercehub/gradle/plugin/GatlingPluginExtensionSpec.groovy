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
        4 * project.file(_) >> new File(_ as String)
    }
}
