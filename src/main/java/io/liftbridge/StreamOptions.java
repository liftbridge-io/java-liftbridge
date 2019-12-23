package io.liftbridge;

import io.liftbridge.proto.Api.CreateStreamRequest;

public class StreamOptions {
    private CreateStreamRequest.Builder requestBuilder;

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
    public Integer getReplicationFactor() {
        return requestBuilder.getReplicationFactor();
    }

    public StreamOptions setReplicationFactor(Integer replicationFactor) {
        requestBuilder = requestBuilder.setReplicationFactor(replicationFactor);
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
        requestBuilder = requestBuilder.setGroup(group);
        return this;
    }

    /**
     * @return the partitions
     */
    public Integer getPartitions() {
        return requestBuilder.getPartitions();
    }

    /**
     * @param partitions the partitions to set
     */
    public StreamOptions setPartitions(Integer partitions) {
        requestBuilder = requestBuilder.setPartitions(partitions);
        return this;
    }

    CreateStreamRequest asRequest(String stream_name){
        CreateStreamRequest.Builder req_builder = requestBuilder.setName(stream_name);
        if(req_builder.getSubject() == null || req_builder.getSubject() == "") {
            return req_builder.setSubject(stream_name).build();
        }
        return req_builder.build();
    }
}
