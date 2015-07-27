package org.nofdev.http;

import org.apache.http.Consts;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * Created by Qiang on 7/3/14.
 */
public class PoolingConnectionManagerFactory {

    private static final Logger logger = LoggerFactory.getLogger(PoolingConnectionManagerFactory.class);

    private PoolingHttpClientConnectionManager connectionManager;

    //<!-- 连接池设置 -->
    /**
     * 最大连接数，缺省为500
     */
    private int maxTotalConnection = 500;

    /**
     * 每个路由最大的连接数 默认为50 如果客户端需要连接的服务器端只有一个，可以设置maxTotalConnection和maxPerRoute相同
     */
    private int maxPerRoute = 50;

    /**
     * 闲置超时时间，缺省为30秒钟
     */
    private long idleConnTimeout = 30000;

    public PoolingConnectionManagerFactory() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this(false);
    }

    public PoolingConnectionManagerFactory(boolean isSecure) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        if(isSecure){//TODO 默认信任全部，不安全
            SSLContextBuilder sslContextBuilder = SSLContexts.custom().loadTrustMaterial(null,new TrustStrategy(){
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            });
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build(),SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create().register("https", sslConnectionSocketFactory).build();
            this.connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        }
        else {
            this.connectionManager = new PoolingHttpClientConnectionManager();
        }
        IdleConnectionMonitorThread idleConnectionMonitorThread = new IdleConnectionMonitorThread(this);
        idleConnectionMonitorThread.start();
    }

    Object getObject() {
//        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
        ConnectionConfig connectionConfig = ConnectionConfig.custom().setCharset(Consts.UTF_8).build();

        connectionManager.setDefaultConnectionConfig(connectionConfig);
        connectionManager.setDefaultSocketConfig(socketConfig);

        connectionManager.setMaxTotal(maxTotalConnection);
        connectionManager.setDefaultMaxPerRoute(maxPerRoute);
        //TODO 每次获取连接池管理器的时候才释放一次空闲连接远远不够
        connectionManager.closeIdleConnections(idleConnTimeout, TimeUnit.SECONDS);

        return connectionManager;
    }

    public Class<?> getObjectType() {
        return PoolingHttpClientConnectionManager.class;
    }

    public boolean isSingleton() {
        return false;
    }

    public void setMaxTotalConnection(int maxTotalConnection) {
        this.maxTotalConnection = maxTotalConnection;
    }

    public void setMaxPerRoute(int maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
    }

    public long getIdleConnTimeout() {
        return idleConnTimeout;
    }

}
