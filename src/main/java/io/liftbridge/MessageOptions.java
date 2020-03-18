package io.liftbridge;

import io.liftbridge.proto.Api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * {@code MessageOptions} are used to configure optional settings for a message.
 */
public class MessageOptions {

    private String ackInbox;
    private String correlationId;
    private AckPolicy ackPolicy = AckPolicy.LEADER;
    private final HashMap<String, byte[]> headers = new HashMap<>();
    private byte[] key;
    private Partitioner partitioner = new ConstantPartitioner();
    private Integer partition;
    private long ackDeadlineDuration;
    private TimeUnit ackDeadlineTimeUnit;

    public MessageOptions() {
    }

    /**
     * Returns the optional message key. If Liftbridge has stream compaction enabled, the stream will retain only the
     * last value for each key. Keys are also useful for stream partitioning.
     *
     * @return message key or null if there is no key
     */
    public byte[] getKey() {
        return key;
    }

    /**
     * Sets an optional key on the message. If Liftbridge has stream compaction enabled, the stream will retain only
     * the last value for each key. Keys are also useful for stream partitioning.
     *
     * @param key message key
     * @return {@code this} to allow for chaining
     */
    public MessageOptions setKey(byte[] key) {
        this.key = key;
        return this;
    }

    /**
     * Returns an immutable view of the message headers.
     *
     * @return message headers
     */
    public Map<String, byte[]> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    /**
     * Adds a header key-value pair to the message.
     *
     * @param key   header key
     * @param value header value
     * @return {@code this} to allow for chaining
     */
    public MessageOptions putHeader(String key, byte[] value) {
        this.headers.put(key, value);
        return this;
    }

    /**
     * Returns the NATS subject Liftbridge should publish the message ack to.
     *
     * @return NATS subject to publish ack to
     */
    public String getAckInbox() {
        return ackInbox;
    }

    /**
     * Sets the NATS subject Liftbridge should publish the message ack to. If this is not set, the server will generate
     * a random inbox.
     *
     * @param ackInbox NATS subject to publish ack to
     * @return {@code this} to allow for chaining
     */
    public MessageOptions setAckInbox(String ackInbox) {
        this.ackInbox = ackInbox;
        return this;
    }

    /**
     * Returns the identifier used to correlate an ack with the published message.
     *
     * @return correlation id
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Sets the identifier used to correlate an ack with the published message. If this is not set, the ack will not
     * have a correlation id.
     *
     * @param correlationId id to correlate ack to message
     * @return {@code this} to allow for chaining
     */
    public MessageOptions setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    /**
     * Returns the {@link AckPolicy} for the message. {@code AckPolicy} controls the behavior of message acks sent by
     * the server. By default, Liftbridge will send an ack when the partition leader has written the message to its
     * write-ahead log.
     *
     * @return {@code AckPolicy}
     */
    public AckPolicy getAckPolicy() {
        return ackPolicy;
    }

    /**
     * Sets the {@link AckPolicy} for the message. {@code AckPolicy} controls the behavior of message acks sent by the
     * server. By default, Liftbridge will send an ack when the partition leader has written the message to its
     * write-ahead log.
     *
     * @param ackPolicy {@code AckPolicy} to set on message
     * @return {@code this} to allow for chaining
     */
    public MessageOptions setAckPolicy(AckPolicy ackPolicy) {
        this.ackPolicy = ackPolicy;
        return this;
    }

    /**
     * Returns the {@link Partitioner} strategy for mapping the message to a stream partition. If a specific partition
     * is set, this will not be used.
     *
     * @return {@code Partitioner}
     */
    public Partitioner getPartitioner() {
        return partitioner;
    }

    /**
     * Sets the {@link Partitioner} strategy for mapping the message to a stream partition. If a specific partition is
     * set, this will not be used.
     *
     * @param partitioner {@code Partitioner} for selecting partition
     * @return {@code this} to allow for chaining
     */
    public MessageOptions setPartitioner(Partitioner partitioner) {
        this.partitioner = partitioner;
        return this;
    }

    /**
     * Returns the stream partition to map the message to. If this is set, any {@link Partitioner} provided will not be
     * used. A null value indicates no partition is set.
     *
     * @return stream partition
     */
    public Integer getPartition() {
        return partition;
    }

    /**
     * Sets the stream partition to map the message to. If this is set, any {@link Partitioner} provided will not be
     * used.
     *
     * @param partition partition to publish message to
     * @return {@code this} to allow for chaining
     */
    public MessageOptions setPartition(int partition) {
        this.partition = partition;
        return this;
    }

    /**
     * Returns the time to wait for an ack. If the {@link AckPolicy} is not {@code NONE} and a deadline is provided,
     * publish calls will synchronously block until the ack is received. If the ack is not received in time, a
     * {@code DeadlineExceeded} exception is thrown.
     *
     * @return time to wait for ack
     */
    public long getAckDeadlineDuration() {
        return ackDeadlineDuration;
    }

    /**
     * Returns the time unit for the ack deadline duration.
     *
     * @return time unit of duration
     */
    public TimeUnit getAckDeadlineTimeUnit() {
        return ackDeadlineTimeUnit;
    }

    /**
     * Sets the deadline to receive a message ack. If the {@link AckPolicy} is not {@code NONE} and a deadline is
     * provided, publish calls will synchronously block until the ack is received. If the ack is not received in time,
     * a {@code DeadlineExceeded} exception is thrown.
     *
     * @param ackDeadlineDuration time to wait for ack
     * @param ackDeadlineTimeUnit time unit of duration
     * @return {@code this} to allow for chaining
     */
    public MessageOptions setAckDeadline(long ackDeadlineDuration, TimeUnit ackDeadlineTimeUnit) {
        this.ackDeadlineDuration = ackDeadlineDuration;
        this.ackDeadlineTimeUnit = ackDeadlineTimeUnit;
        return this;
    }

    /**
     * {@code AckPolicy} controls the behavior of message acknowledgements.
     */
    public enum AckPolicy {
        LEADER, ALL, NONE, UNRECOGNIZED;

        Api.AckPolicy toProto() {
            switch (this) {
                case LEADER:
                    return Api.AckPolicy.LEADER;
                case ALL:
                    return Api.AckPolicy.ALL;
                case NONE:
                    return Api.AckPolicy.NONE;
                default:
                    return Api.AckPolicy.UNRECOGNIZED;
            }
        }

        static AckPolicy fromProto(Api.AckPolicy ackPolicy) {
            switch (ackPolicy) {
                case LEADER:
                    return LEADER;
                case ALL:
                    return ALL;
                case NONE:
                    return NONE;
                default:
                    return UNRECOGNIZED;
            }
        }
    }
}
