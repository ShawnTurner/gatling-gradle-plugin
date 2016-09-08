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

import org.gradle.api.Project

class GatlingPluginExtension {
    public static final boolean DEFAULT_CHECK_FOR_KOS = true
    public static final int DEFAULT_KO_THRESHOLD = 0
    public static final String DEFAULT_GATLING_DATA_DIR = 'data'
    public static final String DEFAULT_GATLING_REPORTS_DIR = 'build/reports'
    public static final String DEFAULT_GATLING_BODIES_DIR = 'user-files/request-bodies'
    public static final boolean DEFAULT_FAIL_BUILD = true
    public static final String DEFAULT_GATLING_CONF_FILE = 'config/gatling.conf'

    private final Project project

    GatlingPluginExtension(Project project) {
        this.project = project
    }

    def gatling(Closure closure) {
        closure.setDelegate this
        closure.call()
    }

    /**
     * Set whether or not the gatling task will check for KOed requests
     */
    Boolean checkForKOs

    boolean getCheckForKOs() {
        checkForKOs ?: DEFAULT_CHECK_FOR_KOS
    }

    /**
     * The number of KOs allowed before a build is considered failed
     */
    int koThreshold

    int getKoThreshold() {
        koThreshold ?: DEFAULT_KO_THRESHOLD
    }

    /**
     * Set Gatling data directory
     */
    def gatlingDataDir

    File getGatlingDataDir() {
        project.file (gatlingDataDir ?: DEFAULT_GATLING_DATA_DIR)
    }

    /**
     * Set Gatling reports directory
     */
    def gatlingReportsDir

    File getGatlingReportsDir() {
        project.file (gatlingReportsDir ?: DEFAULT_GATLING_REPORTS_DIR)
    }

    /**
     * Set Gatling bodies directory
     */
    def gatlingBodiesDir

    File getGatlingBodiesDir() {
        project.file (gatlingBodiesDir ?: DEFAULT_GATLING_BODIES_DIR)
    }

    Boolean failBuild

    boolean getFailBuild() {
        (failBuild != null) ? failBuild : DEFAULT_FAIL_BUILD
    }

    /**
     * Set Gatling conf file location
     */
    def gatlingConfFile

    File getGatlingConfFile() {
        project.file (gatlingConfFile ?: DEFAULT_GATLING_CONF_FILE)
    }

    GatlingMetricsConfig metrics = new GatlingMetricsConfig()

    @SuppressWarnings('ConfusingMethodName')
    def metrics(Closure closure) {
        closure.setDelegate this.metrics
        closure.call()
    }
}
