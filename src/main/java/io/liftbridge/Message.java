package io.liftbridge;

import io.liftbridge.proto.Api;

import java.util.HashMap;

public class Message {
    private Long offset;
    private byte[] key;
    private byte[] value;
    private Long timestampNanos;
    private String subject;
    private String replySubject;
    private HashMap<String, byte[]> headers;

    private Message() {
    }

    static Message fromWire(Api.Message wireMsg) {
        Message msg = new Message();
        msg.setOffset(wireMsg.getOffset());
        msg.setValue(wireMsg.getValue().toByteArray());
        msg.setKey(wireMsg.getKey().toByteArray());
        msg.setTimestampNanos(wireMsg.getTimestamp());
        msg.setSubject(wireMsg.getSubject());
        msg.setReplySubject(wireMsg.getReply());
        return msg;
    }

    /**
     * @return the offset
     */
    public Long getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    private void setOffset(Long offset) {
        this.offset = offset;
    }

    /**
     * @return the key
     */
    public byte[] getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    private void setKey(byte[] key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public byte[] getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    private void setValue(byte[] value) {
        this.value = value;
    }

    /**
     * @return the timestampNanos
     */
    public Long getTimestampNanos() {
        return timestampNanos;
    }

    /**
     * @param timestampNanos the timestampNanos to set
     */
    private void setTimestampNanos(Long timestampNanos) {
        this.timestampNanos = timestampNanos;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    private void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the replySubject
     */
    public String getReplySubject() {
        return replySubject;
    }

    /**
     * @param replySubject the replySubject to set
     */
    private void setReplySubject(String replySubject) {
        this.replySubject = replySubject;
    }

    /**
     * @return the headers
     */
    public HashMap<String, byte[]> getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    private void setHeaders(HashMap<String, byte[]> headers) {
        this.headers = headers;
    }
}
