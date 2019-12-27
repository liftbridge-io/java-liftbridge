package io.liftbridge;

import org.junit.Test;

public class ClientCreateStreamTest extends BaseClientTest {
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
}
