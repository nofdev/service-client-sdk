package org.nofdev.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.nofdev.servicefacade.HttpJsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Qiang on 6/3/14.
 */
public class HttpJsonProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpJsonProxy.class);

    private Class<?> inter;
    @Deprecated
    private String url;

    private ProxyStrategy proxyStrategy;
    private DefaultRequestConfig defaultRequestConfig;
    private PoolingConnectionManagerFactory connectionManagerFactory;

    public HttpJsonProxy(Class<?> inter, ProxyStrategy proxyStrategy, PoolingConnectionManagerFactory connectionManagerFactory, DefaultRequestConfig defaultRequestConfig) {
        this.inter = inter;
        this.proxyStrategy = proxyStrategy;
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

    public HttpJsonProxy(Class<?> inter, ProxyStrategy proxyStrategy) {
        this(inter, proxyStrategy, null, null);
    }

    @Deprecated
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

    @Deprecated
    public HttpJsonProxy(Class<?> inter, String url) {
        this(inter, url, null, null);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, Throwable {
////        TODO
////        if (OBJECT_EQUALS == method) {
////            return equalsInternal(proxy, args[0]);
////        }
//        logger.debug(OBJECT_HASHCODE.getName());
//        logger.debug(method.getName());
//        logger.debug("Is the hashCode method invoke? {}",OBJECT_HASHCODE.equals(method));
//
//        if (OBJECT_HASHCODE.equals(method)) {
//            //TODO should be implementation's hashCode method
//            return proxy.hashCode();
//        }
//        // toString() will fall through to the generic handling.
        if ("hashCode".equals(method.getName())) {
            return inter.hashCode();
        }
        if (proxyStrategy != null) {
            String remoteURL = proxyStrategy.getRemoteURL(inter, method);
            HttpClientUtil httpClientUtil = new HttpClientUtil(connectionManagerFactory, defaultRequestConfig);
            logger.debug("Default connection pool idle connection time is " + connectionManagerFactory.getIdleConnTimeout());
            Map<String, String> params = proxyStrategy.getParams(args);
            HttpMessageSimple response = httpClientUtil.post(remoteURL, params);
            return proxyStrategy.getResult(method, response);
        } else {
            logger.debug("The url is " + url);
            String baseUrl = url.split("\\?")[0];
            logger.debug("The baseUrl is {}", baseUrl);
            String postUrl = baseUrl + "/" + method.getName();
            logger.debug("The postUrl is {}", postUrl);

            Map<String, String> params = new HashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);
            objectMapper.registerModule(new JodaModule());

            String paramsStr = objectMapper.writeValueAsString(args);
            logger.debug("The params string is {}", paramsStr);
//        params = objectMapper.readValue(paramsStr, List.class);
            params.put("params", paramsStr);

            HttpClientUtil httpClientUtil = new HttpClientUtil(connectionManagerFactory, defaultRequestConfig);
            logger.debug("Default connection pool idle connection time is " + connectionManagerFactory.getIdleConnTimeout());
            String result = httpClientUtil.post(postUrl, params).getBody();
            logger.debug("The request return " + result);
            logger.debug("The method return type is {}", method.getGenericReturnType());
            JavaType javaType;
            if (method.getGenericReturnType() != void.class) {
                logger.debug("The method return type is not void");
                javaType = objectMapper.getTypeFactory().constructType(method.getGenericReturnType());
            } else {
                logger.debug("The method return type is void");
                javaType = objectMapper.getTypeFactory().constructType(Object.class);
            }
            javaType = objectMapper.getTypeFactory().constructParametricType(HttpJsonResponse.class, javaType);
            HttpJsonResponse httpJsonResponse = objectMapper.readValue(result, javaType);

            if (httpJsonResponse.getErr() == null) {
                return httpJsonResponse.getVal();
            } else {
                throw ExceptionUtil.getThrowableInstance(httpJsonResponse.getErr());
            }
        }
    }

    public Object getObject() {
        Class<?>[] interfaces = {inter};
        return Proxy.newProxyInstance(inter.getClassLoader(), interfaces, this);
    }

    public void setInter(Class<?> inter) {
        this.inter = inter;
    }

    @Deprecated
    public void setUrl(String url) {
        this.url = url;
    }

    public void setDefaultRequestConfig(DefaultRequestConfig defaultRequestConfig) {
        this.defaultRequestConfig = defaultRequestConfig;
    }

    public void setConnectionManagerFactory(PoolingConnectionManagerFactory connectionManagerFactory) {
        this.connectionManagerFactory = connectionManagerFactory;
    }

    public void setProxyStrategy(ProxyStrategy proxyStrategy) {
        this.proxyStrategy = proxyStrategy;
    }

}
