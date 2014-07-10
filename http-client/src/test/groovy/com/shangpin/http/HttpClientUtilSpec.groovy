package com.shangpin.http

import org.mockserver.integration.ClientAndServer
import org.mockserver.model.Delay
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import spock.lang.Specification

import java.util.concurrent.TimeUnit

/**
 * Created by Qiang on 7/4/14.
 */
class HttpClientUtilSpec extends Specification {

    private ClientAndServer mockServer
    private def url

    def setup() {
        mockServer = ClientAndServer.startClientAndServer(9999)
        url = "http://localhost:9999/test"
    }

    def cleanup() {
        mockServer.stop()
    }

    def "一个基本的POST请求测试"(){
        setup:
        mockServer.when(HttpRequest.request().withURL(url)).respond(HttpResponse.response().withStatusCode(200).withHeader(new Header("Content-Type","text/html")).withBody("hello world"))
        def httpMessage = new HttpClientUtil((new PoolingConnectionManagerFactory()).getObject()).post(url,[:])
        expect:
        httpMessage.body=="hello world"
        httpMessage.statusCode==200
        httpMessage.contentType=="text/html"
    }

    def "测试响应超时"(){
        setup:
        mockServer.when(HttpRequest.request().withURL(url)).respond(HttpResponse.response().withStatusCode(200).withBody("hello world").withDelay(new Delay(TimeUnit.SECONDS,2)))
        def defaultRequestConfig = new DefaultRequestConfig()
        defaultRequestConfig.setDefaultSoTimeout(1000)
        when:
        new HttpClientUtil((new PoolingConnectionManagerFactory()).getObject(),defaultRequestConfig).post(url,[:])
        then:
        thrown(IOException)
    }

    def "测试非200状态码"(){
        setup:
        mockServer.when(HttpRequest.request().withURL(url)).respond(HttpResponse.response().withStatusCode(500));
        def defaultRequestConfig = new DefaultRequestConfig()
        expect:
        new HttpClientUtil((new PoolingConnectionManagerFactory()).getObject(),defaultRequestConfig).post(url,[:]).statusCode==500
    }

    def "测试连接池"(){}
}
