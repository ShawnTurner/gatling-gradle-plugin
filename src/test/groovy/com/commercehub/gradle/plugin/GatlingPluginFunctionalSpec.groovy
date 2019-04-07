package com.commercehub.gradle.plugin

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*

@Unroll
class GatlingPluginFunctionalSpec extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile
    File gatlingScenario

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        def gatlingTestFolder = testProjectDir.newFolder('src' ,'test', 'scala')
        gatlingScenario = new File(gatlingTestFolder, "TestSimulation.scala")
        gatlingScenario << """
            import io.gatling.core.Predef._
            import io.gatling.http.Predef._
            import scala.concurrent.duration._
            
            class TestSimulation extends Simulation { 
              val httpProtocol = http
                .baseURL("http://${UUID.randomUUID()}")
                .acceptLanguageHeader("en-US,en;q=0.5")
                .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")
               
              val scn = scenario("TestSimulation")
                .exec(http("testsim").get("/"))
            
              setUp(
                scn.inject(atOnceUsers(1))
              ).protocols(httpProtocol)
            }
        """
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
            }
            
            import com.commercehub.gradle.plugin.GatlingTask
            task loadTest(type: GatlingTask, dependsOn: 'compileTestScala') {
               gatlingSimulation = 'TestSimulation'
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
        result.task(":loadTest").outcome == SUCCESS

        where:
        // gradle 2.8 is the earliest version that supports GradleRunner withPluginClasspath()
        gradleVersion << ['2.8', '3.1', '4.2.1', '5.2.1']
    }
}
