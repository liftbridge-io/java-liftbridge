package io.liftbridge;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.List;
import java.util.ArrayList;
import java.io.Closeable;

/**
 * API Client that manages the gRPC channel for the user
 */
public class ManagedChannelClient extends Client implements Closeable {
    private ManagedChannel channel;

    private ManagedChannelClient(ManagedChannel channel) {
        super(channel);
        this.channel = channel;
    }

    /**
     * Closes the client connection by shutting down the gRPC channel and
     * releasing any resources.
     */
    public void close() {
        channel.shutdown();
    }

    /**
     * {@code Builder} is used to configure and construct a {@link Client}
     * instance.
     */
    public static class Builder {
        private final List<String> addrs = new ArrayList<>();

        private Builder() {}

        /**
         * Creates a {@link Builder} that will connect to the given broker
         * address.
         *
         * @param addr broker address
         * @return {@code Builder}
         */
        public static Builder create(String addr) {
            Builder builder = new Builder();
            return builder.withBroker(addr);
        }

        /**
         * Adds the given address to the set of broker hosts the client will use
         * when attempting to connect.
         *
         * @param addr broker address
         * @return {@code this} to allow for call chaining
         */
        Builder withBroker(String addr) {
            addrs.add(addr);
            return this;
        }

        /**
         * Creates a configured {@link ManagedChannelClient} instance.
         *
         * @return {@code ManagedChannelClient}
         */
        public ManagedChannelClient build() {
            if (addrs.size() == 0) {
                throw new RuntimeException("no addresses provided");
            }

            // TODO: Handle multiple addresses.
            ManagedChannelBuilder channelBuilder =
                ManagedChannelBuilder.forTarget(addrs.get(0));

            // TODO: Implement TLS.
            ManagedChannel channel = channelBuilder.usePlaintext().build();
            return new ManagedChannelClient(channel);
        }
    }
}
