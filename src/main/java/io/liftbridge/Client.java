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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@code Client} is the main API used to communicate with a Liftbridge cluster. This is a low-level API in which the
 * user must manage the gRPC channel. See {@link ManagedChannelClient} for a high-level API where the gRPC channel is
 * managed for the user.
 */
public class Client {

    private APIStub asyncStub;
    private APIBlockingStub blockingStub;

    protected Client(ManagedChannel grpcChannel) {
        this.asyncStub = APIGrpc.newStub(grpcChannel);
        this.blockingStub = APIGrpc.newBlockingStub(grpcChannel);
    }

    /**
     * Creates a stream with the given name and default options. Will block until response is received.
     *
     * @throws StreamExistsException if the stream being created already exists
     */
    public void createStream(String name) throws StreamExistsException {
        createStream(name, new StreamOptions());
    }

    /**
     * Creates a stream with the given options. Will block until response is received.
     *
     * @throws StreamExistsException if the stream being created already exists
     */
    public void createStream(String name, StreamOptions opts) throws StreamExistsException {
        try {
            blockingStub.createStream(opts.toProto(name));
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.ALREADY_EXISTS) {
                throw new StreamExistsException(String.format("Stream already exists: %s", name), e);
            }
            throw e;
        }
    }

    /**
     * Deletes a stream and all of its partitions. Name is the stream identifier, globally unique.
     *
     * @param name stream to delete
     * @throws NoSuchStreamException if the stream being deleted does not exist
     */
    public void deleteStream(String name) throws NoSuchStreamException {
        Api.DeleteStreamRequest req = Api.DeleteStreamRequest.newBuilder().setName(name).build();
        try {
            blockingStub.deleteStream(req);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new NoSuchStreamException(String.format("Stream does not exist: %s", name), e);
            }
            throw e;
        }
    }

    /**
     * Creates an ephemeral subscription for the given stream. It begins receiving messages starting at the configured
     * position and waits for new messages when it reaches the end of the stream. The default start position is the end
     * of the stream.
     *
     * @param stream     the stream to subscribe to
     * @param opts       {@link SubscriptionOptions} for configuring the subscription
     * @param msgHandler {@link MessageHandler} for handling messages on the stream
     * @return {@link Subscription} which can be used to unsubscribe
     * @throws NoSuchPartitionException when the stream or partition being subscribed to doesn't exist
     */
    public Subscription subscribe(String stream, SubscriptionOptions opts, MessageHandler msgHandler)
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
                        msgHandler.onMessage(Message.fromProto(message));
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

                        if (t instanceof StatusRuntimeException) {
                            StatusRuntimeException e = (StatusRuntimeException) t;
                            switch (e.getStatus().getCode()) {
                                case CANCELLED:
                                    // Subscription cancellation shouldn't cause an error callback.
                                    return;
                                case NOT_FOUND:
                                    // Indicates the stream was deleted.
                                    t = new StreamDeletedException("stream was deleted", e);
                                case FAILED_PRECONDITION:
                                    // Indicates the partition was paused.
                                    t = new PartitionPausedException("partition was paused", e);
                            }
                        }
                        msgHandler.onError(t);
                    }

                    public void onCompleted() {
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
     * Publishes a new message to a Liftbridge stream. The partition that gets published to is determined by the
     * provided partition or {@link Partitioner} passed through {@code MessageOptions}, if any. If a partition or
     * Partitioner is not provided, this defaults to the base partition. This partition determines the underlying NATS
     * subject that gets published to. To publish directly to a specific NATS subject, use the low-level
     * {@link #publishToSubject} API.
     * <p>
     * If the {@link io.liftbridge.MessageOptions.AckPolicy} is not {@code NONE} and a deadline is provided, this will
     * synchronously block until the ack is received. If the ack is not received in time, a
     * {@link DeadlineExceededException} is thrown. If an {@code AckPolicy} and deadline are configured, this returns
     * the Ack on success, otherwise it returns null.
     *
     * @param stream  name of stream to publish to
     * @param payload message value
     * @param opts    {@code MessageOptions} to configure message
     * @return {@code Ack} if an AckPolicy and deadline are configured or null
     * @throws DeadlineExceededException when the configured deadline was exceeded
     */
    public Ack publish(String stream, byte[] payload, MessageOptions opts) throws DeadlineExceededException {
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

        Api.PublishResponse resp;
        try {
            resp = stub.publish(requestBuilder.build());
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                throw new DeadlineExceededException(e);
            }
            throw e;
        }
        if (!resp.hasAck()) {
            return null;
        }
        return Ack.fromProto(resp.getAck());
    }

    /**
     * Publishes a new message to a NATS subject. Note that because this publishes directly to a subject, there may be
     * multiple (or no) streams that receive the message. As a result, {@code MessageOptions} related to partitioning
     * will be ignored. To publish at the stream/partition level, use the high-level {@link #publish} API.
     * <p>
     * If the {@link io.liftbridge.MessageOptions.AckPolicy} is not {@code NONE} and a deadline is provided, this will
     * synchronously block until the first ack is received. If an ack is not received in time, a
     * {@link DeadlineExceededException} is thrown. If an {@code AckPolicy} and deadline are configured, this returns
     * the first Ack on success, otherwise it returns null.
     *
     * @param subject NATS subject to publish to
     * @param payload message value
     * @param opts    {@code MessageOptions} to configure message
     * @return {@code Ack} if an AckPolicy and deadline are configured or null
     * @throws DeadlineExceededException when the configured deadline was exceeded
     */
    public Ack publishToSubject(String subject, byte[] payload, MessageOptions opts) throws DeadlineExceededException {
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

        Api.PublishToSubjectResponse resp;
        try {
            resp = stub.publishToSubject(requestBuilder.build());
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                throw new DeadlineExceededException(e);
            }
            throw e;
        }
        if (!resp.hasAck()) {
            return null;
        }
        return Ack.fromProto(resp.getAck());
    }

    private Ack publish(Api.PublishRequest req, long deadlineDuration, TimeUnit deadlineTimeUnit)
            throws DeadlineExceededException {

        APIBlockingStub stub = blockingStub;
        if (deadlineDuration > 0) {
            stub = stub.withDeadlineAfter(deadlineDuration, deadlineTimeUnit);
        }

        Api.PublishResponse resp;
        try {
            resp = stub.publish(req);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                throw new DeadlineExceededException(e);
            }
            throw e;
        }
        if (!resp.hasAck()) {
            return null;
        }
        return Ack.fromProto(resp.getAck());
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
