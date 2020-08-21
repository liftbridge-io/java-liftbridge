package io.liftbridge;

import io.grpc.StatusRuntimeException;
import io.liftbridge.proto.Api;

import java.util.*;

class MetadataCache {

    private Metadata metadata;
    private final Set<String> bootstrapAddrs;
    private final DoResilientRPC<Api.FetchMetadataResponse> doRPC;
    private final Random random = new Random();

    MetadataCache(Set<String> addrs, DoResilientRPC<Api.FetchMetadataResponse> doRPC) {
        this.bootstrapAddrs = new HashSet<>(addrs);
        this.doRPC = doRPC;
        this.metadata = new Metadata();
    }

    Set<String> getAddrs() {
        Set<String> addrs = new HashSet<>(bootstrapAddrs);
        addrs.addAll(getMetadata().getAddrs());
        return addrs;
    }

    String getAddr(String streamName, int partitionId, boolean readISRReplica) {
        Metadata meta = getMetadata();
        StreamInfo stream = meta.getStream(streamName);
        if (stream == null) {
            return null;
        }
        PartitionInfo partition = stream.getPartition(partitionId);
        if (partition == null) {
            return null;
        }
        // Request to subscribe to a random ISR replica.
        if (readISRReplica) {
            Set<BrokerInfo> isr = partition.getISR();
            int size = isr.size();
            int i = 0;
            int item = random.nextInt(size);
            for (BrokerInfo broker : isr) {
                if (i == item) {
                    return broker.getAddr();
                }
                i++;
            }
        }
        BrokerInfo leader = partition.getLeader();
        if (leader == null) {
            return null;
        }
        return leader.getAddr();
    }

    synchronized Metadata getMetadata() {
        return metadata;
    }

    Metadata update() throws StatusRuntimeException {
        Api.FetchMetadataResponse resp = doRPC.execute(APIClient::fetchMetadata);

        Map<String, BrokerInfo> brokers = new HashMap<>();
        for (Api.Broker broker : resp.getBrokersList()) {
            brokers.put(broker.getId(), new BrokerInfo(broker.getId(), broker.getHost(), broker.getPort()));
        }

        Map<String, StreamInfo> streams = new HashMap<>();
        for (Api.StreamMetadata streamMetadata : resp.getMetadataList()) {
            Map<Integer, PartitionInfo> partitions = new HashMap<>();
            for (Api.PartitionMetadata partitionMetadata : streamMetadata.getPartitionsMap().values()) {
                Set<BrokerInfo> replicas = new HashSet<>();
                for (String replica : partitionMetadata.getReplicasList()) {
                    replicas.add(brokers.get(replica));
                }
                Set<BrokerInfo> isr = new HashSet<>();
                for (String replica : partitionMetadata.getIsrList()) {
                    isr.add(brokers.get(replica));
                }
                PartitionInfo partition = new PartitionInfo(
                        partitionMetadata.getId(),
                        brokers.get(partitionMetadata.getLeader()),
                        replicas,
                        isr
                );
                partitions.put(partitionMetadata.getId(), partition);
            }
            StreamInfo stream = new StreamInfo(streamMetadata.getName(), streamMetadata.getSubject(), partitions);
            streams.put(streamMetadata.getName(), stream);
        }

        Metadata updated = new Metadata(brokers, streams);
        synchronized (this) {
            this.metadata = updated;
        }
        return updated;
    }

    interface DoResilientRPC<T> {
        T execute(RPC<T> rpc) throws StatusRuntimeException;
    }

}
