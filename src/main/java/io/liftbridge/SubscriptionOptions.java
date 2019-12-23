package io.liftbridge;

import io.liftbridge.proto.Api.SubscribeRequest;

public class SubscriptionOptions {
    private SubscribeRequest.Builder requestBuilder;
    private MessageHandler messageHandler;
    public SubscriptionOptions() {
        requestBuilder = SubscribeRequest.newBuilder();
    }

    /**
     * Specifies the stream partition to consume. Defaults to 0.
     */
    public SubscriptionOptions setPartition(int partition) {
        requestBuilder = requestBuilder.setPartition(partition);
        return this;
    }

    public int getPartition() {
        return requestBuilder.getPartition();
    }

    public SubscriptionOptions setStartPosition() {
        return this;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }
}
