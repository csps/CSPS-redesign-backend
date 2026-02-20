package org.csps.backend.exception;

public class ParticipantNotFoundException extends RuntimeException {
    
    public ParticipantNotFoundException(String message) {
        super(message);
    }
}
