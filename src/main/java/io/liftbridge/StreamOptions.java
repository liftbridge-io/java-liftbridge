package io.liftbridge;

import io.liftbridge.proto.Api.CreateStreamRequest;

/**
 * {@code StreamOptions} are used to configure Liftbridge streams.
 */
public class StreamOptions {

    private String subject;
    private int replicationFactor = 1;
    private String group = "";
    private int partitions = 1;

    /**
     * Creates a new {@code StreamOptions} for configuring a stream.
     */
    public StreamOptions() {
    }

    /**
     * Returns the NATS subject the stream will be attached to.
     *
     * @return NATS subject
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * Sets the NATS subject to attach the stream to. If this is not set it will default to the stream name.
     *
     * @param subject NATS subject
     * @return {@code this} to allow for chaining
     */
    public StreamOptions setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    /**
     * Returns the replication factor for the stream. The replication factor controls the number of servers to
     * replicate a stream to. E.g. a value of 1 would mean only 1 server would have the data, and a value of 3 would be
     * 3 servers would have it. A value of -1 will signal to the server to set the replication factor equal to the
     * current number of servers in the cluster.
     *
     * @return stream replication factor
     */
    public int getReplicationFactor() {
        return this.replicationFactor;
    }

    /**
     * Sets the replication factor for the stream. The replication factor controls the number of servers to replicate a
     * stream to. E.g. a value of 1 would mean only 1 server would have the data, and a value of 3 would be 3 servers
     * would have it. If this is not set, it defaults to 1. A value of -1 will signal to the server to set the
     * replication factor equal to the current number of servers in the cluster.
     *
     * @param replicationFactor stream replication factor
     * @return {@code this} to allow for chaining
     */
    public StreamOptions setReplicationFactor(int replicationFactor) {
        this.replicationFactor = replicationFactor;
        return this;
    }

    /**
     * Returns the load-balance group for the stream. When there are multiple streams in the same group, messages will
     * be balanced among them.
     *
     * @return load-balance group name
     */
    public String getGroup() {
        return this.group;
    }

    /**
     * Sets the load-balance group for the stream. When there are multiple streams in the same group, messages will be
     * balanced among them.
     *
     * @param group load-balance group name
     * @return {@code this} to allow for chaining
     */
    public StreamOptions setGroup(String group) {
        this.group = group;
        return this;
    }

    /**
     * Returns the number of stream partitions. Partitions are ordered, replicated, and durably stored on disk and serve
     * as the unit of storage and parallelism for a stream. A partitioned stream for NATS subject "foo.bar" with three
     * partitions internally maps to the NATS subjects "foo.bar", "foo.bar.1", and "foo.bar.2". A single partition would
     * map to "foo.bar" to match behavior of an "un-partitioned" stream. If this is not set, it defaults to 1.
     *
     * @return number of partitions
     */
    public int getPartitions() {
        return this.partitions;
    }

    /**
     * Sets the number of partitions for the stream. Partitions are ordered, replicated, and durably stored on disk and
     * serve as the unit of storage and parallelism for a stream. A partitioned stream for NATS subject "foo.bar" with
     * three partitions internally maps to the NATS subjects "foo.bar", "foo.bar.1", and "foo.bar.2". A single partition
     * would map to "foo.bar" to match behavior of an "un-partitioned" stream. If this is not set, it defaults to 1.
     *
     * @param partitions number of partitions
     * @return {@code this} to allow for chaining
     */
    public StreamOptions setPartitions(int partitions) {
        if (partitions < 0) {
            throw new RuntimeException("partitions cannot be less than zero");
        }
        this.partitions = partitions;
        return this;
    }

    /**
     * Creates a {@link CreateStreamRequest} from the {@code StreamOptions} that can be sent to the server to create a
     * stream with the given name. If a NATS subject is not set on the options, the stream name will be used. Stream
     * names must be globally unique within a cluster.
     *
     * @param streamName name of stream to create
     * @return {@code CreateStreamRequest}
     */
    CreateStreamRequest toProto(String streamName) {
        String subject = this.getSubject();
        if (subject == null || subject.equals("")) {
            subject = streamName;
        }
        CreateStreamRequest.Builder reqBuilder =
                CreateStreamRequest.newBuilder()
                        .setName(streamName)
                        .setSubject(subject)
                        .setReplicationFactor(this.getReplicationFactor())
                        .setGroup(this.getGroup())
                        .setPartitions(this.getPartitions());

        return reqBuilder.build();
    }
}
