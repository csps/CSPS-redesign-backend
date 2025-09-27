package org.csps.backend.exception;

public class PositionAlreadyTakenException extends RuntimeException {
    public PositionAlreadyTakenException(String message) {
        super(message);
    }

}
