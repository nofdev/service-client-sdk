package org.nofdev.http;

/**
 * Created by Qiang on 7/4/14.
 */
public class DefaultRequestConfig {
    //<!-- 默认的单次请求设置，如果单次请求有设置的话会被覆盖掉 -->
    /**
     * 回应超时时间，缺省为30秒钟
     */
    private int defaultSoTimeout = 30000;
    /**
     * 连接超时时间，缺省为5秒钟
     */
    private int defaultConnectionTimeout = 10000;
    /**
     * 从连接池中请求一个连接的超时时间，默认等待HttpConnectionManager返回连接超时（只有在达到最大连接数时起作用）：1秒
     */
    private int defaultConnectionRequestTimeout = 1000;

    public int getDefaultSoTimeout() {
        return defaultSoTimeout;
    }

    public void setDefaultSoTimeout(int defaultSoTimeout) {
        this.defaultSoTimeout = defaultSoTimeout;
    }

    public int getDefaultConnectionTimeout() {
        return defaultConnectionTimeout;
    }

    public void setDefaultConnectionTimeout(int defaultConnectionTimeout) {
        this.defaultConnectionTimeout = defaultConnectionTimeout;
    }

    public int getDefaultConnectionRequestTimeout() {
        return defaultConnectionRequestTimeout;
    }

    public void setDefaultConnectionRequestTimeout(int defaultConnectionRequestTimeout) {
        this.defaultConnectionRequestTimeout = defaultConnectionRequestTimeout;
    }
}
