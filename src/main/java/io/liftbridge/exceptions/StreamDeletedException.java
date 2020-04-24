package io.liftbridge.exceptions;

/**
 * Thrown when subscribed to a stream that was deleted.
 */
public class StreamDeletedException extends LiftbridgeException {

    public StreamDeletedException(String msg, Exception cause) {
        super(msg, cause);
    }

}
