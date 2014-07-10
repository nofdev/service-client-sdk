package com.shangpin.http

import org.apache.http.conn.ConnectionPoolTimeoutException
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.Delay
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import spock.lang.Specification
import spock.util.concurrent.AsyncConditions
import spock.util.concurrent.PollingConditions

import java.util.concurrent.TimeUnit

/**
 * Created by Qiang on 7/4/14.
 */
class HttpClientUtilSpec extends Specification {

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

    def "一个基本的POST请求测试"() {
        setup:
        mockServer.when(HttpRequest.request().withURL(url)).respond(HttpResponse.response().withStatusCode(200).withHeader(new Header("Content-Type", "text/html")).withBody("hello world"))
        def httpMessage = new HttpClientUtil((new PoolingConnectionManagerFactory()).getObject()).post(url, [:])
        expect:
        httpMessage.body == "hello world"
        httpMessage.statusCode == 200
        httpMessage.contentType == "text/html"
    }

    def "测试响应超时"() {
        setup:
        mockServer.when(HttpRequest.request().withURL(url)).respond(HttpResponse.response().withStatusCode(200).withBody("hello world").withDelay(new Delay(TimeUnit.SECONDS, 2)))
        def defaultRequestConfig = new DefaultRequestConfig()
        defaultRequestConfig.setDefaultSoTimeout(1000)
        when:
        new HttpClientUtil((new PoolingConnectionManagerFactory()).getObject(), defaultRequestConfig).post(url, [:])
        then:
        thrown(IOException)
    }

    def "测试非200状态码"() {
        setup:
        mockServer.when(HttpRequest.request().withURL(url)).respond(HttpResponse.response().withStatusCode(500));
        def defaultRequestConfig = new DefaultRequestConfig()
        expect:
        new HttpClientUtil((new PoolingConnectionManagerFactory()).getObject(), defaultRequestConfig).post(url, [:]).statusCode == 500
    }

    def "测试连接池，如果连接池数量不够，等待获取连接超时的话会抛出ConnectionPoolTimeoutException"() {
        setup:
        mockServer.when(HttpRequest.request().withURL(url)).respond(HttpResponse.response().withStatusCode(200).withBody("hello world").withDelay(new Delay(TimeUnit.SECONDS, 2)))
        def defaultRequestConfig = new DefaultRequestConfig()
        defaultRequestConfig.setDefaultConnectionRequestTimeout(100)
        def connectionManagerFactory = new PoolingConnectionManagerFactory()
        connectionManagerFactory.setMaxTotalConnection(3)
        connectionManagerFactory.setMaxPerRoute(3)
        def cond = new AsyncConditions()

        when:
        Thread.start {
            new HttpClientUtil(connectionManagerFactory.getObject(), defaultRequestConfig).post(url, [:])
        }

        Thread.start {
            new HttpClientUtil(connectionManagerFactory.getObject(), defaultRequestConfig).post(url, [:])
        }

        Thread.start {
            new HttpClientUtil(connectionManagerFactory.getObject(), defaultRequestConfig).post(url, [:])
        }
        sleep(1000)
        new HttpClientUtil(connectionManagerFactory.getObject(), defaultRequestConfig).post(url, [:])
        then:
        thrown(ConnectionPoolTimeoutException)
    }
}
