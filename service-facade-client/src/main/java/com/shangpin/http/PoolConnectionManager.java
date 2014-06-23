package com.shangpin.http;

import org.apache.http.Consts;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by liuyang on 2014/6/18.
 */
public class PoolConnectionManager {

    private int defaultConnectionTimeout = 5000;
    /** 连接超时时间，缺省为5秒钟*/
    public int getDefaultConnectionTimeout() {
        return defaultConnectionTimeout;
    }
    public void setDefaultConnectionTimeout(int defaultConnectionTimeout) {
        this.defaultConnectionTimeout = defaultConnectionTimeout;
    }


    private int defaultSoTimeout = 30000;
    /** 回应超时时间，缺省为30秒钟*/
    public int getDefaultSoTimeout() {
        return defaultSoTimeout;
    }
    public void setDefaultSoTimeout(int defaultSoTimeout) {
        this.defaultSoTimeout = defaultSoTimeout;
    }


    private int defaultMaxTotalConnection = 500;
    /** 最大连接数，缺省为500*/
    public int getDefaultMaxTotalConnection() {
        return defaultMaxTotalConnection;
    }
    public void setDefaultMaxTotalConnection(int defaultMaxTotalConnection) {
        this.defaultMaxTotalConnection = defaultMaxTotalConnection;
    }


    private long defaultIdleConnTimeout = 60000;
    /** 闲置超时时间，缺省为60秒钟 */
    public long getDefaultIdleConnTimeout() {
        return defaultIdleConnTimeout;
    }



    private int defaultHttpConnectionManagerTimeout = 3000;
    /** 默认等待HttpConnectionManager返回连接超时（只有在达到最大连接数时起作用）：1秒*/
    public int getDefaultHttpConnectionManagerTimeout() {
        return defaultHttpConnectionManagerTimeout;
    }



    private int defaultMaxTotalRoute = 0;
    /** 每个路由最大的连接数 默认为50*/
    public int getDefaultMaxTotalRoute() {
        return defaultMaxTotalRoute;
    }



    private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

    public PoolingHttpClientConnectionManager getPoolInstance(){
        poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        return poolingHttpClientConnectionManager;
    }


    public PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
        SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
        ConnectionConfig connectionConfig = ConnectionConfig.custom().setCharset(Consts.UTF_8).build();
        poolingHttpClientConnectionManager.setMaxTotal(defaultMaxTotalConnection);
        poolingHttpClientConnectionManager.setDefaultConnectionConfig(connectionConfig);
        poolingHttpClientConnectionManager.setDefaultSocketConfig(socketConfig);
        poolingHttpClientConnectionManager.closeIdleConnections(defaultIdleConnTimeout, TimeUnit.SECONDS);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(defaultMaxTotalRoute);
        return poolingHttpClientConnectionManager;
    }

}
