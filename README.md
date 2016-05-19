# Gatling Gradle Plugin

The gatling gradle plugin provides the ability to run gatling scenarios directly from a gradle build. The plugin utilizes
the gatling cli provided by [gatling](http://gatling.io/docs/2.0.0-RC2/general/configuration.html#command-line-options).


## Using the Plugin

The following gradle configuration will execute the gatling scenarios "MyAwesomeSimulation" and "MyCoolSimulation" using bodies
in the "bodies" directory, data in the "data" directory, and the configuration file "resources/gatling.conf". Reports will
be stores in the "reports" directory. The plugin will also check for KOed requests, and compare request times to previous request times.

    apply plugin: 'scala'

    buildscript {
        repositories {
            mavenCentral()
        }

        dependencies {
            classpath 'com.commercehub:gatling-gradle-plugin:1.0.+'
        }

        configurations.all {
            // check for updates every build
            resolutionStrategy.cacheDynamicVersionsFor 0, 'seconds'
            resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
        }

    }

    repositories {
        mavenCentral()
    }

    ext {
        SCALA_VERSION = "2.11.7"
        GATLING_VERSION = "2.2.0-M3"
    }

    dependencies {
        compile "org.scala-lang:scala-library:${SCALA_VERSION}"
        testCompile "io.gatling:gatling-http:${GATLING_VERSION}"
        testCompile "io.gatling:gatling-core:${GATLING_VERSION}"
        testCompile "io.gatling.highcharts:gatling-charts-highcharts:${GATLING_VERSION}"
        testCompile "io.gatling:gatling-app:${GATLING_VERSION}"
    }


    apply plugin: 'gatling'

    sourceSets {
        test {
            scala {
                srcDirs 'simulations'
            }
            resources {
                srcDirs 'resources'
            }
        }
    }

    gatlingTest {
        gatlingVersion = '2.2.0-M3'

        checkForKOs = true
        koThreshold = 10

        numberOfDaysToCheck = 5

        graphiteUrl = "http://my.graphite.url.com"

        gatlingSimulation = ['MyAwesomeSimulation', 'MyCoolSimulation']
        metricsToCheck = ['Request_Name_1', 'Request_Name_2']
        gatlingDataFolder = 'data'
        gatlingBodiesFolder = 'bodies'
        gatlingReportsFolder = 'reports'

        gatlingConfFile = 'resources/gatling.conf'
    }

Running the following command will execute the scenario:

    gradle(w) gatlingTest

## GatlingTest Task Configuration

* `gatlingVersion` : Set the version of gatling to use. Defaults to 2.2.0-M3.
* `checkForKOs` : Set whether or not to check for KOed requests. Defaults to true.
* `koThreshold` : Number of KOs that will be allowed before the build is considered failed. Defaults to 0.
* `numberOfDaysToCheck` : Number of previous days to compare the current run to. If this value is 0, no check will occur.
 Must be a positive integer. Defaults to 0.
* `graphiteUrl` : The graphite base url.
* `gatlingSimulation` : The list of scenarios to run.
* `metricsToCheck` : The list of graphite metrics to check.
* `gatlingDataFolder` : The directory containing gatling data files. Defaults to $projectRoot/data
* `gatlingBodiesFolder` : The directory containing gatling bodies files. Defaults to $projectRoot/bodies
* `gatlingReportsFolder` : The directory where gatling reports will be dropped. Defaults to $projectRoot/reports
* `gatlingConfFile` : The gatling conf file to use. Defaults to $projectRoot/resources/gatling.conf
* `graphiteMetricPrefix` : prefix to add to the graphite metric