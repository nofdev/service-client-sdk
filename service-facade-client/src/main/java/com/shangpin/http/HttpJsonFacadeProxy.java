package com.shangpin.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import org.apache.http.NameValuePair;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Qiang on 6/3/14.
 */
public class HttpJsonFacadeProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpJsonFacadeProxy.class);

    private Class<?> inter;
    private String url;
    private DefaultRequestConfig defaultRequestConfig = new DefaultRequestConfig();
    private PoolingHttpClientConnectionManager connectionManager;

    HttpJsonFacadeProxy(Class<?> inter, String url, PoolingHttpClientConnectionManager connectionManager, DefaultRequestConfig defaultRequestConfig) {
        this.inter = inter;
        this.url = url;
        this.connectionManager = connectionManager;
        this.defaultRequestConfig = defaultRequestConfig;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.debug("The url is " + url);
        String baseUrl = url.split("\\?")[0];
        logger.debug("The baseUrl is " + baseUrl);
        String postUrl = baseUrl + "/" + method.getName();
        logger.debug("The postUrl is " + postUrl);

//        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Map<String, String> params = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String paramsStr = objectMapper.writeValueAsString(args);
        params.put("params", paramsStr);

        HttpClientUtil httpClientUtil = new HttpClientUtil(connectionManager, defaultRequestConfig);
        String result = httpClientUtil.post(postUrl, params);
        JsonNode jsonNode = objectMapper.readTree(result);
        logger.debug("The request return " + result);
        if (jsonNode.get("err") == null || jsonNode.get("err") instanceof NullNode) {
            return objectMapper.convertValue(jsonNode.get("val"), method.getReturnType());
        } else {
            if (!ExceptionUtil.isExistClass(jsonNode.path("err").get("name").textValue())) {
                throw new ProxyException(jsonNode.path("err").get("msg").textValue());
            } else {
                throw ExceptionUtil.getThrowableInstance(jsonNode.path("err").get("name").textValue(), jsonNode.path("err").get("msg").textValue());
            }
        }
    }

    public Object getObject() {
        Class<?>[] interfaces = {inter};
        return Proxy.newProxyInstance(inter.getClassLoader(), interfaces, new HttpJsonFacadeProxy(inter, url, connectionManager, defaultRequestConfig));
    }

}
