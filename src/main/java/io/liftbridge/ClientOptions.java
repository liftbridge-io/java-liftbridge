package io.liftbridge;

import java.util.HashSet;
import java.util.Set;

/**
 * Used to control the {@link Client} configuration.
 */
class ClientOptions {

    private static final long DEFAULT_KEEP_ALIVE_TIME_MILLIS = 30 * 1000;
    private static final int DEFAULT_MAX_CONNS_PER_BROKER = 2;

    private Set<String> brokers;
    private long keepAliveTimeMillis;
    private int maxConnsPerBroker;

    ClientOptions() {
        brokers = new HashSet<>();
        keepAliveTimeMillis = DEFAULT_KEEP_ALIVE_TIME_MILLIS;
        maxConnsPerBroker = DEFAULT_MAX_CONNS_PER_BROKER;
    }

    public void setBrokers(Set<String> brokers) {
        this.brokers = brokers;
    }

    public Set<String> getBrokers() {
        return brokers;
    }

    public long getKeepAliveTimeMillis() {
        return keepAliveTimeMillis;
    }

    public void setKeepAliveTimeMillis(long keepAliveTimeMillis) {
        this.keepAliveTimeMillis = keepAliveTimeMillis;
    }

    public int getMaxConnsPerBroker() {
        return maxConnsPerBroker;
    }

    public void setMaxConnsPerBroker(int maxConnsPerBroker) {
        this.maxConnsPerBroker = maxConnsPerBroker;
    }
}
