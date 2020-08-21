package io.liftbridge;

import io.grpc.stub.StreamObserver;
import io.liftbridge.exceptions.NoSuchPartitionException;
import io.liftbridge.proto.Api;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.atomic.AtomicLong;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class APIClientSubscribeTest extends BaseAPIClientTest {

    private String populatedStreamName;

    @Before
    public void setupStreams() {
        populatedStreamName = randomAlphabetic(10);
        StreamOptions opts = new StreamOptions();
        client.createStream(streamName, opts);
        client.createStream(populatedStreamName, opts);

        MessageOptions msgOpts = new MessageOptions().setAckDeadline(1, SECONDS);

        for (int i = 0; i < 10; ++i) {
            byte[] payload = ByteBuffer.allocate(4).putInt(i).array();
            client.publish(this.populatedStreamName, payload, msgOpts);
        }
    }

    @After
    public void teardownStreams() {
        client.deleteStream(streamName);
        client.deleteStream(populatedStreamName);
    }

    @Test
    public void testSubscribeDefaultOptions() throws NoSuchPartitionException {
        Subscription sub = client.subscribe(streamName, new SubscriptionOptions(), new StreamObserver<Api.Message>() {
            @Override
            public void onNext(Api.Message value) {
            }

            @Override
            public void onError(Throwable t) {
                fail(t.getMessage());
            }

            @Override
            public void onCompleted() {
            }
        });
        sub.unsubscribe();
    }

    @Test(expected = NoSuchPartitionException.class)
    public void testSubscribeNonExistentStream() throws NoSuchPartitionException {
        client.subscribe(randomAlphabetic(15), new SubscriptionOptions(), new StreamObserver<Api.Message>() {
            @Override
            public void onNext(Api.Message value) {
                fail("Received unexpected message");
            }

            @Override
            public void onError(Throwable t) {
                fail(t.getMessage());
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    @Test
    public void testSubscribeFromBeginning() throws NoSuchPartitionException {
        SubscriptionOptions opts = new SubscriptionOptions().startAtEarliestReceived();
        final List<Integer> streamValues = new ArrayList<>();

        Subscription sub = client.subscribe(populatedStreamName, opts, new StreamObserver<Api.Message>() {
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

        await().atMost(5, SECONDS).until(() -> streamValues.size() >= 10);
        Collections.sort(streamValues);
        Integer[] vals = new Integer[10];
        assertArrayEquals("All messages were received",
                new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                streamValues.toArray(vals));

        sub.unsubscribe();
    }

    @Test
    public void testSubscribeFromLatestReceived() throws NoSuchPartitionException {
        SubscriptionOptions opts = new SubscriptionOptions().startAtLatestReceived();
        final AtomicLong lastOffset = new AtomicLong(-1);

        Subscription sub = client.subscribe(populatedStreamName, opts, new StreamObserver<Api.Message>() {
            @Override
            public void onNext(Api.Message value) {
                lastOffset.set(Message.fromProto(value).getOffset());
            }

            @Override
            public void onError(Throwable t) {
                fail(t.getMessage());
            }

            @Override
            public void onCompleted() {

            }
        });

        await().atMost(5, SECONDS).until(() -> lastOffset.get() >= 0);
        assertEquals("Last offset message was received", 9, lastOffset.get());

        sub.unsubscribe();
    }

    @Test
    public void testSubscribeFromNewOnly() throws NoSuchPartitionException {
        SubscriptionOptions opts = new SubscriptionOptions();
        final List<Integer> streamValues = new ArrayList<>();

        Subscription sub = client.subscribe(populatedStreamName, opts, new StreamObserver<Api.Message>() {
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

        MessageOptions msgOpts = new MessageOptions().setAckDeadline(10, MILLISECONDS);
        for (int i = 0; i < 10; ++i) {
            byte[] payload = ByteBuffer.allocate(20).putInt(i + 10).array();
            client.publish(this.populatedStreamName, payload, msgOpts);
        }
        await().atMost(5, SECONDS).until(() -> streamValues.size() >= 10);
        Collections.sort(streamValues);
        Integer[] vals = new Integer[10];
        assertArrayEquals("All messages were received",
                new Integer[]{10, 11, 12, 13, 14, 15, 16, 17, 18, 19},
                streamValues.toArray(vals));

        sub.unsubscribe();
    }

    @Test
    public void testSubscribeFromOffset() throws NoSuchPartitionException {
        SubscriptionOptions opts = new SubscriptionOptions().startAtOffset(5);
        final List<Long> offsets = new ArrayList<>();

        Subscription sub = client.subscribe(populatedStreamName, opts, new StreamObserver<Api.Message>() {
            @Override
            public void onNext(Api.Message value) {
                offsets.add(Message.fromProto(value).getOffset());
            }

            @Override
            public void onError(Throwable t) {
                fail(t.getMessage());
            }

            @Override
            public void onCompleted() {

            }
        });

        await().atMost(5, SECONDS).until(() -> offsets.size() >= 5);
        Collections.sort(offsets);
        Long[] vals = new Long[5];
        assertArrayEquals("All offset, starting with 5, were received",
                new Long[]{5L, 6L, 7L, 8L, 9L},
                offsets.toArray(vals));

        sub.unsubscribe();
    }

}
