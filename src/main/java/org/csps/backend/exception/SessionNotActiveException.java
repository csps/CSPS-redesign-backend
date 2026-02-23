package org.csps.backend.exception;

public class SessionNotActiveException extends RuntimeException {
    
    public SessionNotActiveException(String message) {
        super(message);
    }
}
