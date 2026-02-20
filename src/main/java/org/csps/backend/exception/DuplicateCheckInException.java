package org.csps.backend.exception;

public class DuplicateCheckInException extends RuntimeException {
    
    public DuplicateCheckInException(String message) {
        super(message);
    }
}
