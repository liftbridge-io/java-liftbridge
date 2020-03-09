package io.liftbridge;

import io.liftbridge.proto.Api.CreateStreamRequest;

public class StreamOptions {
    private String subject;
    private int replicationFactor = 1;
    private String group = "";
    private int partitions = 1;

    public StreamOptions() {}

    public String getSubject() {
        return this.subject;
    }

    public StreamOptions setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public int getReplicationFactor() {
        return this.replicationFactor;
    }

    public StreamOptions setReplicationFactor(int replicationFactor) {
        this.replicationFactor = replicationFactor;
        return this;
    }

    public String getGroup() {
        return this.group;
    }

    public StreamOptions setGroup(String group) {
        this.group = group;
        return this;
    }

    public int getPartitions() {
        return this.partitions;
    }

    public StreamOptions setPartitions(int partitions) {
        this.partitions = partitions;
        return this;
    }

    CreateStreamRequest asRequest(String streamName){
        CreateStreamRequest.Builder reqBuilder =
            CreateStreamRequest.newBuilder()
            .setName(streamName)
            .setReplicationFactor(this.getReplicationFactor())
            .setGroup(this.getGroup())
            .setPartitions(this.getPartitions());

        if(reqBuilder.getSubject() == null || reqBuilder.getSubject() == "") {
            return reqBuilder.setSubject(streamName).build();
        }

        return reqBuilder.build();
    }
}
