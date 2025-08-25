package org.csps.backend.exception;

public class StudentAlreadyExistsException extends RuntimeException{
    public StudentAlreadyExistsException(String message) {
        super(message);
    }

    public StudentAlreadyExistsException(Long studentId) {
        super("Student with Student ID " + studentId + " already exists");
    }
}
