package io.liftbridge;

import io.liftbridge.proto.Api;

import java.time.Instant;
import java.time.temporal.TemporalAmount;

/**
 *
 */
public class SubscriptionOptions {

    private int partition = 0;
    private StartPosition startPosition = new StartAtNewOnly();

    public SubscriptionOptions() {
    }

    /**
     * Specifies the stream partition to consume. Defaults to 0.
     */
    public SubscriptionOptions setPartition(int partition) {
        this.partition = partition;
        return this;
    }

    public int getPartition() {
        return this.partition;
    }

    /**
     * Sets the subscription start position to the earliest message received in the stream.
     *
     * @return {@code this} to allow for chaining
     */
    public SubscriptionOptions startAtEarliestReceived() {
        this.startPosition = new StartAtEarliestReceived();
        return this;
    }

    /**
     * Sets the subscription start position to the latest message received in the stream.
     *
     * @return {@code this} to allow for chaining
     */
    public SubscriptionOptions startAtLatestReceived() {
        this.startPosition = new StartAtLatestReceived();
        return this;
    }

    /**
     * Sets the desired start offset to begin consuming from in the stream.
     *
     * @param offset offset to start consuming from
     * @return {@code this} to allow for chaining
     */
    public SubscriptionOptions startAtOffset(long offset) {
        this.startPosition = new StartAtOffset(offset);
        return this;
    }

    /**
     * Sets the desired timestamp to begin consuming from in the stream.
     *
     * @param instant timestamp to start consuming from
     * @return {@code this} to allow for chaining
     */
    public SubscriptionOptions startAtTime(Instant instant) {
        this.startPosition = new StartAtTime(instant);
        return this;
    }

    /**
     * Sets the desired timestamp to begin consuming from in the stream using a time delta in the past.
     *
     * @param delta amount of time in the past to start consuming from
     * @return {@code this} to allow for chaining
     */
    public SubscriptionOptions startAtTimeDelta(TemporalAmount delta) {
        this.startPosition = new StartAtTime(Instant.now().minus(delta));
        return this;
    }

    Api.SubscribeRequest toProto(String streamName) {
        Api.SubscribeRequest.Builder requestBuilder =
                Api.SubscribeRequest.newBuilder()
                        .setStream(streamName)
                        .setPartition(partition);
        startPosition.setRequestBuilderParameters(requestBuilder);

        return requestBuilder.build();
    }

    abstract static class StartPosition {
        abstract Api.SubscribeRequest.Builder setRequestBuilderParameters(Api.SubscribeRequest.Builder builder);
    }

    static class StartAtNewOnly extends StartPosition {

        StartAtNewOnly() {
        }

        Api.SubscribeRequest.Builder setRequestBuilderParameters(Api.SubscribeRequest.Builder builder) {
            return builder.setStartPosition(Api.StartPosition.NEW_ONLY);
        }
    }

    static class StartAtEarliestReceived extends StartPosition {
        Api.SubscribeRequest.Builder setRequestBuilderParameters(Api.SubscribeRequest.Builder builder) {
            return builder.setStartPosition(Api.StartPosition.EARLIEST);
        }
    }

    static class StartAtLatestReceived extends StartPosition {
        Api.SubscribeRequest.Builder setRequestBuilderParameters(Api.SubscribeRequest.Builder builder) {
            return builder.setStartPosition(Api.StartPosition.LATEST);
        }
    }

    static class StartAtOffset extends StartPosition {

        private final long offset;

        StartAtOffset(long offset) {
            this.offset = offset;
        }

        Api.SubscribeRequest.Builder setRequestBuilderParameters(Api.SubscribeRequest.Builder builder) {
            return builder.setStartPosition(Api.StartPosition.OFFSET).setStartOffset(offset);
        }
    }

    static class StartAtTime extends StartPosition {

        private final Instant instant;

        StartAtTime(Instant instant) {
            this.instant = instant;
        }

        Api.SubscribeRequest.Builder setRequestBuilderParameters(Api.SubscribeRequest.Builder builder) {
            return builder.setStartPosition(Api.StartPosition.TIMESTAMP).setStartTimestamp(
                    this.instant.getEpochSecond() * 1_000_000L + this.instant.getNano());
        }
    }
}
