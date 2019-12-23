package io.liftbridge;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import io.liftbridge.MessageHandler;
import io.liftbridge.proto.APIGrpc.APIStub;
import io.liftbridge.proto.APIGrpc.APIBlockingStub;
import io.liftbridge.proto.APIGrpc;
import io.liftbridge.proto.Api;
import com.google.protobuf.ByteString;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

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
    public void subscribe(String streamName, MessageHandler msgHandler,
                          SubscriptionOptions opts) {
        opts = opts.setStreamName(streamName);
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

    /**
     * Publishes message to a NATS subject
     * This is a temporary method until the publish API is done
     */
    public void publish(String subject, ByteBuffer payload,
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
}
