package org.csps.backend.exception;

public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(String message) {
        super(message);
    }

    public StudentNotFoundException(Long studentId) {
        super("Student with ID " + studentId + " not found");
    }
}
