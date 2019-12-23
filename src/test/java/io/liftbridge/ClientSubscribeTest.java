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

// TODO there's not much we can test without publishing
public class ClientSubscribeTest extends BaseClientTest {
    @Before
    public void setup() {
        this.populatedStreamName = randomAlphabetic(10);
        client.createStream(streamName, new StreamOptions());
        client.createStream(this.populatedStreamName, new StreamOptions());

        for(Integer i = 0; i < 10; ++i) {
            ByteBuffer payload = ByteBuffer.allocate(4).putInt(i);
            client.publish(this.populatedStreamName,
                           payload.asReadOnlyBuffer(),
                           10, MILLISECONDS);
        }
    }

    String populatedStreamName;
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
    public void testSubscribeFromFirstMessage() {
        SubscriptionOptions opts = new SubscriptionOptions()
            .setStartPosition(new SubscriptionOptions.StartAtEarliestReceived());
        final List<Integer> streamValues = new ArrayList<Integer>();

        client.subscribe(populatedStreamName, new MessageHandler(){
                public void onMessage(io.liftbridge.Message msg){
                    streamValues.add(msg.getValue().getInt());
                }
                public void onError(Throwable t){}
            }, opts);
        await().atMost(5, SECONDS).until(() -> streamValues.size() >= 10);
        System.out.println(streamValues.get(0));
        streamValues.remove(0);
        Collections.sort(streamValues);
        Integer[] vals = new Integer[10];
        assertArrayEquals("All messages were received",
                          new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                          streamValues.toArray(vals));
    }
}
