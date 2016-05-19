package com.commercehub.gradle.plugin

import spock.lang.Specification

/**
 * Created by bmanley on 3/7/16.
 */
class KoCheckerSpec extends Specification {
    private static final String TEST_RESPONSE =
            '''>>>>>>>>>>>>>>>>>>>>>>>>>>
                Request:
                Get by Partner Id: KO status.find.is(200), but actually found 404
                =========================
                Session:
                Session(Get Result for Partner,53445937929107898-161,Map(gatling.http.cache.dns ->
                =========================
                HTTP request:
                GET http://my.cool.url
                headers=
                Connection: keep-alive
                Host: host.name
                Authorization: Basic c2Ffd2FyZWhvdXNld2ViX29wZW46QGNjZXNzMkFsbA==
                Accept: */*
                realm=Realm{principal='username', password='password', scheme=BASIC,
                =========================
                HTTP response:
                status=
                404 Not Found
                headers=
                Date: [Mon, 07 Mar 2016 16:33:26 GMT]
                Content-Type: [application/json]
                Content-Length: [121]

                body=
                {"type":"NotFoundException","message":"Partner not found or linked to org: aero","causedBy":[],"constraintViolations":[]}
                <<<<<<<<<<<<<<<<<<<<<<<<<'''

    private static final KoChecker KO_CHECKER = new KoChecker()


    void testCheckForKos() {
        given:
        final int KO_THRESHOLD = 0

        File testFile = new File('test')
        testFile.write(TEST_RESPONSE)

        when:
        KO_CHECKER.checkForKos(KO_THRESHOLD, testFile)

        then:
        thrown GatlingGradlePluginException
    }

    void testCheckForKosPass() {
        given:
        final int KO_THRESHOLD = 1

        File testFile = new File('test')
        testFile.write(TEST_RESPONSE)

        when:
        KO_CHECKER.checkForKos(KO_THRESHOLD, testFile)

        then:
        assert testFile.getText() == TEST_RESPONSE
    }

}
