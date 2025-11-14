package org.csps.backend.domain.dtos.request;

public class MerchAlreadyExistException extends RuntimeException{
    public MerchAlreadyExistException(String message) {
        super(message);
    }

}
