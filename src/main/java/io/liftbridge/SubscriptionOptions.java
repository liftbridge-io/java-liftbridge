package io.liftbridge;

import io.liftbridge.proto.Api;
import java.time.Instant;

public class SubscriptionOptions {
    private int partition = 0;
    private StartPosition startPosition = new StartAtEarliestReceived();

    public SubscriptionOptions() {
        this.setStartPosition(new StartAtNewOnly());
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

    public StartPosition getStartPosition() {
        return this.startPosition;
    }

    public SubscriptionOptions setStartPosition(StartPosition position) {
        this.startPosition = position;
        return this;
    }

    Api.SubscribeRequest toWire(String streamName) {
        Api.SubscribeRequest.Builder requestBuilder =
            Api.SubscribeRequest.newBuilder()
            .setStream(streamName)
            .setPartition(partition);
        startPosition.setRequestBuilderParameters(requestBuilder);

        return requestBuilder.build();
    }

    public abstract static class StartPosition {
        abstract Api.SubscribeRequest.Builder setRequestBuilderParameters(
            Api.SubscribeRequest.Builder builder);
    }

    public static class StartAtNewOnly extends StartPosition {
        StartAtNewOnly() {}
        Api.SubscribeRequest.Builder setRequestBuilderParameters(
            Api.SubscribeRequest.Builder builder) {
            return builder.setStartPosition(Api.StartPosition.NEW_ONLY);
        }
    }

    public static class StartAtEarliestReceived extends StartPosition {
        Api.SubscribeRequest.Builder setRequestBuilderParameters(
            Api.SubscribeRequest.Builder builder) {
            return builder.setStartPosition(Api.StartPosition.EARLIEST);
        }
    }

    public static class StartAtLatestReceived extends StartPosition {
        Api.SubscribeRequest.Builder setRequestBuilderParameters(
            Api.SubscribeRequest.Builder builder) {
            return builder.setStartPosition(Api.StartPosition.LATEST);
        }
    }

    public static class StartAtOffset extends StartPosition {
        private Long offset;

        StartAtOffset(Long offset) {
            this.offset = offset;
        }

        Api.SubscribeRequest.Builder setRequestBuilderParameters(
            Api.SubscribeRequest.Builder builder) {
            return builder
                .setStartPosition(Api.StartPosition.OFFSET)
                .setStartOffset(offset);
        }
    }

    public static class StartAtInstant extends StartPosition {
        private Instant instant;

        StartAtInstant(Instant instant) {
            this.instant = instant;
        }

        Api.SubscribeRequest.Builder setRequestBuilderParameters(
            Api.SubscribeRequest.Builder builder) {
            return builder.setStartPosition(Api.StartPosition.TIMESTAMP)
                .setStartTimestamp(
                    this.instant.getEpochSecond() * 1_000_000L + this.instant.getNano());
        }
    }
}
