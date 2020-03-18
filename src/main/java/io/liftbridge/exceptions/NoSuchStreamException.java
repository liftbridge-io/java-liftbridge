package io.liftbridge.exceptions;

/**
 * Thrown when trying to delete a stream that does not exist.
 */
public class NoSuchStreamException extends LiftbridgeException {

    public NoSuchStreamException(String msg, Exception cause) {
        super(msg, cause);
    }

}
