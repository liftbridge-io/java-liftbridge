package io.liftbridge;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.liftbridge.exceptions.*;
import io.liftbridge.proto.Api;

import java.util.*;

/**
 * {@code Client} is the primary API component for interacting with a Liftbridge cluster. Use {@link Client.Builder} to
 * configure a {@code Client} instance.
 */
public class Client {

    private ManagedAPIClient conn;
    private boolean closed;
    private MetadataCache metadataCache;
    private final Map<String, ConnectionPool> pools = new HashMap<>();
    private final ClientOptions opts;

    private Client(ManagedAPIClient client, ClientOptions opts) {
        this.conn = client;
        this.opts = opts;
    }

    private void setMetadataCache(MetadataCache metadataCache) {
        this.metadataCache = metadataCache;
    }

    /**
     * Closes the client connection and any associated resources.
     */
    public synchronized void close() {
        if (closed) {
            return;
        }
        for (ConnectionPool pool : pools.values()) {
            pool.close();
        }
        getAPIClient().close();
        closed = true;
    }

    /**
     * Creates a stream with the given name and default options. This will use the stream name as the NATS subject to
     * attach the stream to.
     *
     * @throws StreamExistsException if the stream being created already exists
     */
    public void createStream(String name) throws StreamExistsException {
        createStream(name, new StreamOptions());
    }

    /**
     * Creates a stream with the given name and options.
     *
     * @throws StreamExistsException if the stream being created already exists
     */
    public void createStream(String name, StreamOptions opts) throws StreamExistsException {
        try {
            doResilientRPC((RPC<Void>) client -> {
                client.createStream(name, opts);
                return null;
            });
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
        try {
            doResilientRPC((RPC<Void>) client -> {
                client.deleteStream(name);
                return null;
            });
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
        NoSuchPartitionException ex = null;
        for (int i = 0; i < 5; i++) {
            PoolAndAddr poolAndAddr;
            try {
                poolAndAddr = getPoolAndAddr(stream, opts.getPartition(), opts.getReadIsrReplica());
            } catch (NoSuchPartitionException e) {
                ex = e;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }
                metadataCache.update();
                continue;
            }

            ConnectionPool pool = poolAndAddr.pool;
            String addr = poolAndAddr.addr;
            // TODO: Exception handling
            ManagedAPIClient conn = pool.get(connFactory(addr));

            // TODO: Handle auto resubscribe on UNAVAILABLE errors.
            return conn.subscribe(stream, opts, new StreamObserver<Api.Message>() {
                @Override
                public void onNext(Api.Message msg) {
                    msgHandler.onMessage(Message.fromProto(msg));
                }

                @Override
                public void onError(Throwable t) {
                    pool.put(conn);
                    if (t instanceof StatusRuntimeException) {
                        StatusRuntimeException e = (StatusRuntimeException) t;
                        switch (e.getStatus().getCode()) {
                            case CANCELLED:
                                // Subscription cancellation shouldn't cause an error callback.
                                return;
                            case NOT_FOUND:
                                // Indicates the stream was deleted.
                                t = new StreamDeletedException("stream was deleted", e);
                                break;
                            case FAILED_PRECONDITION:
                                // Indicates the partition was paused.
                                t = new PartitionPausedException("partition was paused", e);
                                break;
                        }
                    }
                    msgHandler.onError(t);
                }

                @Override
                public void onCompleted() {
                    pool.put(conn);
                }
            });
        }
        throw ex;
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
        Api.PublishResponse resp;
        try {
            resp = doResilientRPC(client -> client.publish(stream, payload, opts));
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
        Api.PublishToSubjectResponse resp;
        try {
            resp = doResilientRPC(client -> client.publishToSubject(subject, payload, opts));
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
     * Executes the given RPC and performs retries if it fails due to the broker being unavailable, cycling through the
     * known broker list.
     *
     * @param rpc {@link RPC} to execute
     * @param <T> response type
     * @return RPC result
     * @throws StatusRuntimeException gRPC exception
     */
    private <T> T doResilientRPC(RPC<T> rpc) throws StatusRuntimeException {
        ManagedAPIClient client = getAPIClient();
        for (int i = 0; i < 10; i++) {
            try {
                return rpc.execute(client);
            } catch (StatusRuntimeException e) {
                if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
                    // TODO: add error handling here
                    client = dialBroker();
                    getAPIClient().close();
                    setAPIClient(client);
                    continue;
                }
                throw e;
            }
        }
        return null;
    }

    private ConnectionPool.ConnectionFactory connFactory(String addr) {
        return () -> dialBroker(addr);
    }

    /**
     * Returns the {@link ConnectionPool} and broker address for the given partition.
     *
     * @param stream         stream to get pool and address for
     * @param partition      partition to get pool and address for
     * @param readIsrReplica whether or not we can read from a random replica or only the partition leader
     * @return {@link PoolAndAddr} containing connection pool and broker address
     * @throws NoSuchPartitionException if there is no metadata for the given partition
     */
    private PoolAndAddr getPoolAndAddr(String stream, int partition, boolean readIsrReplica) throws NoSuchPartitionException {
        String addr = metadataCache.getAddr(stream, partition, readIsrReplica);
        if (addr == null) {
            throw new NoSuchPartitionException("no metadata for partition");
        }
        synchronized (this) {
            ConnectionPool pool = pools.get(addr);
            if (pool == null) {
                pool = new ConnectionPool(opts.getMaxConnsPerBroker(), opts.getKeepAliveTimeMillis());
                pools.put(addr, pool);
            }
            return new PoolAndAddr(addr, pool);
        }
    }

    private synchronized ManagedAPIClient getAPIClient() {
        return this.conn;
    }

    private synchronized void setAPIClient(ManagedAPIClient client) {
        this.conn = client;
    }

    /**
     * Dials each broker in the cluster, in random order, returning an APIClient for the first one that is successful.
     *
     * @return ManagedAPIClient
     */
    private ManagedAPIClient dialBroker() {
        return dialBroker(metadataCache.getAddrs());
    }

    /**
     * Dials each broker in the set of addresses, in random order, returning an APIClient for the first one that is
     * successful.
     *
     * @param addrs set of addresses to dial
     * @return ManagedAPIClient
     */
    private static ManagedAPIClient dialBroker(Set<String> addrs) {
        List<String> addrsList = new ArrayList<>(addrs);
        Collections.shuffle(addrsList);
        for (String addr : addrsList) {
            return dialBroker(addr);
        }
        return null;
    }

    private static ManagedAPIClient dialBroker(String addr) {
        // TODO: Implement TLS.
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forTarget(addr);
        ManagedChannel channel = channelBuilder.usePlaintext().build();
        return new ManagedAPIClient(channel);
    }

    private static class PoolAndAddr {
        private final String addr;
        private final ConnectionPool pool;

        PoolAndAddr(String addr, ConnectionPool pool) {
            this.addr = addr;
            this.pool = pool;
        }
    }

    /**
     * {@code Builder} is used to configure and construct a {@link APIClient} instance.
     */
    public static class Builder {

        private final Set<String> addrs = new HashSet<>();

        private Builder() {
        }

        /**
         * Creates a {@link Client.Builder} that will connect to the given broker
         * address.
         *
         * @param addr broker address
         * @return {@code Builder}
         */
        public static Client.Builder create(String addr) {
            Client.Builder builder = new Client.Builder();
            return builder.withBroker(addr);
        }

        /**
         * Adds the given address to the set of broker hosts the client will use
         * when attempting to connect.
         *
         * @param addr broker address
         * @return {@code this} to allow for call chaining
         */
        Client.Builder withBroker(String addr) {
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

            ClientOptions opts = new ClientOptions();
            opts.setBrokers(addrs);

            ManagedAPIClient apiClient = Client.dialBroker(addrs);
            Client client = new Client(apiClient, opts);
            MetadataCache metadataCache = new MetadataCache(addrs, client::doResilientRPC);
            metadataCache.update();
            client.setMetadataCache(metadataCache);

            return client;
        }

    }

}