package io.liftbridge;

import io.liftbridge.proto.Api.CreateStreamRequest;

public class StreamOptions {

    private final CreateStreamRequest.Builder requestBuilder;

    public StreamOptions() {
        requestBuilder = CreateStreamRequest.newBuilder();
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return requestBuilder.getSubject();
    }

    public StreamOptions setSubject(String subject) {
        requestBuilder.setSubject(subject);
        return this;
    }

    /**
     * @return the replicationFactor
     */
    public int getReplicationFactor() {
        return requestBuilder.getReplicationFactor();
    }

    public StreamOptions setReplicationFactor(int replicationFactor) {
        requestBuilder.setReplicationFactor(replicationFactor);
        return this;
    }

    /**
     * @return the group
     */
    public String getGroup() {
        return requestBuilder.getGroup();
    }

    /**
     * @param group the group to set
     */
    public StreamOptions setGroup(String group) {
        requestBuilder.setGroup(group);
        return this;
    }

    /**
     * @return the partitions
     */
    public int getPartitions() {
        return requestBuilder.getPartitions();
    }

    /**
     * @param partitions the partitions to set
     */
    public StreamOptions setPartitions(int partitions) {
        requestBuilder.setPartitions(partitions);
        return this;
    }

    CreateStreamRequest asRequest(String streamName) {
        CreateStreamRequest.Builder reqBuilder = requestBuilder.setName(streamName);
        if (reqBuilder.getSubject() == null || reqBuilder.getSubject().isEmpty()) {
            return reqBuilder.setSubject(streamName).build();
        }
        return reqBuilder.build();
    }
}
