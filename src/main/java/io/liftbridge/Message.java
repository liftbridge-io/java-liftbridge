package io.liftbridge;

import io.liftbridge.proto.Api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.time.Instant;
import com.google.protobuf.ByteString;

public class Message {
    private Long offset;
    private byte[] key;
    private byte[] value;
    private int partition;
    private Instant timestamp;
    private String streamName;
    private String subject;
    private String replySubject;
    private HashMap<String, byte[]> headers = new HashMap<String, byte[]>();
    private String correlationId;

    private Message() {
    }

    static Message fromWire(Api.Message wireMsg) {
        Message msg = new Message();

        Long tsNanos = wireMsg.getTimestamp();

        msg.setOffset(wireMsg.getOffset());
        msg.setKey(wireMsg.getKey().toByteArray());
        msg.setValue(wireMsg.getValue().toByteArray());
        msg.setTimestamp(Instant.ofEpochSecond(
                             tsNanos / 1_000_000_000,
                             tsNanos % 1_000_000_000));
        msg.setStreamName(wireMsg.getStream());
        msg.setPartition(wireMsg.getPartition());
        msg.setSubject(wireMsg.getSubject());
        msg.setReplySubject(wireMsg.getReplySubject());
        msg.setCorrelationId(wireMsg.getCorrelationId());

        for (Map.Entry<String, ByteString> entry :
                 wireMsg.getHeadersMap().entrySet()) {
            msg.putHeader(entry.getKey(), entry.getValue().toByteArray());
        }

        return msg;
    }

	private void setOffset(Long offset) {
		this.offset = offset;
	}

    private void setKey(byte[] key) {
		this.key = key;
	}

    private void setValue(byte[] value) {
		this.value = value;
	}

    private void setTimestamp(Instant instant) {
        this.timestamp = instant;
	}

    private void setSubject(String subject) {
		this.subject = subject;
	}

    private void setReplySubject(String replySubject) {
		this.replySubject = replySubject;
	}

    private void setStreamName(String streamName) {
		this.streamName = streamName;
	}

    private void setPartition(int partition) {
		this.partition = partition;
	}

	private void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

    public Long getOffset() {
        return offset;
    }

	public byte[] getKey() {
		return key;
	}

    public byte[] getValue() {
		return value;
	}

    public Instant getTimestamp() {
		return timestamp;
	}

    public String getSubject() {
		return subject;
	}

    public String getReplySubject() {
		return replySubject;
	}

    public Map<String, byte[]> getHeaders() {
		return Collections.unmodifiableMap(headers);
	}

    private void putHeader(String key, byte[] value) {
		this.headers.put(key, value);
	}

	public String getStreamName() {
		return streamName;
	}

	public int getPartition() {
		return partition;
	}

	public String getCorrelationId() {
		return correlationId;
	}
}
