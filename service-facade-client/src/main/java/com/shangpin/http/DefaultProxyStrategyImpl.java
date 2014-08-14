package com.shangpin.http;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Qiang on 8/14/14.
 */
public class DefaultProxyStrategyImpl implements ProxyStrategy {
    private static final Logger logger = LoggerFactory.getLogger(DefaultProxyStrategyImpl.class);

    private String baseURL;

    DefaultProxyStrategyImpl(String baseURL){
        this.baseURL = baseURL;
    }

    @Override
    public String getRemoteURL(Class<?> inter, Method method) {
        logger.debug("The baseUrl is {}", baseURL);
        String serviceLayer;
        if (inter.getName().endsWith("Facade")) {
            serviceLayer = "facade";
        } else if (inter.getName().endsWith("Micro")) {
            serviceLayer = "micro";
        } else {
            serviceLayer = "service";
        }
        StringBuilder remoteURLBuffer = new StringBuilder();
        remoteURLBuffer.append(baseURL);
        remoteURLBuffer.append("/");
        remoteURLBuffer.append(serviceLayer);
        remoteURLBuffer.append("/json/");
        remoteURLBuffer.append(inter.getPackage().getName());
        remoteURLBuffer.append("/");
        remoteURLBuffer.append(inter.getSimpleName());
        remoteURLBuffer.append("/");
        remoteURLBuffer.append(method.getName());
        String remoteURL = remoteURLBuffer.toString();
        logger.debug("The remoteUrl is {}", remoteURL);
        return remoteURL;
    }

    @Override
    public Map<String, String> getParams(Object[] args) throws JsonProcessingException {
        Map<String, String> params = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String paramsStr = objectMapper.writeValueAsString(args);
        logger.debug("The params string is {}", paramsStr);
        params.put("params", paramsStr);
        return params;
    }

    @Override
    public Object getResult(Method method, HttpMessageSimple httpMessageSimple) throws Throwable {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String result = httpMessageSimple.getBody();
        logger.debug("The request return " + result);
        JavaType javaType = objectMapper.getTypeFactory().constructType(method.getGenericReturnType());
        javaType = objectMapper.getTypeFactory().constructParametricType(HttpJsonResponse.class, javaType);
        HttpJsonResponse httpJsonResponse = objectMapper.readValue(result, javaType);
        if (httpJsonResponse.getErr() == null) {
            return httpJsonResponse.getVal();
        } else {
            throw ExceptionUtil.getThrowableInstance(httpJsonResponse.getErr());
        }
    }
}
