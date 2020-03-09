package io.liftbridge;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.liftbridge.proto.APIGrpc.APIStub;
import io.liftbridge.proto.APIGrpc.APIBlockingStub;
import io.liftbridge.proto.APIGrpc;
import io.liftbridge.proto.Api;

/**
 * {@code Client} is the main API used to communicate with a Liftbridge cluster. Use {@link Builder#build()} to
 * construct a {@code Client} instance.
 */
public class Client {

    private APIStub asyncStub;
    private APIBlockingStub blockingStub;

    protected Client(ManagedChannel grpcChannel) {
        this.asyncStub = APIGrpc.newStub(grpcChannel);
        this.blockingStub = APIGrpc.newBlockingStub(grpcChannel);
    }

    /**
     * Closes the client connection by shutting down the gRPC channel and releasing any resources.
     */
    public static Client connect(ManagedChannel grpcChannel) {
        return new Client(grpcChannel);
    }

    /**
     * Creates a stream. Will block until response is received.
     */
    public void createStream(String streamName) {
        createStream(streamName, new StreamOptions());
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
    public void subscribe(String streamName, SubscriptionOptions opts,
                          MessageHandler msgHandler) {
        asyncStub.subscribe(
            opts.toWire(streamName), new StreamObserver<Api.Message>() {
                    public void onNext(Api.Message message) {
                        msgHandler.onMessage(
                            io.liftbridge.Message.fromWire(message));
                    }

                    public void onError(Throwable t) {
                        msgHandler.onError(t);
                    }

                    public void onCompleted() {}
                }
            );
    }

    /**
     * Publishes message to a Liftbridge stream.
     */
    public void publish(
        String streamName, byte[] payload, MessageOptions opts) {
        Api.PublishRequest request = PublishRequest.build(streamName, payload,
                                                          opts);

        blockingStub.withDeadlineAfter(
            opts.getAckDeadlineDuration(),
            opts.getAckDeadlineTimeUnit())
            .publish(request);
    }

}
