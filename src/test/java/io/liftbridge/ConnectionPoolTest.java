package io.liftbridge;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ConnectionPoolTest {

    @Test
    public void testGetNewConn() {
        ConnectionPool.ConnectionFactory mockFactory = mock(ConnectionPool.ConnectionFactory.class);
        ManagedAPIClient expected = mock(ManagedAPIClient.class);
        when(mockFactory.newConnection()).thenReturn(expected);
        ConnectionPool pool = new ConnectionPool(2, 30 * 1000);

        ManagedAPIClient actual = pool.get(mockFactory);

        assertEquals(expected, actual);
        verify(mockFactory).newConnection();
    }

    @Test
    public void testPutGet() {
        ConnectionPool.ConnectionFactory mockFactory = mock(ConnectionPool.ConnectionFactory.class);
        ManagedAPIClient expected = mock(ManagedAPIClient.class);
        when(mockFactory.newConnection()).thenReturn(expected);
        ConnectionPool pool = new ConnectionPool(2, 30 * 1000);
        ManagedAPIClient conn = pool.get(mockFactory);

        pool.put(conn);
        ManagedAPIClient actual = pool.get(mockFactory);

        assertEquals(expected, actual);
        verify(mockFactory, times(1)).newConnection();

        pool.get(mockFactory);

        verify(mockFactory, times(2)).newConnection();
    }

    @Test
    public void testPutMaxConns() {
        ConnectionPool.ConnectionFactory mockFactory = mock(ConnectionPool.ConnectionFactory.class);
        ManagedAPIClient expected = mock(ManagedAPIClient.class);
        when(mockFactory.newConnection()).thenReturn(expected);
        ConnectionPool pool = new ConnectionPool(1, 30 * 1000);
        ManagedAPIClient conn = pool.get(mockFactory);
        pool.put(conn);

        pool.put(conn);

        verify(conn, times(1)).close();
    }

    @Test
    public void testPutConnExpired() throws InterruptedException {
        ConnectionPool.ConnectionFactory mockFactory = mock(ConnectionPool.ConnectionFactory.class);
        ManagedAPIClient expected = mock(ManagedAPIClient.class);
        when(mockFactory.newConnection()).thenReturn(expected);
        ConnectionPool pool = new ConnectionPool(1, 1);
        ManagedAPIClient conn = pool.get(mockFactory);

        pool.put(conn);

        Thread.sleep(5);
        verify(conn, times(1)).close();
    }

    @Test
    public void testClose() {
        ConnectionPool.ConnectionFactory mockFactory = mock(ConnectionPool.ConnectionFactory.class);
        ManagedAPIClient expected = mock(ManagedAPIClient.class);
        when(mockFactory.newConnection()).thenReturn(expected);
        ConnectionPool pool = new ConnectionPool(2, 30 * 1000);
        ManagedAPIClient conn = pool.get(mockFactory);
        pool.put(conn);
        pool.put(conn);

        pool.close();

        verify(conn, times(2)).close();
    }

}
