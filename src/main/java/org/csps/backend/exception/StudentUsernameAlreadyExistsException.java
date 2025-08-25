package org.csps.backend.exception;

public class StudentUsernameAlreadyExistsException extends RuntimeException {
    public StudentUsernameAlreadyExistsException() {
        super();
    }

    public StudentUsernameAlreadyExistsException(String message) {
        super("Student with the username "+ message + " already exists");
    }
}
