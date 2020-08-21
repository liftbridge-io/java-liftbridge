package io.liftbridge;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.Test;

import static org.junit.Assert.*;

public class APIClientCreateDeleteStreamTest extends BaseAPIClientTest {
    // TODO write assertions when a "fetch metadata" method is available
    @Test
    public void testCreateStreamDefaultOptions() {
        client.createStream(streamName, new StreamOptions());
        client.deleteStream(streamName);
    }

    @Test
    public void testCreateStreamWithMorePartitions() {
        client.createStream(streamName, new StreamOptions().setPartitions(3));
        client.deleteStream(streamName);
    }

    @Test
    public void testCreateStreamWithGroup() {
        client.createStream(streamName, new StreamOptions().setGroup("grp1"));
        client.deleteStream(streamName);
    }

    @Test
    public void testCreateStreamWithSubject() {
        client.createStream(streamName, new StreamOptions().setSubject("subj-rand"));
        client.deleteStream(streamName);
    }

    @Test
    public void testCreateStreamWithReplicationFactor() {
        client.createStream(streamName, new StreamOptions().setReplicationFactor(1));
        client.deleteStream(streamName);
    }

    @Test
    public void testCreateStreamWithAllOptions() {
        StreamOptions options = new StreamOptions()
                .setPartitions(3)
                .setGroup("grp1")
                .setSubject("subj-rand")
                .setReplicationFactor(1);
        client.createStream(streamName, options);
        client.deleteStream(streamName);
    }

    @Test
    public void testCreateStreamDuplicatedFails() {
        client.createStream(streamName, new StreamOptions());
        try {
            client.createStream(streamName, new StreamOptions());
            fail("Expected StreamExistsException");
        } catch (StatusRuntimeException e) {
            assertEquals(Status.Code.ALREADY_EXISTS, e.getStatus().getCode());
        } finally {
            client.deleteStream(streamName);
        }
    }

    @Test(expected = StatusRuntimeException.class)
    public void testDeleteStreamNoSuchStream() {
        client.deleteStream("foo");
    }
}
