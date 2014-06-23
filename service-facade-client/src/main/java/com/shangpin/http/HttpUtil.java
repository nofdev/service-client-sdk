package com.shangpin.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Qiang on 6/3/14.
 */
public class HttpUtil {
    private static Logger logger = Logger.getLogger(HttpUtil.class);

    public static void main(String[] args){
        final String url = "http://localhost:9090/web-admin-sample/demo/index1";
        try {
            for(int i = 0; i < 10; i++){
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            System.out.println("====================>"+this.getName());
                            HttpClient httpClient = HttpClients.createDefault();
                            HttpPost post = new HttpPost(url);
                            HttpResponse response = httpClient.execute(post);
                            System.out.println(EntityUtils.toString(response.getEntity()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static String post(String url, java.util.List<NameValuePair> params, PoolConnectionManager connectionManager) throws ClientProtocolException, IOException {

        HttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager.getPoolingHttpClientConnectionManager())
                .build();
        HttpPost post = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionManager.getDefaultHttpConnectionManagerTimeout())
                .setConnectTimeout(connectionManager.getDefaultConnectionTimeout())
                .setSocketTimeout(connectionManager.getDefaultSoTimeout())
                .setExpectContinueEnabled(false)
                .build();
        post.setConfig(requestConfig);
        post.setEntity(new UrlEncodedFormEntity(params, Charset.forName("UTF-8")));
        HttpResponse httpResponse = httpClient.execute(post);
        HttpEntity httpEntity = httpResponse.getEntity();
        String result = EntityUtils.toString(httpEntity);
        logger.debug("response entity is " + result);
        post.releaseConnection();
        return result;

//        String result = "";
//        if(null != params){
//            post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
//        }
//        try{
//            SSLContext sslcontext = SSLContext.getInstance("TLS");
//
//            sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
//
//                public void checkClientTrusted(
//                        java.security.cert.X509Certificate[] x509Certificates, String s)
//                        throws java.security.cert.CertificateException {
//                }
//
//                public void checkServerTrusted(
//                        java.security.cert.X509Certificate[] x509Certificates, String s)
//                        throws java.security.cert.CertificateException {
//                }
//
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    return new java.security.cert.X509Certificate[0];
//                }
//            }}, null);
//            SSLSocketFactory sf = new SSLSocketFactory(sslcontext,
//                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//            Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
//            Scheme https = new Scheme("https", 443, sf);
//            httpclient.getConnectionManager().getSchemeRegistry().register(http);
//            httpclient.getConnectionManager().getSchemeRegistry().register(https);
//
//            HttpResponse response = httpclient.execute(post);
//            HttpEntity entity = response.getEntity();
//            result = EntityUtils.toString(entity);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
//
//        httpclient.getConnectionManager().shutdown();
//        return result;
    }
}
