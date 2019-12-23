package io.liftbridge;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.After;
import org.junit.Ignore;
import io.grpc.ManagedChannelBuilder;
import io.grpc.ManagedChannel;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Ignore
public class BaseClientTest{
    private static final String SERVER_ADDRESS = "localhost";
    private static final Integer SERVER_PORT = 9292;

    private static ManagedChannel grpcConnect() {
        ManagedChannelBuilder builder = ManagedChannelBuilder.forAddress(SERVER_ADDRESS, SERVER_PORT);
        return builder.usePlaintext().build();
    }

    @BeforeClass
    public static void setupGrpcChannel() {
        grpcChannel = grpcConnect();
    }

    @AfterClass
    public static void cleanupGrpcChannel() {
        grpcChannel.shutdown();
    }

    @Before
    public void setupStreamName() {
        this.streamName = randomAlphabetic(10);
    }

    @Before
    public void setupClient() {
        this.client = Client.connect(grpcChannel);
    }

    @After
    public void tearDownClient() {
        this.client = null;
    }

    static ManagedChannel grpcChannel;
    String streamName;
    Client client;
}
