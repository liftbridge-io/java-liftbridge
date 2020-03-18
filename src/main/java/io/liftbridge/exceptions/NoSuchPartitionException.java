package io.liftbridge.exceptions;

/**
 * Thrown when trying to subscribe to a stream partition that does not exist.
 */
public class NoSuchPartitionException extends LiftbridgeException {

    public NoSuchPartitionException(Exception cause) {
        super(cause);
    }

}
