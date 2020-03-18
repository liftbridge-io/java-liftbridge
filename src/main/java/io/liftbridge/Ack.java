package io.liftbridge;

import io.liftbridge.proto.Api;

/**
 * {@code Ack} represents an acknowledgement that a message was committed to a stream partition.
 */
public class Ack {

    private String stream;
    private String partitionSubject;
    private String messageSubject;
    private long offset;
    private String ackInbox;
    private String correlationId;
    private MessageOptions.AckPolicy ackPolicy;

    private Ack() {
    }

    static Ack fromProto(Api.Ack wireAck) {
        Ack ack = new Ack();

        ack.stream = wireAck.getStream();
        ack.partitionSubject = wireAck.getPartitionSubject();
        ack.messageSubject = wireAck.getMsgSubject();
        ack.offset = wireAck.getOffset();
        ack.ackInbox = wireAck.getAckInbox();
        ack.correlationId = wireAck.getCorrelationId();
        ack.ackPolicy = MessageOptions.AckPolicy.fromProto(wireAck.getAckPolicy());

        return ack;
    }

    /**
     * Returns the stream the message was received on.
     *
     * @return stream name
     */
    public String getStream() {
        return stream;
    }

    /**
     * Returns the NATS subject the stream partition is attached to.
     *
     * @return NATS subject of stream partition
     */
    public String getPartitionSubject() {
        return partitionSubject;
    }

    /**
     * Returns the NATS subject the message was received on.
     *
     * @return NATS subject of message
     */
    public String getMessageSubject() {
        return messageSubject;
    }

    /**
     * Returns the partition offset the message was committed to.
     *
     * @return partition offset of message
     */
    public long getOffset() {
        return offset;
    }

    /**
     * Returns the NATS subject the ack was published to.
     *
     * @return NATS subject of ack
     */
    public String getAckInbox() {
        return ackInbox;
    }

    /**
     * Returns the user-supplied correlation id from the message.
     *
     * @return message correlation id
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Returns the {@link io.liftbridge.MessageOptions.AckPolicy} set on the message.
     *
     * @return message {@code AckPolicy}
     */
    public MessageOptions.AckPolicy getAckPolicy() {
        return ackPolicy;
    }

}
