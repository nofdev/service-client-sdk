//package com.shangpin.http
//
//import groovy.json.JsonBuilder
//import org.mockserver.integration.ClientAndServer
//import org.mockserver.model.HttpRequest
//import org.mockserver.model.HttpResponse
//import spock.lang.Specification
//
///**
// * Created by Qiang on 7/10/14.
// */
//class HttpJsonProxySpec extends Specification {
//
//    private ClientAndServer mockServer
//    private def url
//
//    def setupSpec() {
//    }
//
//    def setup() {
//        mockServer = ClientAndServer.startClientAndServer(9999)
//        url = "http://localhost:9999/com.shangpin.http/DemoFacade"
//    }
//
//    def cleanup() {
//        mockServer.stop()
//    }
//
//    def "测试能否正常的代理一个远程接口"() {
//        setup:
//        mockServer.when(
//                HttpRequest.request()
//                        .withURL("${url}/${method}")
//        ).respond(
//                HttpResponse.response()
//                        .withStatusCode(200)
//                        .withBody(new JsonBuilder([callId: UUID.randomUUID().toString(), val: val, err: null]).toString())
//        )
//        def proxy = new HttpJsonProxy(DemoFacade, url)
//        def testFacadeService = proxy.getObject()
//        def result = testFacadeService."${method}"(*args);
//        expect:
//        result == exp
//
//        where:
//        method              | args                                     | val                                      | exp
//        "method1"           | []                                       | "hello world"                            | "hello world"
//        "getAllAttendUsers" | [new UserDTO(name: "zhangsan", age: 10)] | [new UserDTO(name: "zhangsan", age: 10)] | [new UserDTO(name: "zhangsan", age: 10)]
//    }
//
//    def "测试能否正常的代理一个远程接口抛出的异常"() {
//        setup:
//        def exceptionMessage = new ExceptionMessage(name: "com.shangpin.http.TestException", msg: "Test")
//        mockServer.when(
//                HttpRequest.request()
//                        .withURL("${url}/method1")
//        ).respond(
//                HttpResponse.response()
//                        .withStatusCode(500)
//                        .withBody(new JsonBuilder([callId: UUID.randomUUID().toString(), val: null, err: exceptionMessage]).toString())
//        )
//        def proxy = new HttpJsonProxy(DemoFacade, url)
//        def testFacadeService = proxy.getObject()
//
//        when:
//        testFacadeService.method1()
//
//        then:
//        thrown(TestException)
//    }
//}
//
//class UserDTO implements Serializable {
//    /**
//     * 姓名
//     */
//    private String name;
//    /**
//     * 年龄
//     */
//    private Integer age;
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public Integer getAge() {
//        return age;
//    }
//
//    public void setAge(Integer age) {
//        this.age = age;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        return (this.name == obj.name && this.age == obj.age);
//    }
//}
//
//interface DemoFacade {
//    String method1();
//
//    List<UserDTO> getAllAttendUsers(UserDTO userDTO);
//}
//
//class TestException extends RuntimeException {
//    TestException(String msg) {
//        super(msg);
//    }
//}
//
//
