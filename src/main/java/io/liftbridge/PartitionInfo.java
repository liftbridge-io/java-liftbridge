package io.liftbridge;

import java.util.Collections;
import java.util.Set;

public class PartitionInfo {

    private final int id;
    private final BrokerInfo leader;
    private final Set<BrokerInfo> replicas;
    private final Set<BrokerInfo> isr;

    PartitionInfo(int id, BrokerInfo leader, Set<BrokerInfo> replicas, Set<BrokerInfo> isr) {
        this.id = id;
        this.leader = leader;
        this.replicas = replicas;
        this.isr = isr;
    }

    public int getId() {
        return id;
    }

    public Set<BrokerInfo> getReplicas() {
        return Collections.unmodifiableSet(replicas);
    }

    public Set<BrokerInfo> getISR() {
        return Collections.unmodifiableSet(isr);
    }

    public BrokerInfo getLeader() {
        return leader;
    }

}
