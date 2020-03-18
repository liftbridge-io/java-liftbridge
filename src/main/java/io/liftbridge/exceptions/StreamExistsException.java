package io.liftbridge.exceptions;

/**
 * Thrown when trying to create a stream that already exists.
 */
public class StreamExistsException extends LiftbridgeException {

    public StreamExistsException(String msg, Exception cause) {
        super(msg, cause);
    }

}
