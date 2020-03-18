package io.liftbridge;

import io.liftbridge.exceptions.LiftbridgeException;
import io.liftbridge.exceptions.NoSuchStreamException;
import io.liftbridge.exceptions.StreamExistsException;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClientCreateDeleteStreamTest extends BaseClientTest {
    // TODO write assertions when a "fetch metadata" method is available
    @Test
    public void testCreateStreamDefaultOptions() throws LiftbridgeException {
        client.createStream(streamName);
        client.deleteStream(streamName);
    }

    @Test
    public void testCreateStreamWithMorePartitions() throws LiftbridgeException {
        client.createStream(streamName, new StreamOptions().setPartitions(3));
        client.deleteStream(streamName);
    }

    @Test
    public void testCreateStreamWithGroup() throws LiftbridgeException {
        client.createStream(streamName, new StreamOptions().setGroup("grp1"));
        client.deleteStream(streamName);
    }

    @Test
    public void testCreateStreamWithSubject() throws LiftbridgeException {
        client.createStream(streamName, new StreamOptions().setSubject("subj-rand"));
        client.deleteStream(streamName);
    }

    @Test
    public void testCreateStreamWithReplicationFactor() throws LiftbridgeException {
        client.createStream(streamName, new StreamOptions().setReplicationFactor(1));
        client.deleteStream(streamName);
    }

    @Test
    public void testCreateStreamWithAllOptions() throws LiftbridgeException {
        StreamOptions options = new StreamOptions()
                .setPartitions(3)
                .setGroup("grp1")
                .setSubject("subj-rand")
                .setReplicationFactor(1);
        client.createStream(streamName, options);
        client.deleteStream(streamName);
    }

    @Test
    public void testCreateStreamDuplicatedFails() throws LiftbridgeException {
        client.createStream(streamName, new StreamOptions());
        try {
            client.createStream(streamName, new StreamOptions());
            fail("Expected StreamExistsException");
        } catch (StreamExistsException e) {
            assertTrue("Threw expected exception", e.getMessage().contains("already exists"));
        } finally {
            client.deleteStream(streamName);
        }
    }

    @Test(expected = NoSuchStreamException.class)
    public void testDeleteStreamNoSuchStream() throws LiftbridgeException {
        client.deleteStream("foo");
    }
}
