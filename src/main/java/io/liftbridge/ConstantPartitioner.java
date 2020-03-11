package io.liftbridge;

public class ConstantPartitioner implements Partitioner {
    private int partition = 0;

    public ConstantPartitioner() {}

    public ConstantPartitioner(int partition) {
        this.partition = partition;
    }

    public int partition(
        String streamName, byte[] messageKey, byte[] messagePayload,
        MessageOptions opts) {
        return this.partition;
    }
}
