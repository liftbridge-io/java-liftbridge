package io.liftbridge;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * {@link APIClient} that manages the gRPC channel for the user. This generally should not be used directly. Instead see
 * {@link Client} for the high-level user-facing API.
 */
class ManagedAPIClient extends APIClient {

    private ManagedChannel channel;

    protected ManagedAPIClient(ManagedChannel channel) {
        super(channel);
        this.channel = channel;
    }

    /**
     * Closes the client connection by shutting down the gRPC channel and releasing any resources.
     */
    void close() {
        channel.shutdown();
    }

    /**
     * {@code Builder} is used to configure and construct a {@link Client} instance.
     */
    public static class Builder {

        private final String addr;

        private Builder(String addr) {
            this.addr = addr;
        }

        /**
         * Creates a {@link Builder} that will connect to the given broker
         * address.
         *
         * @param addr broker address
         * @return {@code Builder}
         */
        public static Builder create(String addr) {
            return new Builder(addr);
        }

        /**
         * Creates a configured {@link ManagedAPIClient} instance.
         *
         * @return {@code ManagedAPIClient}
         */
        public ManagedAPIClient build() {
            ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forTarget(addr);

            // TODO: Implement TLS.
            ManagedChannel channel = channelBuilder.usePlaintext().build();
            return new ManagedAPIClient(channel);
        }
    }

}
