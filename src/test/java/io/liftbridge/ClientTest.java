package io.liftbridge;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import io.grpc.ManagedChannelBuilder;
import io.grpc.ManagedChannel;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class ClientTest {
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

    private static ManagedChannel grpcChannel;
    private String streamName;
    Client client;

    // TODO write assertions when a "fetch metadata" method is available
    @Test
    public void testCreateStreamDefaultOptions() {
        client.createStream(streamName, new StreamOptions());
    }

    @Test
    public void testCreateStreamWithMorePartitions() {
        client.createStream(streamName, new StreamOptions().setPartitions(3));
    }

    @Test
    public void testCreateStreamWithGroup(){
        client.createStream(streamName, new StreamOptions().setGroup("grp1"));
    }

    @Test
    public void testCreateStreamWithSubject(){
        client.createStream(streamName, new StreamOptions().setSubject("subj-rand"));
    }

    @Test
    public void testCreateStreamWithReplicationFactor(){
        client.createStream(streamName, new StreamOptions().setReplicationFactor(1));
    }

    @Test
    public void testCreateStreamWithAllOptions(){
        StreamOptions options = new StreamOptions()
            .setPartitions(3)
            .setGroup("grp1")
            .setSubject("subj-rand")
            .setReplicationFactor(1);
        client.createStream(streamName, options);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateStreamDuplicatedFails(){
        client.createStream(streamName, new StreamOptions());
        client.createStream(streamName, new StreamOptions());
    }

    @Test
    public void testSubscribeDefaultOptions() {
        client.createStream(streamName, new StreamOptions());
        client.subscribe(streamName, new MessageHandler(){
                public void onMessage(io.liftbridge.Message msg){}
                public void onError(Throwable t){}
            }, new SubscriptionOptions());
    }
}
