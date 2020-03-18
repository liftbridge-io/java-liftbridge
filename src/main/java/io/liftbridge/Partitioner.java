package io.liftbridge;

/**
 * {@code Partitioner} is used to map a message to a stream partition.
 */
public interface Partitioner {

    /**
     * Computes the partition number for a given message.
     *
     * @param stream name of the stream being published to
     * @param key    message key
     * @param value  message value
     * @param opts   {@code MessageOptions}
     * @return stream partition number
     */
    int partition(String stream, byte[] key, byte[] value, MessageOptions opts);

}
