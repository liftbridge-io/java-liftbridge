package io.liftbridge.exceptions;

/**
 * Thrown when a configured deadline was exceeded.
 */
public class DeadlineExceededException extends LiftbridgeException {

    public DeadlineExceededException(Exception cause) {
        super(cause);
    }

}
