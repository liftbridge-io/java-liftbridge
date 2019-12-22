package io.liftbridge;

public interface MessageHandler {
    void onMessage(io.liftbridge.Message msg);
    void onError(Throwable t);
}
