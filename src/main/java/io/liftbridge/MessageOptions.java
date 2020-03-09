package io.liftbridge;

import io.liftbridge.proto.Api;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MessageOptions {
    private String ackInbox;
    private String correlationId;
    private AckPolicy ackPolicy = AckPolicy.NONE;
    private HashMap<String, byte[]> headers;
    private byte[] key;
    private Partitioner partitioner = new ConstantPartitioner();
    private long ackDeadlineDuration;
    private TimeUnit ackDeadlineTimeUnit;

    public MessageOptions() {}

    public byte[] getKey() {
		return key;
	}
	public MessageOptions setKey(byte[] key) {
		this.key = key;
        return this;
	}

	public HashMap<String, byte[]> getHeaders() {
		return headers;
	}

	public MessageOptions setHeaders(HashMap<String, byte[]> headers) {
		this.headers = headers;
        return this;
    }

	public String getAckInbox() {
		return ackInbox;
	}

	public MessageOptions setAckInbox(String ackInbox) {
		this.ackInbox = ackInbox;
        return this;
    }

	public String getCorrelationId() {
		return correlationId;
	}

	public MessageOptions setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
        return this;
    }

	public AckPolicy getAckPolicy() {
		return ackPolicy;
	}

	public MessageOptions setAckPolicy(AckPolicy ackPolicy) {
		this.ackPolicy = ackPolicy;
        return this;
    }

    public Partitioner getPartitioner() {
        return partitioner;
    }

    public MessageOptions setPartitioner(Partitioner partitioner) {
        this.partitioner = partitioner;
        return this;
    }

	public long getAckDeadlineDuration() {
		return ackDeadlineDuration;
	}

	public MessageOptions setAckDeadlineDuration(long ackDeadlineDuration) {
		this.ackDeadlineDuration = ackDeadlineDuration;
        return this;
    }

	public TimeUnit getAckDeadlineTimeUnit() {
		return ackDeadlineTimeUnit;
	}

	public MessageOptions setAckDeadlineTimeUnit(
        TimeUnit ackDeadlineTimeUnit) {
		this.ackDeadlineTimeUnit = ackDeadlineTimeUnit;
        return this;
    }

    public enum AckPolicy {
        LEADER, ALL, NONE, UNRECOGNIZED;

        Api.AckPolicy toWire() {
            switch(this){
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
    }
}
