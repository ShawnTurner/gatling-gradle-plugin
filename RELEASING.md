# How to release
1. Follow the steps outlined in CONTRIBUTING.md.
1. Run bintrayUpload with your bintray username and API key.
    1. `gradle(w) clean build bintrayUpload -DbintrayUserName=my-bintray-username -DbintrayApiKey=my-bintray-api-key`
1. Go to the [GitHub Releases page](https://github.com/commercehub-oss/gatling-gradle-plugin/releases),  click "Draft a new release", select the tag version, use the version number as the title, add release notes and click "Publish release".