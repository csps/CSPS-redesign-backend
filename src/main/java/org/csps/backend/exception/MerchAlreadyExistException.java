package org.csps.backend.exception;

public class MerchAlreadyExistException extends RuntimeException{
    public MerchAlreadyExistException(String message) {
        super(message);
    }

}
