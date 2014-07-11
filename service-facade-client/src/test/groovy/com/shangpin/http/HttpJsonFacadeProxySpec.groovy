package com.shangpin.http

import org.mockserver.integration.ClientAndServer
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import spock.lang.Specification

/**
 * Created by Qiang on 7/10/14.
 */
class HttpJsonFacadeProxySpec extends Specification{

    private ClientAndServer mockServer
    private def url

    def setupSpec() {
    }

    def setup() {
        mockServer = ClientAndServer.startClientAndServer(9999)
        url = "http://localhost:9999/test"
    }

    def cleanup() {
        mockServer.stop()
    }

    def "test some thing"(){
        setup:
        mockServer.when(HttpRequest.request().withURL(url)).respond(HttpResponse.response().withStatusCode(200).withHeader(new Header("Content-Type", "text/html")).withBody("hello world"))

    }
}
