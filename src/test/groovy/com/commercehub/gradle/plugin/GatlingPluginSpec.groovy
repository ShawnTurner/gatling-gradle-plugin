package com.commercehub.gradle.plugin

import nebula.test.PluginProjectSpec

class GatlingPluginSpec extends PluginProjectSpec {
    @Override
    @SuppressWarnings('GetterMethodCouldBeProperty')
    String getPluginName() {
        return 'gatling'
    }
}
