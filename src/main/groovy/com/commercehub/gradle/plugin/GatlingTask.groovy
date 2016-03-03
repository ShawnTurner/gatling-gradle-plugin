package com.commercehub.gradle.plugin

import groovy.util.logging.Slf4j
import org.gradle.api.Project

/**
 * Created by bmanley on 11/16/15.
 */
@Slf4j
class GatlingTask {

    static void run(Project project, String scenario, def gatlingClasspath) {
        project.javaexec {
            standardInput = System.in
            standardOutput = new File(project.buildDir, 'gatling.log').newOutputStream()
            main = 'io.gatling.app.Gatling'
            classpath = gatlingClasspath

            jvmArgs '-Dgatling.core.directory.binaries=./build/classes/test',
                    '-Xmx1024M',
                    '-Xms1024M',
                    '-Xss1m'

            args '-df', project.gatlingTest.gatlingDataFolder
            args '-rf', project.gatlingTest.gatlingReportsFolder
            args '-bdf', project.gatlingTest.gatlingBodiesFolder
            args '-s', scenario
        }
    }

}
