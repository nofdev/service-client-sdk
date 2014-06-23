package com.shangpin.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qiang on 6/3/14.
 */
public class HttpJsonFacadeProxy implements InvocationHandler {
    private static final Logger logger =  LoggerFactory.getLogger(HttpJsonFacadeProxy.class);

    private Class<?> inter;
    private String url;
    private int defaultSoTimeout;
    private int defaultConnectionTimeout;
    private PoolConnectionManager connectionManager;

    public void setDefaultConnectionTimeout(int defaultConnectionTimeout) {
        this.defaultConnectionTimeout = defaultConnectionTimeout;
    }

    public void setDefaultSoTimeout(int defaultSoTimeout) {
        this.defaultSoTimeout = defaultSoTimeout;
    }

    public void setConnectionManager(PoolConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    HttpJsonFacadeProxy(Class<?> inter, String url) {
        this.inter = inter;
        this.url = url;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.debug("The url is "+url);
        String baseUrl = url.split("\\?")[0];
        logger.debug("The baseUrl is "+baseUrl);
        String postUrl = baseUrl+"/"+method.getName();
        logger.debug("The postUrl is "+postUrl);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        ObjectMapper objectMapper = new ObjectMapper();
        String paramsStr = objectMapper.writeValueAsString(args);
        params.add(new BasicNameValuePair("params", paramsStr));

        String result = HttpUtil.post(postUrl, params,connectionManager);
        JsonNode jsonNode = objectMapper.readTree(result);
        logger.debug("The request return " + result);
        if (jsonNode.get("err") == null || jsonNode.get("err") instanceof NullNode) {
            return objectMapper.convertValue(jsonNode.get("val"), method.getReturnType());
        }else{
            if(!ExceptionUtil.isExistClass(jsonNode.path("err").get("name").textValue())){
                throw new ProxyException(jsonNode.path("err").get("msg").textValue());
            }else{
                throw ExceptionUtil.getThrowableInstance(jsonNode.path("err").get("name").textValue(),jsonNode.path("err").get("msg").textValue());
            }
        }
    }

    public static Object getInstance(Class<?> inter,String url) {
        Class<?>[] interfaces = {inter};
        return Proxy.newProxyInstance(inter.getClassLoader(), interfaces, new HttpJsonFacadeProxy(inter, url));
    }

}
