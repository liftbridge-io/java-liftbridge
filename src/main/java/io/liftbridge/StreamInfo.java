package io.liftbridge;

import java.util.Collections;
import java.util.Map;

public class StreamInfo {

    private final String subject;
    private final String name;
    private final Map<Integer, PartitionInfo> partitions;

    StreamInfo(String name, String subject, Map<Integer, PartitionInfo> partitions) {
        this.name = name;
        this.subject = subject;
        this.partitions = partitions;
    }

    public PartitionInfo getPartition(int id) {
        return partitions.get(id);
    }

    public Map<Integer, PartitionInfo> getPartitions() {
        return Collections.unmodifiableMap(partitions);
    }

}
