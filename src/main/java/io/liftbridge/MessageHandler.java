package io.liftbridge;

/**
 * {@code MessageHandler} is the callback invoked by {@link Client#subscribe} when a message is received on the
 * specified stream partition. If {@link MessageHandler#onError} is called, no more messages will be received.
 */
public interface MessageHandler {

    /**
     * Called when a message is received.
     *
     * @param msg received message
     */
    void onMessage(io.liftbridge.Message msg);

    /**
     * Called when an error has occurred on the stream. If this is called, no more messages will be received.
     *
     * @param t subscription error
     */
    void onError(Throwable t);

}
