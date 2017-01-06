# Gatling Gradle Plugin

The gatling gradle plugin provides the ability to run gatling scenarios directly from a gradle build. The plugin utilizes
the gatling cli provided by [gatling](http://gatling.io/docs/2.0.0-RC2/general/configuration.html#command-line-options).

## 2.0 Release

*June 20, 2016*

Version 2.0 of this plugin is now available. This version is a major upgrade and is not compatible with configurations for the 1.x plugin versions. This upgrade deliveres the following features:
* The ability to configure multiple simulations per gradle project.
* Ability to set degradation tolerances for comparison of metrics in graphite.
* Ability to pass JVM properties per task definition.

*September 7, 2016*

Version 2.1 of this plugin is now available. This version is a minor upgrade and should not effect current configurations for the 2.x plugin versions. This upgrade deliveres the following features:
* The ability to set minimum average time thresholds for individual metrics

### Compatibility
The gatling gradle plugin has been verified to work with version 2.2.3 of gatling and gradle 3.3.

## Using the Plugin

The following gradle configuration will execute the gatling scenarios "MyAwesomeSimulation" and "MyCoolSimulation" using bodies
in the "bodies" directory, data in the "data" directory, and the configuration file "resources/gatling.conf". Reports will
be stores in the "reports" directory. The plugin will also check for KOed requests, and compare request times to previous request times.

    apply plugin: 'scala'

    buildscript {
        repositories {
            jcenter()
        }

        dependencies {
            classpath 'com.commercehub:gatling-gradle-plugin:2.0'
        }

    }

    repositories {
        jcenter()
    }

    ext {
        SCALA_VERSION = "2.11.7"
        GATLING_VERSION = "2.2.3"
    }

    dependencies {
        compile "org.scala-lang:scala-library:${SCALA_VERSION}"
        testCompile "io.gatling:gatling-http:${GATLING_VERSION}"
        testCompile "io.gatling:gatling-core:${GATLING_VERSION}"
        testCompile "io.gatling.highcharts:gatling-charts-highcharts:${GATLING_VERSION}"
        testCompile "io.gatling:gatling-app:${GATLING_VERSION}"
    }


    apply plugin: 'gatling'

    gatling {
        checkForKOs = true
        koThreshold = 0

        metrics {
            graphiteUrl = "http://my.graphite.server.com"
            metricPrefix = 'my-namespace'
        }
    }

    import com.commercehub.gradle.plugin.GatlingTask
    task loadTest(type: GatlingTask, dependsOn: ['testClasses']) {
        gatlingSimulation = 'MyGatlingTest'
        metrics {
            metricsToCheck = ['myapp:pageresponsetime']
            daysToCheck = 5
            degradationTolerance = 0.50
            thresholdsByMetricIndex = [100]
        }
        jvmOptions {
            minHeapSize = "1024m"
            maxHeapSize = "1024m"
            systemProperty 'some.custom.setting', 'value'
        }
    }


Running the following command will execute the gatling task defined above:

    gradle(w) loadTest

## GatlingTest Task Configuration

All task paramters except for sourceSet, gatlingSimulation and jvmOptions can be configured at the 'gatling' extension
level to provide global defaults.

* `sourceSet` : Project source set containing gatling simulation to run. Defaults to project.sourceSets.test.
* `gatlingSimulation` : The list of scenarios to run.
* `failBuild` : Sets whether or not a failed load test should fail/stop the build. Defaults to true.
* `checkForKOs` : Set whether or not to check for KOed requests. Defaults to true.
* `koThreshold` : Number of KOs that will be allowed before the build is considered failed. Defaults to 0.
* `gatlingDataDir` : The directory containing gatling data files. Defaults to $projectRoot/data.
* `gatlingBodiesDir` : The directory containing gatling bodies files. Defaults to $projectRoot/user-files/request-bodies.
* `gatlingReportsDir` : The directory where gatling reports will be dropped. Defaults to $projectRoot/build/reports.
* `gatlingConfFile` : The gatling conf file to use. Defaults to $projectRoot/resources/gatling.conf.
* `metrics` : Nested configuration closure configuring performance metrics checks.
* `jvmOptions` : Nested configuration of type [JavaForkOptions](https://docs.gradle.org/current/javadoc/org/gradle/process/JavaForkOptions.html)

## Configuring checking of performance metrics in graphite

* `graphiteUrl` : The graphite base url.
* `graphiteMetricPrefix` : prefix to add to the graphite metric.
* `metricsToCheck` : The list of graphite metrics to check. Metrics in this list will be pre-pended with.
 `gatling.<graphiteMetricPrefix>.<gatlingSimulation(lowercase)>`.
* `thresholdsByMetricIndex` : The list of timing thresholds(in ms) that you don't want your average metric times to exceed.
 Must be a positive integer. Must have one threshold per `metricsToCheck` or exclude from configuration to bypass tests.
 The index of the threshold number correlates to the index of the metric in `metricsToCheck` that it belongs to.
* `numberOfDaysToCheck` : Number of previous days to compare the current run to. If this value is 0, no check will occur.
 Must be a positive integer. Defaults to 0.
* `degradationTolerance` : Percentage threshold for which the current average response times for a metric cannot exceed
 historical averages. Defaults to 0.5, for 50%.
