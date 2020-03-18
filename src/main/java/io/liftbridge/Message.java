package io.liftbridge;

import io.liftbridge.proto.Api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.time.Instant;

import com.google.protobuf.ByteString;

/**
 * {@code Message} received from a Liftbridge stream.
 */
public class Message {

    private long offset;
    private byte[] key;
    private byte[] value;
    private int partition;
    private Instant timestamp;
    private String stream;
    private String subject;
    private String replySubject;
    private final HashMap<String, byte[]> headers = new HashMap<>();

    private Message() {
    }

    static Message fromProto(Api.Message wireMsg) {
        Message msg = new Message();

        long tsNanos = wireMsg.getTimestamp();

        msg.offset = wireMsg.getOffset();
        msg.key = wireMsg.getKey().toByteArray();
        msg.value = wireMsg.getValue().toByteArray();
        msg.timestamp = Instant.ofEpochSecond(
                tsNanos / 1_000_000_000,
                tsNanos % 1_000_000_000);
        msg.stream = wireMsg.getStream();
        msg.partition = wireMsg.getPartition();
        msg.subject = wireMsg.getSubject();
        msg.replySubject = wireMsg.getReplySubject();

        for (Map.Entry<String, ByteString> entry : wireMsg.getHeadersMap().entrySet()) {
            msg.headers.put(entry.getKey(), entry.getValue().toByteArray());
        }

        return msg;
    }

    /**
     * Returns the offset, which is a monotonic message sequence in the stream partition.
     *
     * @return message offset
     */
    public long getOffset() {
        return offset;
    }

    /**
     * Returns the optional key set on the message, useful for partitioning and stream compaction.
     *
     * @return message key
     */
    public byte[] getKey() {
        return key;
    }

    /**
     * Returns the message payload.
     *
     * @return message payload
     */
    public byte[] getValue() {
        return value;
    }

    /**
     * Returns the time the message was received by the server.
     *
     * @return message timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the NATS subject the message was received on
     *
     * @return message subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Returns the NATS reply subject on the message, if any.
     *
     * @return message reply subject
     */
    public String getReplySubject() {
        return replySubject;
    }

    /**
     * Returns the message key-value headers.
     *
     * @return message headers
     */
    public Map<String, byte[]> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    /**
     * Returns the name of the stream the message was received on.
     *
     * @return message stream
     */
    public String getStream() {
        return stream;
    }

    /**
     * Returns the stream partition the message was received on.
     *
     * @return message stream partition
     */
    public int getPartition() {
        return partition;
    }

}
