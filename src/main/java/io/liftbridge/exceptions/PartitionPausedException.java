package io.liftbridge.exceptions;

/**
 * Thrown when subscribed to a partition that was paused.
 */
public class PartitionPausedException extends LiftbridgeException {

    public PartitionPausedException(String msg, Exception cause) {
        super(msg, cause);
    }

}
