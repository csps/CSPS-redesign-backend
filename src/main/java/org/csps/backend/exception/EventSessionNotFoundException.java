package org.csps.backend.exception;

public class EventSessionNotFoundException extends RuntimeException {
    
    public EventSessionNotFoundException(String message) {
        super(message);
    }
}
