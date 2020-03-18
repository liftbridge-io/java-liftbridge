package io.liftbridge.exceptions;

/**
 * Base Liftbridge exception.
 */
public class LiftbridgeException extends Exception {

    LiftbridgeException(String msg, Exception cause) {
        super(msg, cause);
    }

    LiftbridgeException(Exception cause) {
        super(cause);
    }

}
