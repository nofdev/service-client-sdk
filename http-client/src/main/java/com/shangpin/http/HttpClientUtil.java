package com.shangpin.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * http连接工具
 */
public class HttpClientUtil {
    private static Logger logger = Logger.getLogger(HttpClientUtil.class);

    private PoolingConnectionManagerFactory connectionManagerFactory;
    private DefaultRequestConfig defaultRequestConfig;
    private HttpClient httpClient;

    public HttpClientUtil(PoolingConnectionManagerFactory connectionManagerFactory, DefaultRequestConfig defaultRequestConfig) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        this.connectionManagerFactory = connectionManagerFactory;
        this.defaultRequestConfig = defaultRequestConfig;
        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setConnectionManager((PoolingHttpClientConnectionManager) connectionManagerFactory.getObject());
        this.httpClient = httpClientBuilder.build();
    }

    public HttpClientUtil(PoolingConnectionManagerFactory connectionManagerFactory) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this(connectionManagerFactory, new DefaultRequestConfig());
    }

//    /**
//     * 默认构造参数，全部使用默认连接池设置，不建议使用
//     */
//    public HttpClientUtil() {
//        this(new PoolingConnectionManagerFactory(), new DefaultRequestConfig(),false);
//        }

    public HttpMessageSimple post(String url, Map<String, String> params) throws IOException {
        HttpPost post = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(defaultRequestConfig.getDefaultConnectionRequestTimeout())
                .setConnectTimeout(defaultRequestConfig.getDefaultConnectionTimeout())
                .setSocketTimeout(defaultRequestConfig.getDefaultSoTimeout())
                .setExpectContinueEnabled(false)
                .build();
        post.setConfig(requestConfig);

        List<NameValuePair> pairList = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            pairList.add(nameValuePair);
        }
        post.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
        HttpResponse httpResponse = httpClient.execute(post);
        HttpEntity httpEntity = httpResponse.getEntity();
        String body = EntityUtils.toString(httpEntity);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        String contentType;
        if(httpEntity.getContentType()==null){
            contentType = null;
        }else {
            contentType = httpEntity.getContentType().getValue();
        }
        logger.debug("response entity is " + body);
        post.releaseConnection();
        return new HttpMessageSimple(statusCode,contentType,body);
    }

    public HttpMessageSimple get(String url, Map<String, String> params) throws IOException {

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(defaultRequestConfig.getDefaultConnectionRequestTimeout())
                .setConnectTimeout(defaultRequestConfig.getDefaultConnectionTimeout())
                .setSocketTimeout(defaultRequestConfig.getDefaultSoTimeout())
                .setExpectContinueEnabled(false)
                .build();


        List<NameValuePair> pairList = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            pairList.add(nameValuePair);
        }

        url = url+"?"+EntityUtils.toString(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
        HttpGet get = new HttpGet(url);
        get.setConfig(requestConfig);
        HttpResponse httpResponse = httpClient.execute(get);
        HttpEntity httpEntity = httpResponse.getEntity();
        String body = EntityUtils.toString(httpEntity);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        String contentType;
        if(httpEntity.getContentType()==null){
            contentType = null;
        }else {
            contentType = httpEntity.getContentType().getValue();
        }
        logger.debug("response entity is " + body);
        get.releaseConnection();
        return new HttpMessageSimple(statusCode,contentType,body);
    }
}
