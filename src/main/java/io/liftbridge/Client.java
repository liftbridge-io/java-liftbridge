package io.liftbridge;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.liftbridge.proto.APIGrpc.APIStub;
import io.liftbridge.proto.APIGrpc.APIBlockingStub;
import io.liftbridge.proto.APIGrpc;
import io.liftbridge.proto.Api;
import com.google.protobuf.ByteString;
import java.util.Map;

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
     * Subscribes to a Liftbridge stream.
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
     * Publishes message to a Liftbridge stream.
     */
    public void publish(
        String streamName, byte[] payload, MessageOptions opts) {
        int partition = opts.getPartitioner().partition(
            streamName, opts.getKey(), payload, opts);

        Api.PublishRequest.Builder requestBuilder =
            Api.PublishRequest.newBuilder()
            .setValue(ByteString.copyFrom(payload))
            .setStream(streamName)
            .setPartition(partition)
            .setKey(ByteString.copyFrom(opts.getKey()));

        for (Map.Entry<String, byte[]> header : opts.getHeaders().entrySet()) {
            requestBuilder.putHeaders(
                header.getKey(), ByteString.copyFrom(header.getValue()));
        }

        blockingStub.withDeadlineAfter(
            opts.getAckDeadlineDuration(),
            opts.getAckDeadlineTimeUnit())
            .publish(requestBuilder.build());
    }

}
