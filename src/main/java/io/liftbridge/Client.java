package io.liftbridge;

import io.grpc.ManagedChannel;
import io.liftbridge.MessageHandler;
import io.liftbridge.proto.APIGrpc.APIStub;
import io.liftbridge.proto.APIGrpc.APIBlockingStub;
import io.liftbridge.proto.APIGrpc;

public class Client {
    private APIStub asyncStub;
    private APIBlockingStub blockingStub;
    private Client(APIStub asyncStub, APIBlockingStub blockingStub) {
        this.asyncStub = asyncStub;
        this.blockingStub = blockingStub;
    }

    /**
     * Creates a client connected to the API using the provided gRPC channel.
     */
    public static Client connect(ManagedChannel grpcChannel) {
        return new Client(APIGrpc.newStub(grpcChannel),
                          APIGrpc.newBlockingStub(grpcChannel));
    }


    /**
     * Creates a stream. Will block until response is received.
     */
    public void createStream(String streamName, StreamOptions opts) {
        blockingStub.createStream(opts.asRequest(streamName));
    }

    /**
     * Subscribes to a stream.
     */
    public void subscribe(String streamName, SubscriptionOptions opts) {

    }
}
