package io.liftbridge;

/**
 * {@link Partitioner} which maps messages to the same partition.
 */
public class ConstantPartitioner implements Partitioner {

    private int partition = 0;

    /**
     * Creates a new {@code ConstantPartitioner} which always maps messages to the base (zeroth) partition.
     */
    public ConstantPartitioner() {
    }

    /**
     * Creates a new {@code ConstantPartitioner} which always maps messages to the specified partition.
     *
     * @param partition partition to map messages to
     */
    public ConstantPartitioner(int partition) {
        this.partition = partition;
    }

    /**
     * Computes the partition number for a given message. This will always return the same partition number.
     *
     * @param stream name of the stream being published to
     * @param key    message key
     * @param value  message value
     * @param opts   {@code MessageOptions}
     * @return stream partition number
     */
    public int partition(String stream, byte[] key, byte[] value, MessageOptions opts) {
        return this.partition;
    }
}
