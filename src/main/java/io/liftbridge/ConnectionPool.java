package io.liftbridge;

import java.util.*;

/**
 * Maintains a pool of Liftbridge connections. It limits the number of connections based on maxConns and closes unused
 * connections based on keepAliveTime.
 */
class ConnectionPool {

    private final List<ManagedAPIClient> conns = new ArrayList<>();
    private final int maxConns;
    private final Map<ManagedAPIClient, Timer> timers = new HashMap<>();
    private final long keepAliveTimeMillis;

    ConnectionPool(int maxConns, long keepAliveTimeMillis) {
        this.maxConns = maxConns;
        this.keepAliveTimeMillis = keepAliveTimeMillis;
    }

    /**
     * Returns a Liftbridge connection from the pool, if any, or by using the provided factory.
     *
     * @param factory {@link ConnectionFactory} to create a new connection if the pool is empty
     * @return {@link ManagedAPIClient}
     */
    synchronized ManagedAPIClient get(ConnectionFactory factory) {
        ManagedAPIClient conn;
        if (conns.size() > 0) {
            conn = conns.remove(0);
            Timer timer = timers.get(conn);
            // Cancel the timer if there is one for this connection.
            if (timer != null) {
                timer.cancel();
                timers.remove(conn);
            }
        } else {
            conn = factory.newConnection();
        }
        return conn;
    }

    /**
     * Returns the given Liftbridge connection to the pool if there is capacity or closes it if there is not.
     *
     * @param conn {@link ManagedAPIClient} to return
     */
    synchronized void put(ManagedAPIClient conn) {
        if (maxConns == 0 || conns.size() < maxConns) {
            conns.add(conn);
            if (keepAliveTimeMillis > 0) {
                // Start timer to close conn if it's unused for keepAliveTime.
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        connExpired(conn);
                    }
                }, keepAliveTimeMillis);
                timers.put(conn, timer);
            }
        } else {
            conn.close();
        }
    }

    /**
     * Cleans up the connection pool by closing all active connections and stopping all timers.
     */
    synchronized void close() {
        for (ManagedAPIClient conn : conns) {
            conn.close();
        }
        conns.clear();
        for (Timer timer : timers.values()) {
            timer.cancel();
        }
        timers.clear();
    }

    /**
     * Called when the keepAliveTime timer has fired for the given connection. This will close and remove the connection from the pool.
     *
     * @param conn the expired connection to close and remove
     */
    private synchronized void connExpired(ManagedAPIClient conn) {
        conns.remove(conn);
        conn.close();
        timers.remove(conn);
    }

    interface ConnectionFactory {
        ManagedAPIClient newConnection();
    }

}
