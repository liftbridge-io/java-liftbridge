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
    private HashMap<String, byte[]> headers;
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

	public Long getOffset() {
        return offset;
    }

	private void setOffset(Long offset) {
		this.offset = offset;
	}

    public byte[] getKey() {
		return key;
	}

    private void setKey(byte[] key) {
		this.key = key;
	}

    public byte[] getValue() {
		return value;
	}

    private void setValue(byte[] value) {
		this.value = value;
	}

    public Instant getTimestamp() {
		return timestamp;
	}

    private void setTimestamp(Instant instant) {
        this.timestamp = instant;
	}

    public String getSubject() {
		return subject;
	}

    private void setSubject(String subject) {
		this.subject = subject;
	}

    public String getReplySubject() {
		return replySubject;
	}

    private void setReplySubject(String replySubject) {
		this.replySubject = replySubject;
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

	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}

	public int getPartition() {
		return partition;
	}

	public void setPartition(int partition) {
		this.partition = partition;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}
}
