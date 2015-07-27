package org.nofdev.http

import org.apache.http.conn.ConnectionPoolTimeoutException
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
    private def urlSecure

    def setupSpec() {
    }

    def setup() {
        mockServer = ClientAndServer.startClientAndServer(9090, 9443)
        url = "http://localhost:9090/test"
        urlSecure = "https://localhost:9443/test"
    }

    def cleanup() {
        mockServer.stop()
    }

    def "一个基本的POST请求测试"() {
        setup:
        mockServer.when(HttpRequest.request().withURL(url)).respond(HttpResponse.response().withStatusCode(200).withHeader(new Header("Content-Type", "text/html")).withBody("hello world"))
        def httpMessage = new HttpClientUtil(new PoolingConnectionManagerFactory()).post(url, [:])
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
        new HttpClientUtil(new PoolingConnectionManagerFactory(), defaultRequestConfig).post(url, [:])
        then:
        thrown(IOException)
    }

    def "测试非200状态码"() {
        setup:
        mockServer.when(HttpRequest.request().withURL(url)).respond(HttpResponse.response().withStatusCode(500));
        def defaultRequestConfig = new DefaultRequestConfig()
        expect:
        new HttpClientUtil(new PoolingConnectionManagerFactory(), defaultRequestConfig).post(url, [:]).statusCode == 500
    }

    def "测试连接池，如果连接池数量不够，等待获取连接超时的话会抛出ConnectionPoolTimeoutException"() {
        setup:
        mockServer.when(HttpRequest.request().withURL(url)).respond(HttpResponse.response().withStatusCode(200).withBody("hello world").withDelay(new Delay(TimeUnit.SECONDS, 2)))
        def defaultRequestConfig = new DefaultRequestConfig()
        defaultRequestConfig.setDefaultConnectionRequestTimeout(100)
        def connectionManagerFactory = new PoolingConnectionManagerFactory()
        connectionManagerFactory.setMaxTotalConnection(3)
        connectionManagerFactory.setMaxPerRoute(3)
        when:
        Thread.start {
            new HttpClientUtil(connectionManagerFactory, defaultRequestConfig).post(url, [:])
        }

        Thread.start {
            new HttpClientUtil(connectionManagerFactory, defaultRequestConfig).post(url, [:])
        }

        Thread.start {
            new HttpClientUtil(connectionManagerFactory, defaultRequestConfig).post(url, [:])
        }

        sleep(1000)
        new HttpClientUtil(connectionManagerFactory, defaultRequestConfig).post(url, [:])
        then:
        thrown(ConnectionPoolTimeoutException)
    }

    def "测试https连接，使用不安全的信任全部证书和域名验证"() {
        setup:
        mockServer.when(HttpRequest.request().withURL(urlSecure)).respond(HttpResponse.response().withStatusCode(200).withHeader(new Header("Content-Type", "text/html")).withBody("hello world"))
        def httpMessage = new HttpClientUtil(new PoolingConnectionManagerFactory(true)).post(urlSecure, [:])
        expect:
        httpMessage.body == "hello world"
        httpMessage.statusCode == 200
        httpMessage.contentType == "text/html"
    }

    /**
     * TODO
     */
    def "测试定期清理未关闭的连接"() {

    }

//    def "测试并发"(){
//        setup:
//        mockServer.when(HttpRequest.request().withURL(url)).respond(HttpResponse.response().withStatusCode(200).withBody("hello world").withDelay(new Delay(TimeUnit.SECONDS, 1)))
//        def defaultRequestConfig = new DefaultRequestConfig()
//        defaultRequestConfig.setDefaultConnectionRequestTimeout(20000)
//        def connectionManagerFactory = new PoolingConnectionManagerFactory()
//        connectionManagerFactory.setMaxTotalConnection(5)
//        connectionManagerFactory.setMaxPerRoute(5)
//
////        for (int i=0;i<50;i++){
////            Thread.start {
////                new HttpClientUtil(connectionManagerFactory, defaultRequestConfig).post(url, [:])
////            }
////        }
//
//        new HttpClientUtil(connectionManagerFactory, defaultRequestConfig).post(url, [:])
//        new IdleConnectionMonitorThread(connectionManagerFactory).start()
//        sleep(50000)
//        expect:
//        true
//    }

    def "测试关闭连接"() {
        setup:
        def connectionManagerFactory = new PoolingConnectionManagerFactory()
        Thread thread = new IdleConnectionMonitorThread(connectionManagerFactory)
        thread.start()
        try {

        } finally {
            thread.shutdown()
        }
        sleep(1000)
        expect:
        thread.alive == false
    }
}
