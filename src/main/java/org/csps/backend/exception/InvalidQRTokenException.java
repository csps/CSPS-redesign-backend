package org.csps.backend.exception;

public class InvalidQRTokenException extends RuntimeException {
    
    public InvalidQRTokenException(String message) {
        super(message);
    }
}
