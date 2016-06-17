package com.commercehub.gradle.plugin

/**
 * Created by bmanley on 3/7/16.
 */
class KoChecker {
    private static final String KO_REGEX = /:\sKO\s/

    static void checkForKos(int koThreshold, File gatlingLogFile) {
        String gatlingLog = gatlingLogFile.getText()

        int matches = gatlingLog.findAll(KO_REGEX).size()

        if (matches > koThreshold) {
            throw new GatlingGradlePluginException("Test failed. Found $matches failed requests, which is greater than " +
                    "the allowed $koThreshold. See report for failed requests.\n")
        }
    }
}
