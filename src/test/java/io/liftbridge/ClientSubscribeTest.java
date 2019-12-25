package io.liftbridge;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicLong;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class ClientSubscribeTest extends BaseClientTest {
    @Before
    public void setupStreams() {
        populatedStreamName = randomAlphabetic(10);
        client.createStream(streamName, new StreamOptions());
        client.createStream(populatedStreamName, new StreamOptions());

        for(Integer i = 0; i < 10; ++i) {
            byte[] payload = ByteBuffer.allocate(4).putInt(i).array();
            client.publish(this.populatedStreamName,
                           payload,
                           10, MILLISECONDS);
        }
    }

    private String populatedStreamName;

    @Test
    public void testSubscribeDefaultOptions() {
        client.subscribe(streamName, new MessageHandler(){
                public void onMessage(io.liftbridge.Message msg){}
                public void onError(Throwable t){}
            }, new SubscriptionOptions());
    }

    @Test
    public void testSubscribeNonExistentStream()  {
        final AtomicReference<Throwable> streamErr = new AtomicReference(null);
        client.subscribe(randomAlphabetic(15), new MessageHandler(){
                public void onMessage(io.liftbridge.Message msg){}
                public void onError(Throwable t){
                    streamErr.set(t);
                }
            }, new SubscriptionOptions());
        await().atMost(1, SECONDS).until(() -> streamErr.get() != null);
        assertTrue(streamErr.get() instanceof RuntimeException);
        assertTrue(streamErr.get().getMessage().contains("NOT_FOUND"));
    }

    @Test
    public void testSubscribeFromBeginning() {
        SubscriptionOptions opts = new SubscriptionOptions()
            .setStartPosition(new SubscriptionOptions.StartAtEarliestReceived());
        final List<Integer> streamValues = new ArrayList<Integer>();

        client.subscribe(populatedStreamName, new MessageHandler(){
                public void onMessage(io.liftbridge.Message msg){
                    if(msg.getValue().length > 0){
                        streamValues.add(ByteBuffer.wrap(msg.getValue()).getInt());
                    }
                }
                public void onError(Throwable t){}
            }, opts);


        await().atMost(5, SECONDS).until(() -> streamValues.size() >= 10);
        Collections.sort(streamValues);
        Integer[] vals = new Integer[10];
        assertArrayEquals("All messages were received",
                          new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                          streamValues.toArray(vals));
    }

    @Test
    public void testSubscribeFromLatestReceived() {
        SubscriptionOptions opts = new SubscriptionOptions()
            .setStartPosition(new SubscriptionOptions.StartAtLatestReceived());
        final AtomicLong lastOffset = new AtomicLong(-1);

        client.subscribe(populatedStreamName, new MessageHandler(){
                public void onMessage(io.liftbridge.Message msg){
                    if(msg.getValue().length > 0) {
                        lastOffset.set(msg.getOffset());
                    }
                }
                public void onError(Throwable t){}
            }, opts);


        await().atMost(5, SECONDS).until(() -> lastOffset.get() >= 0);
        assertEquals("Last offset message was received", 9, lastOffset.get());
    }

    @Test
    public void testSubscribeFromNewOnly() {
        SubscriptionOptions opts = new SubscriptionOptions();
        final List<Integer> streamValues = new ArrayList<Integer>();

        client.subscribe(populatedStreamName, new MessageHandler(){
                public void onMessage(io.liftbridge.Message msg){
                    if(msg.getValue().length > 0) {
                        streamValues.add(ByteBuffer.wrap(msg.getValue()).getInt());
                    }
                }
                public void onError(Throwable t){}
            }, opts);

        for(Integer i = 0; i < 10; ++i) {
            byte[] payload = ByteBuffer.allocate(20).putInt(i + 10).array();
            client.publish(this.populatedStreamName,
                           payload, 10, MILLISECONDS);
        }
        await().atMost(5, SECONDS).until(() -> streamValues.size() >= 10);
        Collections.sort(streamValues);
        Integer[] vals = new Integer[10];
        assertArrayEquals("All messages were received",
                          new Integer[]{10, 11, 12, 13, 14, 15, 16, 17, 18, 19},
                          streamValues.toArray(vals));
    }

    @Test
    public void testSubscribeFromOffset() {
        SubscriptionOptions opts = new SubscriptionOptions()
            .setStartPosition(new SubscriptionOptions.StartAtOffset(5L));
        final List<Long> offsets = new ArrayList<Long>();

        client.subscribe(populatedStreamName, new MessageHandler(){
                public void onMessage(io.liftbridge.Message msg){
                    if(msg.getValue().length > 0) {
                        offsets.add(msg.getOffset());
                    }
                }
                public void onError(Throwable t){}
            }, opts);

        await().atMost(5, SECONDS).until(() -> offsets.size() >= 5);
        Collections.sort(offsets);
        Long[] vals = new Long[5];
        assertArrayEquals("All offset, starting with 5, were received",
                          new Long[]{5L, 6L, 7L, 8L, 9L},
                          offsets.toArray(vals));
    }
}
