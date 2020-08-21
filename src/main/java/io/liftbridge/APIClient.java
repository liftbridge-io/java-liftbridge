package io.liftbridge;

import com.google.protobuf.ByteString;
import io.grpc.Context;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.liftbridge.exceptions.*;
import io.liftbridge.proto.APIGrpc;
import io.liftbridge.proto.APIGrpc.APIBlockingStub;
import io.liftbridge.proto.APIGrpc.APIStub;
import io.liftbridge.proto.Api;

import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@code APIClient} is a low-level API in which the user must manage the gRPC channel. This is for communicating with a
 * particular Liftbridge cluster node and generally should not be used directly. Instead see {@link Client} for the
 * high-level user-facing API.
 */
class APIClient {

    private final APIStub asyncStub;
    private final APIBlockingStub blockingStub;
    private final ManagedChannel grpcChannel;

    protected APIClient(ManagedChannel grpcChannel) {
        this.asyncStub = APIGrpc.newStub(grpcChannel);
        this.blockingStub = APIGrpc.newBlockingStub(grpcChannel);
        this.grpcChannel = grpcChannel;
    }

    public Api.FetchMetadataResponse fetchMetadata() throws StatusRuntimeException {
        Api.FetchMetadataRequest req = Api.FetchMetadataRequest.newBuilder().build();
        return blockingStub.fetchMetadata(req);
    }

    /**
     * Creates a stream with the given options. Will block until response is received.
     */
    public void createStream(String name, StreamOptions opts) throws StatusRuntimeException {
        blockingStub.createStream(opts.toProto(name));
    }

    /**
     * Deletes a stream and all of its partitions. Name is the stream identifier, globally unique.
     */
    public void deleteStream(String name) throws StatusRuntimeException {
        Api.DeleteStreamRequest req = Api.DeleteStreamRequest.newBuilder().setName(name).build();
        blockingStub.deleteStream(req);
    }

    /**
     * Creates an ephemeral subscription for the given stream. It begins receiving messages starting at the configured
     * position and waits for new messages when it reaches the end of the stream. The default start position is the end
     * of the stream.
     *
     * @param stream     the stream to subscribe to
     * @param opts       {@link SubscriptionOptions} for configuring the subscription
     * @param msgHandler {@link StreamObserver} for handling messages on the stream
     * @return {@link Subscription} which can be used to unsubscribe
     * @throws NoSuchPartitionException when the stream or partition being subscribed to doesn't exist
     */
    public Subscription subscribe(String stream, SubscriptionOptions opts, StreamObserver<Api.Message> msgHandler)
            throws NoSuchPartitionException {

        Context.CancellableContext ctx = Context.current().withCancellation();
        AtomicBoolean gotHandshake = new AtomicBoolean();
        SynchronousQueue<MaybeThrowable> handshake = new SynchronousQueue<>();

        ctx.run(() -> asyncStub.subscribe(opts.toProto(stream), new StreamObserver<Api.Message>() {
                    public void onNext(Api.Message message) {
                        // On initial subscribe, the server will either send an empty message, indicating the
                        // subscription was successfully created, or an error.
                        if (!gotHandshake.get()) {
                            gotHandshake.set(true);
                            try {
                                handshake.put(MaybeThrowable.isNull());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                        msgHandler.onNext(message);
                    }

                    public void onError(Throwable t) {
                        if (!gotHandshake.get()) {
                            gotHandshake.set(true);
                            try {
                                handshake.put(MaybeThrowable.notNull(t));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        msgHandler.onError(t);
                    }

                    public void onCompleted() {
                        msgHandler.onCompleted();
                    }
                }
        ));

        try {
            Throwable t = handshake.take().maybe();
            if (t != null) {
                if (!(t instanceof StatusRuntimeException)) {
                    throw new StatusRuntimeException(Status.fromThrowable(t));
                }
                StatusRuntimeException e = (StatusRuntimeException) t;
                if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                    throw new NoSuchPartitionException(e);
                }
                throw e;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Subscription.fromGrpc(ctx);
    }

    /**
     * Publishes a new message to a Liftbridge stream.
     */
    public Api.PublishResponse publish(String stream, byte[] payload, MessageOptions opts) throws StatusRuntimeException {
        Integer partition = opts.getPartition();
        if (partition == null) {
            partition = opts.getPartitioner().partition(stream, opts.getKey(), payload, opts);
        }

        Api.PublishRequest.Builder requestBuilder = Api.PublishRequest.newBuilder()
                .setStream(stream)
                .setPartition(partition)
                .setAckPolicy(opts.getAckPolicy().toProto());
        if (payload != null) {
            requestBuilder.setValue(ByteString.copyFrom(payload));
        }
        if (opts.getAckInbox() != null) {
            requestBuilder.setAckInbox(opts.getAckInbox());
        }
        if (opts.getCorrelationId() != null) {
            requestBuilder.setCorrelationId(opts.getCorrelationId());
        }

        byte[] msgKey = opts.getKey();
        if (msgKey != null) {
            requestBuilder.setKey(ByteString.copyFrom(msgKey));
        }

        Map<String, byte[]> headers = opts.getHeaders();
        if (headers != null) {
            for (Map.Entry<String, byte[]> header : headers.entrySet()) {
                requestBuilder.putHeaders(
                        header.getKey(), ByteString.copyFrom(header.getValue()));
            }
        }

        APIBlockingStub stub = blockingStub;
        if (opts.getAckDeadlineDuration() > 0) {
            stub = stub.withDeadlineAfter(opts.getAckDeadlineDuration(), opts.getAckDeadlineTimeUnit());
        }

        return stub.publish(requestBuilder.build());
    }

    /**
     * Publishes a new message to a NATS subject.
     */
    public Api.PublishToSubjectResponse publishToSubject(String subject, byte[] payload, MessageOptions opts) throws StatusRuntimeException {
        Api.PublishToSubjectRequest.Builder requestBuilder = Api.PublishToSubjectRequest.newBuilder()
                .setSubject(subject)
                .setAckPolicy(opts.getAckPolicy().toProto());
        if (payload != null) {
            requestBuilder.setValue(ByteString.copyFrom(payload));
        }
        if (opts.getAckInbox() != null) {
            requestBuilder.setAckInbox(opts.getAckInbox());
        }
        if (opts.getCorrelationId() != null) {
            requestBuilder.setCorrelationId(opts.getCorrelationId());
        }

        byte[] msgKey = opts.getKey();
        if (msgKey != null) {
            requestBuilder.setKey(ByteString.copyFrom(msgKey));
        }

        Map<String, byte[]> headers = opts.getHeaders();
        if (headers != null) {
            for (Map.Entry<String, byte[]> header : headers.entrySet()) {
                requestBuilder.putHeaders(
                        header.getKey(), ByteString.copyFrom(header.getValue()));
            }
        }

        APIBlockingStub stub = blockingStub;
        if (opts.getAckDeadlineDuration() > 0) {
            stub = stub.withDeadlineAfter(opts.getAckDeadlineDuration(), opts.getAckDeadlineTimeUnit());
        }

        return stub.publishToSubject(requestBuilder.build());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof APIClient)) {
            return false;
        }
        APIClient other = (APIClient) o;
        if (other.grpcChannel == null && this.grpcChannel == null) {
            return true;
        }
        if (other.grpcChannel == null) {
            return false;
        }
        return other.grpcChannel.equals(this.grpcChannel);
    }

    @Override
    public int hashCode() {
        if (this.grpcChannel == null) {
            return 0;
        }
        return this.grpcChannel.hashCode();
    }

    private static class MaybeThrowable {

        private final Throwable t;

        private MaybeThrowable(Throwable t) {
            this.t = t;
        }

        private static MaybeThrowable isNull() {
            return new MaybeThrowable(null);
        }

        private static MaybeThrowable notNull(Throwable t) {
            return new MaybeThrowable(t);
        }

        private Throwable maybe() {
            return t;
        }

    }

}
