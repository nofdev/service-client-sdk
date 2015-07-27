package org.nofdev.http;

/**
 * 一个简化的HttpMessage，只关心statusCode，contentType和转成String类型的body
 */
public class HttpMessageSimple {
    private int statusCode;
    private String contentType;
    private String body;

    public HttpMessageSimple() {
    }

    public HttpMessageSimple(int statusCode, String contentType, String body) {

        this.statusCode = statusCode;
        this.contentType = contentType;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
