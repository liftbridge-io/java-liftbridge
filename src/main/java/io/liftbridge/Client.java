package io.liftbridge;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.liftbridge.proto.APIGrpc.APIStub;
import io.liftbridge.proto.APIGrpc.APIBlockingStub;
import io.liftbridge.proto.APIGrpc;
import io.liftbridge.proto.Api;
import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * {@code Client} is the main API used to communicate with a Liftbridge cluster. Use {@link Builder#build()} to
 * construct a {@code Client} instance.
 */
public class Client {

    private APIStub asyncStub;
    private APIBlockingStub blockingStub;
    private ManagedChannel channel;

    private Client(ManagedChannel channel) {
        this.channel = channel;
        this.asyncStub = APIGrpc.newStub(channel);
        this.blockingStub = APIGrpc.newBlockingStub(channel);
    }

    /**
     * Closes the client connection by shutting down the gRPC channel and releasing any resources.
     */
    public void close() {
        channel.shutdown();
    }

    /**
     * Creates a stream. Will block until response is received.
     */
    public void createStream(String streamName, StreamOptions opts) {
        blockingStub.createStream(opts.asRequest(streamName));
    }

    /**
     * Creates a stream. Will block until response is received.
     */
    public void createStream(String streamName) {
        createStream(streamName, new StreamOptions());
    }

    /**
     * Subscribes to a stream.
     */
    public void subscribe(String streamName, SubscriptionOptions opts, MessageHandler msgHandler) {
        opts = opts.setStreamName(streamName);
        asyncStub.subscribe(opts.asRequest(), new StreamObserver<Api.Message>() {
            public void onNext(Api.Message message) {
                msgHandler.onMessage(io.liftbridge.Message.fromWire(message));
            }

            public void onError(Throwable t) {
                msgHandler.onError(t);
            }

            public void onCompleted() {
            }
        });
    }

    /**
     * Subscribes to a stream.
     */
    public void subscribe(String streamName, MessageHandler msgHandler) {
        subscribe(streamName, new SubscriptionOptions(), msgHandler);
    }

    /**
     * Publishes message to a NATS subject
     * This is a temporary method until the publish API is done
     */
    public void publish(String subject, byte[] payload,
                        long deadlineDuration, TimeUnit deadlineUnit) {
        Api.Message msg = Api.Message.newBuilder()
                .setSubject(subject)
                .setValue(ByteString.copyFrom(payload))
                .setAckPolicy(Api.AckPolicy.LEADER)
                .build();

        Api.PublishRequest req = Api.PublishRequest.newBuilder()
                .setMessage(msg)
                .build();

        blockingStub.withDeadlineAfter(deadlineDuration, deadlineUnit)
                .publish(req);
    }

    /**
     * {@code Builder} is used to configure and construct a {@link Client} instance.
     */
    public static class Builder {

        private final List<String> addrs = new ArrayList<>();

        private Builder() {
        }

        /**
         * Creates a {@link Builder} that will connect to the given broker address.
         *
         * @param addr broker address
         * @return {@code Builder}
         */
        public static Builder create(String addr) {
            Builder builder = new Builder();
            return builder.withBroker(addr);
        }

        /**
         * Adds the given address to the set of broker hosts the client will use when attempting to connect.
         *
         * @param addr broker address
         * @return {@code this} to allow for call chaining
         */
        Builder withBroker(String addr) {
            addrs.add(addr);
            return this;
        }

        /**
         * Creates a configured {@link Client} instance.
         *
         * @return {@code Client}
         */
        public Client build() {
            if (addrs.size() == 0) {
                throw new RuntimeException("no addresses provided");
            }

            // TODO: Handle multiple addresses.
            ManagedChannelBuilder channelBuilder = ManagedChannelBuilder.forTarget(addrs.get(0));

            // TODO: Implement TLS.
            ManagedChannel channel = channelBuilder.usePlaintext().build();
            return new Client(channel);
        }
    }

}
