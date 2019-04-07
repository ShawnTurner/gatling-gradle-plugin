package com.commercehub.gradle.plugin

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*

class GatlingPluginFunctionalSpec extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
    }

    @Unroll
    def "can execute loadTest task with Gradle version #gradleVersion"() {
        given:
        buildFile << """
            plugins {
                id 'scala'
                id 'gatling'
            }
            
            repositories {
               jcenter()
            }
            
            ext {
                SCALA_VERSION = "2.11.7"
                GATLING_VERSION = "2.2.3"
            }

            dependencies {
                compile "org.scala-lang:scala-library:\${SCALA_VERSION}"
                testCompile "io.gatling:gatling-http:\${GATLING_VERSION}"
                testCompile "io.gatling:gatling-core:\${GATLING_VERSION}"
                testCompile "io.gatling.highcharts:gatling-charts-highcharts:\${GATLING_VERSION}"
                testCompile "io.gatling:gatling-app:\${GATLING_VERSION}"
                testCompile this.project.sourceSets.test.output
            }
            
            import com.commercehub.gradle.plugin.GatlingTask
            task loadTest(type: GatlingTask) {
               gatlingSimulation = 'Simulation'
            }
        """

        when:

        def result = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir.root)
                .withArguments('loadTest', '--stacktrace')
                .withPluginClasspath()
                .build()

        then:
        //result.output.contains('Hello world!')
        result.task(":loadTest").outcome == SUCCESS

        where:
        // gradle 2.8 is the earliest version that supports GradleRunner withPluginClasspath()
        gradleVersion << ['2.8', '3.1', '4.2', '5.2']
    }
}
