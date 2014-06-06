package com.shangpin.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Qiang on 6/3/14.
 */
public class HttpUtil {

    public static String post(String url,
                              java.util.List<NameValuePair> params)
            throws ClientProtocolException, IOException{
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost post= new HttpPost(url);

        String result = "";
        if(null != params){
            post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        }
        try{
            SSLContext sslcontext = SSLContext.getInstance("TLS");

            sslcontext.init(null, new TrustManager[]{new X509TrustManager() {

                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] x509Certificates, String s)
                        throws java.security.cert.CertificateException {
                }

                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] x509Certificates, String s)
                        throws java.security.cert.CertificateException {
                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            }}, null);
            SSLSocketFactory sf = new SSLSocketFactory(sslcontext,
                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
            Scheme https = new Scheme("https", 443, sf);
            httpclient.getConnectionManager().getSchemeRegistry().register(http);
            httpclient.getConnectionManager().getSchemeRegistry().register(https);

            HttpResponse response = httpclient.execute(post);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        httpclient.getConnectionManager().shutdown();
        return result;
    }
}
