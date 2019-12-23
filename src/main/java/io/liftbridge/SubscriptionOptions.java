package io.liftbridge;

import io.liftbridge.proto.Api.SubscribeRequest;
import io.liftbridge.proto.Api;

public class SubscriptionOptions {
    private SubscribeRequest.Builder requestBuilder;
    private StartPosition startPosition;
    private MessageHandler messageHandler;

    public SubscriptionOptions() {
        requestBuilder = SubscribeRequest.newBuilder();
        this.setStartPosition(new StartAtNewOnly());
    }

    /**
     * Specifies the stream partition to consume. Defaults to 0.
     */
    public SubscriptionOptions setPartition(int partition) {
        requestBuilder = requestBuilder.setPartition(partition);
        return this;
    }

    public int getPartitions() {
        return requestBuilder.getPartition();
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public String getStreamName() {
        return requestBuilder.getStream();
    }

    public StartPosition getStartPosition() {
        return this.startPosition;
    }

    public SubscriptionOptions setPartition(Integer partition) {
        requestBuilder.setPartition(partition);
        return this;
    }

    public SubscriptionOptions setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
        return this;
    }

    public SubscriptionOptions setStreamName(String streamName) {
        requestBuilder.setStream(streamName);
        return this;
    }

    public SubscriptionOptions setStartPosition(StartPosition position) {
        requestBuilder = position.setRequestBuilderParameters(requestBuilder);
        this.startPosition = position;
        return this;
    }

    SubscribeRequest asRequest() {
        return requestBuilder.build();
    }

    public abstract class StartPosition {
        abstract SubscribeRequest.Builder setRequestBuilderParameters(SubscribeRequest.Builder builder);
    }

    public class StartAtNewOnly extends StartPosition {
        StartAtNewOnly() {}
        SubscribeRequest.Builder setRequestBuilderParameters(SubscribeRequest.Builder builder) {
            return builder.setStartPosition(Api.StartPosition.NEW_ONLY);
        }
    }

    public class StartAtEarliestReceived extends StartPosition {
        SubscribeRequest.Builder setRequestBuilderParameters(SubscribeRequest.Builder builder) {
            return builder.setStartPosition(Api.StartPosition.EARLIEST);
        }
    }

    public class StartAtLatestReceived extends StartPosition {
        SubscribeRequest.Builder setRequestBuilderParameters(SubscribeRequest.Builder builder) {
            return builder.setStartPosition(Api.StartPosition.LATEST);
        }
    }

    public class StartAtOffset extends StartPosition {
        private Long offset;

        StartAtOffset(Long offset) {
            this.offset = offset;
        }

        SubscribeRequest.Builder setRequestBuilderParameters(SubscribeRequest.Builder builder) {
            return builder.setStartPosition(Api.StartPosition.OFFSET).setStartOffset(offset);
        }
    }

    public class StartAtTimestamp extends StartPosition {
        private Long timestampNanoSeconds;

        StartAtTimestamp(Long timestampNanoSeconds) {
            this.timestampNanoSeconds = timestampNanoSeconds;
        }

        SubscribeRequest.Builder setRequestBuilderParameters(SubscribeRequest.Builder builder) {
            return builder.setStartPosition(Api.StartPosition.TIMESTAMP)
                .setStartTimestamp(this.timestampNanoSeconds);
        }
    }
}
