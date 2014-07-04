package com.shangpin.http

import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import spock.lang.Specification

/**
 * Created by Qiang on 7/4/14.
 */
class HttpClientUtilSpec extends Specification {

    private ClientAndServer mockServer

    def setup() {
        mockServer = ClientAndServer.startClientAndServer(8080)
    }

    def cleanup() {
        mockServer.stop()
    }

    def "test something"(){
        setup:
        def url = "http://localhost:8080/test"
        mockServer.when(HttpRequest.request().withURL())
        expect:
        1==1
    }
}
