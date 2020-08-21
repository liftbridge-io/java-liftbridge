package io.liftbridge;

import java.time.Instant;
import java.util.*;

public class Metadata {

    private Instant lastUpdated = Instant.now();
    private Set<String> addrs = new HashSet<>();
    private Map<String, BrokerInfo> brokers;
    private Map<String, StreamInfo> streams;

    Metadata() {
        brokers = new HashMap<>();
        streams = new HashMap<>();
    }

    Metadata(Map<String, BrokerInfo> brokers, Map<String, StreamInfo> streams) {
        for (BrokerInfo broker : brokers.values()) {
            addrs.add(broker.getAddr());
        }
        this.brokers = brokers;
        this.streams = streams;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public Set<BrokerInfo> getBrokers() {
        Set<BrokerInfo> brokers = new HashSet<>(this.brokers.size());
        brokers.addAll(this.brokers.values());
        return brokers;
    }

    public Set<String> getAddrs() {
        return Collections.unmodifiableSet(addrs);
    }

    public StreamInfo getStream(String name) {
        return streams.get(name);
    }

    int getPartitionCountForStream(String stream) {
        StreamInfo info = getStream(stream);
        if (info == null) {
            return 0;
        }
        return info.getPartitions().size();
    }

    boolean hasStreamMetadata(String stream) {
        return getStream(stream) != null;
    }

}
