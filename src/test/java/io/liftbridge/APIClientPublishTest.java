package io.liftbridge;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.liftbridge.exceptions.NoSuchPartitionException;
import io.liftbridge.proto.Api;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.*;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class APIClientPublishTest extends BaseAPIClientTest {

    private static final String CORRELATION_ID = "abc123";

    @Before
    public void setupStreams() {
        StreamOptions opts = new StreamOptions().setSubject("foo.*");
        client.createStream(streamName, opts);
    }

    @After
    public void teardownStreams() {
        client.deleteStream(streamName);
    }

    @Test(expected = StatusRuntimeException.class)
    public void testPublishDeadlineExceeded() {
        MessageOptions msgOpts = new MessageOptions().setAckDeadline(1, NANOSECONDS);
        client.publish(streamName, null, msgOpts);
    }

    @Test
    public void testPublishToSubject() throws NoSuchPartitionException {
        MessageOptions msgOpts = new MessageOptions()
                .setAckDeadline(100, MILLISECONDS)
                .setCorrelationId(CORRELATION_ID);
        for (int i = 0; i < 5; i++) {
            byte[] payload = ByteBuffer.allocate(4).putInt(i).array();
            Api.PublishToSubjectResponse resp = client.publishToSubject(String.format("foo.%d", i), payload, msgOpts);
            Api.Ack ack = resp.getAck();
            assertEquals("Received expected ack", CORRELATION_ID, ack.getCorrelationId());
        }

        SubscriptionOptions subOpts = new SubscriptionOptions().startAtEarliestReceived();
        final List<Integer> streamValues = new ArrayList<>();

        Subscription sub = client.subscribe(streamName, subOpts, new StreamObserver<Api.Message>() {
            @Override
            public void onNext(Api.Message value) {
                streamValues.add(ByteBuffer.wrap(Message.fromProto(value).getValue()).getInt());
            }

            @Override
            public void onError(Throwable t) {
                fail(t.getMessage());
            }

            @Override
            public void onCompleted() {

            }
        });

        await().atMost(5, SECONDS).until(() -> streamValues.size() >= 5);
        Collections.sort(streamValues);
        Integer[] vals = new Integer[5];
        assertArrayEquals("All messages were received",
                new Integer[]{0, 1, 2, 3, 4},
                streamValues.toArray(vals));

        sub.unsubscribe();
    }

    @Test
    public void testPublishNoAck() {
        // No ack when deadline is not set.
        Api.PublishResponse resp = client.publish(streamName, null, new MessageOptions());
        assertFalse("Ack is null", resp.hasAck());

        // No ack when AckPolicy is NONE.
        MessageOptions opts = new MessageOptions()
                .setAckDeadline(100, MILLISECONDS)
                .setAckPolicy(MessageOptions.AckPolicy.NONE);
        resp = client.publish(streamName, null, opts);
        assertFalse("Ack is null", resp.hasAck());
    }

}
