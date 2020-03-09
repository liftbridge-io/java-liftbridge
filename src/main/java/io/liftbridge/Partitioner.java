package io.liftbridge;

public interface Partitioner {
    public int partition(
        String streamName, byte[] messageKey, byte[] messagePayload,
        MessageOptions opts);
}
