package io.liftbridge;

import io.liftbridge.exceptions.DeadlineExceededException;
import io.liftbridge.exceptions.NoSuchPartitionException;
import io.liftbridge.exceptions.NoSuchStreamException;
import io.liftbridge.exceptions.StreamExistsException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.concurrent.TimeUnit.*;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

public class ClientPublishTest extends BaseClientTest {

    private static final String CORRELATION_ID = "abc123";

    @Before
    public void setupStreams() throws StreamExistsException {
        StreamOptions opts = new StreamOptions().setSubject("foo.*");
        client.createStream(streamName, opts);
    }

    @After
    public void teardownStreams() throws NoSuchStreamException {
        client.deleteStream(streamName);
    }

    @Test(expected = DeadlineExceededException.class)
    public void testPublishDeadlineExceeded() throws DeadlineExceededException {
        MessageOptions msgOpts = new MessageOptions().setAckDeadline(1, NANOSECONDS);
        client.publish(streamName, null, msgOpts);
    }

    @Test
    public void testPublishToSubject() throws NoSuchPartitionException, DeadlineExceededException {
        MessageOptions msgOpts = new MessageOptions()
                .setAckDeadline(100, MILLISECONDS)
                .setCorrelationId(CORRELATION_ID);
        for (int i = 0; i < 5; i++) {
            byte[] payload = ByteBuffer.allocate(4).putInt(i).array();
            Ack ack = client.publishToSubject(String.format("foo.%d", i), payload, msgOpts);
            assertEquals("Received expected ack", CORRELATION_ID, ack.getCorrelationId());
        }

        SubscriptionOptions subOpts = new SubscriptionOptions().startAtEarliestReceived();
        final List<Integer> streamValues = new ArrayList<>();

        Subscription sub = client.subscribe(streamName, subOpts, new MessageHandler() {
            @Override
            public void onMessage(Message msg) {
                streamValues.add(ByteBuffer.wrap(msg.getValue()).getInt());
            }

            @Override
            public void onError(Throwable t) {
                fail(t.getMessage());
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
    public void testPublishNoAck() throws DeadlineExceededException {
        // No ack when deadline is not set.
        Ack ack = client.publish(streamName, null, new MessageOptions());
        assertNull(ack);

        // No ack when AckPolicy is NONE.
        MessageOptions opts = new MessageOptions()
                .setAckDeadline(100, MILLISECONDS)
                .setAckPolicy(MessageOptions.AckPolicy.NONE);
        ack = client.publish(streamName, null, opts);
        assertNull(ack);
    }

}
