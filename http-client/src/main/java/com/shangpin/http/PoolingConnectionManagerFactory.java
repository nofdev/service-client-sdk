package com.shangpin.http;

import org.apache.http.Consts;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by Qiang on 7/3/14.
 */
public class PoolingConnectionManagerFactory {
    //<!-- 连接池设置 -->
    /**
     * 最大连接数，缺省为500
     */
    private int maxTotalConnection = 500;
    /**
     * 每个路由最大的连接数 默认为50 0为不限制
     */
    private int maxPerRoute = 0;
    /**
     * 闲置超时时间，缺省为60秒钟
     */
    private long idleConnTimeout = 60000;

    Object getObject() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

        SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
        ConnectionConfig connectionConfig = ConnectionConfig.custom().setCharset(Consts.UTF_8).build();

        connectionManager.setDefaultConnectionConfig(connectionConfig);
        connectionManager.setDefaultSocketConfig(socketConfig);

        connectionManager.setMaxTotal(maxTotalConnection);
        connectionManager.setDefaultMaxPerRoute(maxPerRoute);
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
}
