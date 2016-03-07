package com.commercehub.gradle.plugin

import groovy.util.logging.Slf4j


/**
 * Created by bmanley on 3/7/16.
 */
@Slf4j
class KoChecker {
    private static final String KO_REGEX = /:\sKO\s/

    static void checkForKos(int koThreshold) {
        String gatlingLog = new File("build/gatling.log").getText()

        int matches = gatlingLog.findAll(KO_REGEX).size()

        if (matches > koThreshold) {
            log.error("Found $matches KOed requests. This exceeds the allowed $koThreshold.")
            throw new GatlingGradlePluginException("Test failed. Found $matches failed requests, which is greater than " +
                    "the allowed $koThreshold. See report for failed requests.\n")
        }
    }
}
