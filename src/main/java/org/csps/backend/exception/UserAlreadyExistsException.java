package org.csps.backend.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(Long userId) {
        super("User with the User ID " + userId + " already exists");
    }
}
