package io.liftbridge;

import org.junit.Test;
import static org.junit.Assert.*;
import io.grpc.ManagedChannelBuilder;
import io.grpc.ManagedChannel;

public class ClientTest {
    private static ManagedChannel grpcConnect() {
        ManagedChannelBuilder builder = ManagedChannelBuilder.forAddress("localhost", 9292);
        return builder.usePlaintext().build();
    }

    @Test public void testCreateStream() {
        ManagedChannel grpc_chan = ClientTest.grpcConnect();
        io.liftbridge.Client client = io.liftbridge.Client.connect(grpc_chan);
        assertTrue("asserting", true);
    }
}
