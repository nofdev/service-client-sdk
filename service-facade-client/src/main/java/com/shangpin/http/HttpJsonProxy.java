package com.shangpin.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Qiang on 6/3/14.
 */
public class HttpJsonProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpJsonProxy.class);

    private Class<?> inter;
    private String url;
    private DefaultRequestConfig defaultRequestConfig;
    private PoolingConnectionManagerFactory connectionManagerFactory;

    public HttpJsonProxy(Class<?> inter, String url, PoolingConnectionManagerFactory connectionManagerFactory, DefaultRequestConfig defaultRequestConfig) {
        this.inter = inter;
        this.url = url;
        if (connectionManagerFactory == null) {
            try {
                this.connectionManagerFactory = new PoolingConnectionManagerFactory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.connectionManagerFactory = connectionManagerFactory;
        }
        if (defaultRequestConfig == null) {
            this.defaultRequestConfig = new DefaultRequestConfig();
        } else {
            this.defaultRequestConfig = defaultRequestConfig;
        }
    }

    public HttpJsonProxy(Class<?> inter, String url) {
        this(inter, url, null, null);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.debug("The url is " + url);
        String baseUrl = url.split("\\?")[0];
        logger.debug("The baseUrl is {}", baseUrl);
        String postUrl = baseUrl + "/" + method.getName();
        logger.debug("The postUrl is {}", postUrl);

        Map<String, String> params = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String paramsStr = objectMapper.writeValueAsString(args);
        logger.debug("The params string is {}", paramsStr);
//        params = objectMapper.readValue(paramsStr, List.class);
        params.put("params", paramsStr);

        HttpClientUtil httpClientUtil = new HttpClientUtil(connectionManagerFactory, defaultRequestConfig);
        logger.debug("Default connection pool idle connection time is " + connectionManagerFactory.getIdleConnTimeout());
        String result = httpClientUtil.post(postUrl, params).getBody();
        logger.debug("The request return " + result);
        JavaType javaType = objectMapper.getTypeFactory().constructType(method.getGenericReturnType());
        javaType = objectMapper.getTypeFactory().constructParametricType(HttpJsonResponse.class, javaType);
        HttpJsonResponse httpJsonResponse = objectMapper.readValue(result, javaType);

        if(httpJsonResponse.getErr()==null){
            return httpJsonResponse.getVal();
        }else {
            throw ExceptionUtil.getThrowableInstance(httpJsonResponse.getErr());
        }
//        HttpJsonResponse httpJsonResponse = objectMapper.readValue(result, javaType);
//        JsonNode jsonNode = objectMapper.readTree(result);
//        if (jsonNode.get("err") == null || jsonNode.get("err") instanceof NullNode) {
//            logger.debug(jsonNode.get("val").asText());
//            Type returnType = method.getGenericReturnType();
//            logger.debug("The return type is {}", returnType.toString());
//            if (returnType instanceof ParameterizedType) {
//                logger.error(jsonNode.get("val").getNodeType().toString());
//                Type actualType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
//                JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, (Class<?>)actualType);
//                return objectMapper.readValue(jsonNode.get("val").textValue(), listType);
//            }else {
//                logger.error(jsonNode.get("val").getNodeType().toString());
//                return objectMapper.readValue(jsonNode.get("val").traverse(), method.getReturnType());
//            }
//        } else {
//            if (!ExceptionUtil.isExistClass(jsonNode.path("err").get("name").textValue())) {
//                throw new ProxyException(jsonNode.path("err").get("msg").textValue());
//            } else {
//                throw ExceptionUtil.getThrowableInstance(jsonNode.path("err").get("name").textValue(), jsonNode.path("err").get("msg").textValue());
//            }
//        }
//        logger.debug(httpJsonResponse.getVal().toString());
    }

    public Object getObject() {
        Class<?>[] interfaces = {inter};
        return Proxy.newProxyInstance(inter.getClassLoader(), interfaces, new HttpJsonProxy(inter, url, connectionManagerFactory, defaultRequestConfig));
    }

    public void setInter(Class<?> inter) {
        this.inter = inter;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDefaultRequestConfig(DefaultRequestConfig defaultRequestConfig) {
        this.defaultRequestConfig = defaultRequestConfig;
    }

    public void setConnectionManagerFactory(PoolingConnectionManagerFactory connectionManagerFactory) {
        this.connectionManagerFactory = connectionManagerFactory;
    }

}
