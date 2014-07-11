package com.shangpin.http;

import org.apache.http.Consts;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

import java.util.Date;
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

    public PoolingConnectionManagerFactory() {
        this.connectionManager = new PoolingHttpClientConnectionManager();
    }

    Object getObject() {
//        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
        ConnectionConfig connectionConfig = ConnectionConfig.custom().setCharset(Consts.UTF_8).build();

        connectionManager.setDefaultConnectionConfig(connectionConfig);
        connectionManager.setDefaultSocketConfig(socketConfig);

        connectionManager.setMaxTotal(maxTotalConnection);
        connectionManager.setDefaultMaxPerRoute(maxPerRoute);
        //每次获取连接池管理器的时候才释放一次空闲连接远远不够
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
