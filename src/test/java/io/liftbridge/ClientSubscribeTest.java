package io.liftbridge;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.*;
import java.util.concurrent.atomic.AtomicReference;
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
    public void testSubscribeFromNewOnly() {
        SubscriptionOptions opts = new SubscriptionOptions();
        final List<Integer> streamValues = new ArrayList<Integer>();

        client.subscribe(populatedStreamName, new MessageHandler(){
                public void onMessage(io.liftbridge.Message msg){
                    try{
                        streamValues.add(ByteBuffer.wrap(msg.getValue()).getInt());
                    } catch(java.nio.BufferUnderflowException ex) {

                    }
                }
                public void onError(Throwable t){
                    System.out.println(t.getMessage());
                    t.printStackTrace();
                }
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
}
