package org.nofdev.http;

import org.apache.http.conn.HttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Qiang on 7/11/14.
 */
public class IdleConnectionMonitorThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(IdleConnectionMonitorThread.class);

    private PoolingConnectionManagerFactory connectionManagerFactory;

    private final HttpClientConnectionManager connMgr;
    private volatile boolean shutdown;

    public IdleConnectionMonitorThread(PoolingConnectionManagerFactory connectionManagerFactory) {
        super();
        this.connectionManagerFactory = connectionManagerFactory;
        this.connMgr = (HttpClientConnectionManager)connectionManagerFactory.getObject();
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(5000);
                    //logger.info(new Date()+"Closing idle connection");
                    // Close expired connections
                    connMgr.closeExpiredConnections();
                    // Optionally, close connections
                    // that have been idle longer than 30 sec
                    connMgr.closeIdleConnections(connectionManagerFactory.getIdleConnTimeout(), TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
