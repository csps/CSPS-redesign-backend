package org.csps.backend.exception;

public class MerchNotFoundException extends RuntimeException {
    public MerchNotFoundException(String message) {
        super(message);
    }
}
