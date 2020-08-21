package io.liftbridge;

public class BrokerInfo {

    private final String id;
    private final String host;
    private final int port;

    BrokerInfo(String id, String host, int port) {
        this.id = id;
        this.host = host;
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getAddr() {
        return String.format("%s:%d", host, port);
    }

}
