package io.liftbridge;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.*;
import java.util.concurrent.atomic.AtomicReference;


import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

// TODO there's not much we can test without publishing
public class ClientSubscribeTest extends BaseClientTest {
    @Before
    public void setup() {
        // this.populatedStreamName = randomAlphabetic(10);
        client.createStream(streamName, new StreamOptions());
    }

    // String populatedStreamName;
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
}
