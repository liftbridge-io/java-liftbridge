package io.liftbridge;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import io.liftbridge.MessageHandler;
import io.liftbridge.proto.APIGrpc.APIStub;
import io.liftbridge.proto.APIGrpc.APIBlockingStub;
import io.liftbridge.proto.APIGrpc;
import io.liftbridge.proto.Api;
import com.google.protobuf.ByteString;

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
    public void subscribe(String streamName, MessageHandler msgHandler, SubscriptionOptions opts) {
        asyncStub.subscribe(opts.asRequest(), new StreamObserver<Api.Message>() {
                public void onNext(Api.Message message) {
                    msgHandler.onMessage(io.liftbridge.Message.fromWire(message));
                }
                public void onError(Throwable t) {
                    msgHandler.onError(t);
                }
                public void onCompleted() {}
            });
    }

    public void publishToSubject(String subject, byte[] payload) {
        Api.Message msg = Api.Message.newBuilder()
            .setSubject(subject)
            .setValue(ByteString.copyFrom(payload))
            .build();

        Api.PublishRequest req = Api.PublishRequest.newBuilder()
            .setMessage(msg)
            .build();

        blockingStub.publish(req);
    }
}
