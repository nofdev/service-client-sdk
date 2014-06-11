package com.shangpin.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
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

        String result = HttpUtil.post(postUrl, params);
        JsonNode jsonNode = objectMapper.readTree(result);

        if (jsonNode.get("err") == null || jsonNode.get("err") instanceof NullNode) {
            return objectMapper.convertValue(jsonNode.get("val"), method.getReturnType());
        }else{
            Class<?> c = ExceptionUtil.isExistClass(jsonNode.get("err").textValue());
            if(c == null){
                new ProxyException(objectMapper.readTree(jsonNode.get("err").textValue()).get("msg").textValue());
            }
        }
        return null;
    }

//    HttpJsonFacadeProxy(Class<?> httpJsonFacadeClass) {
//        this.httpJsonFacadeClass = httpJsonFacadeClass;
//    }

    public static Object getInstance(Class<?> inter,String url) {

            Class<?>[] interfaces = {inter};

        return Proxy.newProxyInstance(inter.getClassLoader(), interfaces, new HttpJsonFacadeProxy(inter, url));
    }

}
